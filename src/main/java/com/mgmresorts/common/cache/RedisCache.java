package com.mgmresorts.common.cache;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

import javax.inject.Inject;

import com.mgmresorts.common.cache.CacheValue.Status;
import com.mgmresorts.common.concurrent.Executor;
import com.mgmresorts.common.concurrent.Executors;
import com.mgmresorts.common.concurrent.Pool;
import com.mgmresorts.common.concurrent.Result;
import com.mgmresorts.common.concurrent.Task;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.lambda.Worker;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.JSonMapper;
import com.mgmresorts.common.utils.Utils;

import redis.clients.jedis.JedisCluster;

public abstract class RedisCache<T> implements ICache<T> {

    private final Logger logger = Logger.get(RedisCache.class);
    private final StampedLock lock = new StampedLock();
    private final Executors instance = Executors.instance();
    protected final JSonMapper mapper = new JSonMapper();

    @Inject
    private Optional<JedisCluster> resource;

    @Inject
    private Runtime runtime;

    public static <E> RedisCache<E> as(Class<E> clazz, String name) {
        return new RedisCache<E>() {
            public String name() {
                return name;
            }
        };
    }

    private CacheValue<T> buildValue(T value, Integer expire, Status status) {
        final CacheValue<T> cached = new CacheValue<T>();
        if (expire != null && expire != 0) {
            cached.setExpire(LocalDateTime.now().plusSeconds(expire).getNano());
        }
        cached.setVersion(0);
        cached.setPayloadString(value instanceof String ? (String) value : mapper.writeValueAsString(value));
        cached.setPayload(value);
        cached.setStatus(status);
        cached.setOwn(true);
        return cached;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final CacheValue<T> read(String key, Boolean safeGet, Class<T> clazz) throws AppException {
        if (!resource.isPresent()) {
            return null;
        }
        CacheValue<T> out = null;
        final String redisKey = ICache.super.formatKey(key);
        out = buildValue(null, DEFAULT_CACHE_EXPIRY, Status.PENDING);
        final Long action = resource.get().setnx(redisKey, mapper.writeValueAsString(out));
        if (action > 0) {
            out.setOwn(true);
            resource.get().expire(redisKey, DEFAULT_PENDING_EXPIRY);
        } else {
            final String string = resource.get().get(redisKey);
            out = mapper.readValue(string, CacheValue.class);
            if (!Utils.isEmpty(out.getPayloadString())) {
                out.setPayload(clazz.isAssignableFrom(String.class) ? (T) out.getPayloadString() : mapper.readValue(out.getPayloadString(), clazz));
            }
        }
        return out;
    }

    @Override
    public final CacheValue<T> create(String key, T value, Integer expire, Status status) throws AppException {
        CacheValue<T> cached = null;
        final String redisKey = ICache.super.formatKey(key);
        cached = buildValue(value, expire, status);
        resource.get().setex(redisKey, expire, mapper.writeValueAsString(cached));
        logger.trace("Entry added in cache for {}", key);
        return cached;

    }

    @Override
    public final CacheValue<T> create(String key, T value, Status status) throws AppException {
        CacheValue<T> cached = null;
        final String redisKey = ICache.super.formatKey(key);
        cached = buildValue(value, DEFAULT_CACHE_EXPIRY, status);
        resource.get().setex(redisKey, DEFAULT_CACHE_EXPIRY, mapper.writeValueAsString(cached));
        logger.trace("Entry added in cache for {}", key);
        return cached;

    }


    @Override
    public T nonBlockingGet(String key, Class<T> clazz, Worker<String, T> function) throws AppException {

        final boolean disabledGlobally = Boolean.valueOf(Runtime.get().getConfiguration("cache.disabled.global"));
        final boolean disabledSpecific = Boolean.valueOf(Runtime.get().getConfiguration("cache.disabled." + name()));

        if (disabledGlobally || disabledSpecific) {
            logger.trace("Not using cache");
            return function.apply(key);
        } else {
            logger.trace("Using cache");
        }

        final Executor service = instance.get(Pool.getOrCreate("redis-client-thread-pool"));
        T out = null;
        final Result<T> submit = service.invoke(new Task<T>() {
            @Override
            protected T execute() throws Exception {
                return loop(key, clazz, function, 1);
            }

            private T loop(String key, Class<T> clazz, Worker<String, T> function, int retry) throws Exception {
                CacheValue<T> read = read(key, true, clazz);
                if (read.isOwn() && read.getStatus() == Status.PENDING) {
                    logger.trace("No cache data found. going to source to get it with key ... {}", key);
                    final T value = function.apply(key);
                    create(key, value, runtime.getInt("cache.expiry." + name(), DEFAULT_CACHE_EXPIRY));
                    return value;
                } else if (!read.isOwn() && read.getStatus() == Status.PENDING) {
                    if (retry < ICache.maxRecheck()) {
                        logger.trace("One thread went to source system. waiting for {}. retry check {}", key, retry);
                        Thread.sleep(CACHE_RECHECK_TIME_MILLIS);
                        return loop(key, clazz, function, retry + 1);
                    } else {
                        throw new Exception("Timeout in waiting for cache to be updated");
                    }
                } else {
                    logger.trace("Found data in cache. returning data for ... {}", key);
                    return read.getPayload();
                }
            }

        });
        if (submit.getThrowable() != null) {
            logger.debug("timeout in getting from cache {}", submit.getThrowable().getMessage());
            try {
                out = function.apply(key);
            } catch (Exception e) {
                if (e instanceof AppException) {
                    throw (AppException) e;
                } else if (e.getCause() instanceof AppException) {
                    throw (AppException) e.getCause();
                } else {
                    throw new AppException(SystemError.UNEXPECTED_SYSTEM, e.getCause());
                }
            }
            create(key, out, runtime.getInt(name() + ".expiry." + name(), DEFAULT_CACHE_EXPIRY));
        } else {
            out = submit.getOutput();
        }
        return out;

    }

    /**
     * This method is a reference one. This works in a single threaded model. Be
     * sure about the use cases if you are planning to use this #nonBlockingGet()
     */

    @Override
    public T singleThreadedGet(String key, Class<T> clazz, Worker<String, T> function) throws AppException {
        long stamped = -1;
        T out = null;
        try {
            try {
                stamped = lock.tryWriteLock(MAX_WRITE_LOCK_TIMEOUT, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            final CacheValue<T> read = read(key, true, clazz);
            if (read.getStatus() == Status.PENDING) {
                logger.debug("Going to source to get data {}", key);
                final T value = function.apply(key);
                out = create(key, value, runtime.getInt(name() + ".expiry." + name(), DEFAULT_CACHE_EXPIRY)).getPayload();
            } else {
                out = read.getPayload();
            }

        } finally {
            if (stamped != -1) {
                lock.unlock(stamped);
            }
        }
        return out;
    }

}
