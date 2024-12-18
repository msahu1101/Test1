package com.mgmresorts.common.cache;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;

import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.logging.Logger;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Named
public class RedisCluster extends JedisCluster {
    private static final Logger logger = Logger.get(RedisCluster.class);

    public RedisCluster() {
        super(new HostAndPort(host(), port()), timeout(), timeout(), 3, password(), "jedis-client", config(), true, socketFactory(), null, null, null);
    }

    private static SSLSocketFactory socketFactory() {
        try {
            final TrustManager[] unTrustManagers = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    logger.warn("Ignoring trusted client check...");
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    logger.warn("Ignoring trusted server check...");
                }
            } };
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, unTrustManagers, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            logger.error("Error in ssl factory setup {}", e);
            return null;
        }
    }

    private static String host() {
        final String configuration = Runtime.get().getConfiguration("customer.cache.host");
        logger.debug("Redis host resolved as: {}", configuration);
        return configuration;
    }

    private static int port() {
        final int port = Runtime.get().getInt("customer.cache.port", -1);
        logger.debug("Redis port resolved as: {}", port);
        return port;
    }

    private static String password() {
        return Runtime.get().getConfiguration("customer.cache.password");
    }

    private static int timeout() {
        return Runtime.get().getInt("customer.cache.read.timeout", -1);
    }

    private static JedisPoolConfig config() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setJmxNamePrefix("jedis");
        poolConfig.setMaxTotal(50);
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}
