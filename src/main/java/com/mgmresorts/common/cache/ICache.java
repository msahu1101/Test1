package com.mgmresorts.common.cache;

import com.mgmresorts.common.cache.CacheValue.Status;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.lambda.Worker;

public interface ICache<T> {

    int DEFAULT_CACHE_EXPIRY = 30;
    int MAX_WRITE_LOCK_TIMEOUT = 3;
    int DEFAULT_PENDING_EXPIRY = 5;
    int CACHE_RECHECK_TIME_MILLIS = 100;

    default String formatKey(String key) {
        return name() + ":" + key;
    }

    default CacheValue<T> read(String key, Class<T> clazz) throws AppException {
        return read(key, false, clazz);
    }

    default CacheValue<T> create(String key, T value, Integer expire) throws AppException {
        return create(key, value, expire, Status.UPDATED);
    }

    default CacheValue<T> create(String key, T value) throws AppException {
        return create(key, value, Status.UPDATED);
    }

    static int maxRecheck() {
        return MAX_WRITE_LOCK_TIMEOUT * 1000 / CACHE_RECHECK_TIME_MILLIS - 1;
    }

    String name();

    CacheValue<T> read(String key, Boolean safeGet, Class<T> clazz) throws AppException;

    CacheValue<T> create(String key, T value, Integer expire, Status status) throws AppException;

    T singleThreadedGet(String key, Class<T> clazz, Worker<String, T> function) throws AppException;

    T nonBlockingGet(String key, Class<T> clazz, Worker<String, T> function) throws AppException;

    CacheValue<T> create(String key, T value, Status status) throws AppException;

}