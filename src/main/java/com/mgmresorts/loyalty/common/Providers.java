package com.mgmresorts.loyalty.common;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.TargetServer;
import org.eclipse.persistence.jpa.PersistenceProvider;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.mgmresorts.common.concurrent.Executor;
import com.mgmresorts.common.concurrent.Executors;
import com.mgmresorts.common.concurrent.Pool;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.support.nodb.NonDatabaseEntityManager;
import com.microsoft.applicationinsights.core.dependencies.javaxannotation.Nullable;

public class Providers {

    public static class LmeDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "lme");
        }
    }

    public static class LmeEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("lme")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "lme");
        }
    }

    public static class PcisDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "pcis");
        }
    }

    public static class PcisEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("pcis")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "pcis");
        }
    }

    public static class MlifeDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "mlife");
        }
    }

    public static class MlifeEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("mlife")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "mlife");
        }
    }

    public static class WorkDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "work");
        }
    }

    public static class WorkEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("work")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "work");
        }
    }

    public static class ReportingDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "reporting");
        }
    }

    public static class ReportingEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("reporting")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "reporting");
        }
    }

    public static class PlayerDsProvider implements Provider<DataSource> {
        @Inject
        private Runtime runtime;

        public PoolingDataSource<PoolableConnection> get() {
            return buildDataSource(runtime, "player");
        }
    }

    public static class PlayerEmProvider implements Provider<EntityManager> {
        @Inject
        @Named("player")
        @Nullable
        private DataSource source;
        @Inject
        private Runtime runtime;

        @Override
        public EntityManager get() {
            return buildEntityManager(runtime, source, "player");
        }
    }

    public static class NonDatabaseEmProvider implements Provider<EntityManager> {
        public EntityManager get() {
            return new NonDatabaseEntityManager();
        }
    }

    public static class CustomerBalanceExecutorProvider implements Provider<Executor> {
        @Inject
        private Executors executors;

        @Override
        public Executor get() {
            return executors.get(Pool.getOrCreate("customer.balance.thread.pool"));
        }
    }

    private static final Map<DataSource, EntityManagerFactory> factory = new HashMap<>();

    private static synchronized EntityManager buildEntityManager(Runtime runtime, DataSource source, String prefix) {
        if (Boolean.parseBoolean(runtime.getConfiguration("application.backend.canned-mode", "false"))) {
            return null;
        }
        if (!factory.containsKey(source)) {
            final EntityManagerFactory createEntityManagerFactory = new PersistenceProvider().createEntityManagerFactory(prefix, ImmutableMap.<String, Object>builder() //
                    .put(PersistenceUnitProperties.TARGET_DATABASE, "SQLServer")//
                    .put(PersistenceUnitProperties.TRANSACTION_TYPE, PersistenceUnitTransactionType.RESOURCE_LOCAL.name())//
                    .put(PersistenceUnitProperties.LOGGING_LEVEL, "FINE")//
                    .put(PersistenceUnitProperties.LOGGING_TIMESTAMP, "false")//
                    .put(PersistenceUnitProperties.LOGGING_THREAD, "false")//
                    .put(PersistenceUnitProperties.LOGGING_SESSION, "false")//
                    .put(PersistenceUnitProperties.TARGET_SERVER, TargetServer.None)//
                    .put(PersistenceUnitProperties.NON_JTA_DATASOURCE, source)//
                    // .put(PersistenceUnitProperties.EXCLUSIVE_CONNECTION_MODE, "Always")//
                    .build());
            factory.put(source, createEntityManagerFactory);
        }

        return factory.get(source).createEntityManager();
    }

    private static PoolingDataSource<PoolableConnection> buildDataSource(Runtime runtime, String prefix) {
        final Logger logger = Logger.get(Providers.class);
        if (Boolean.parseBoolean(runtime.getConfiguration("application.backend.canned-mode", "false"))) {
            return null;
        }
        final String url = runtime.getConfiguration("database." + prefix + ".connection.url");
        final String user = runtime.getConfiguration("database." + prefix + ".connection.user");
        final String password = runtime.getConfiguration("database." + prefix + ".connection.password@secure");
        final int maximumConnections = runtime.getInt("database." + prefix + ".connection.max.total", 10);
        final int maximumIdleConnections = runtime.getInt("database." + prefix + ".connection.max.idle", 3);
        final ConnectionFactory factory = new DriverManagerConnectionFactory(url, user, password);
        final PoolableConnectionFactory poolableFactory = new PoolableConnectionFactory(factory, null);
        final GenericObjectPoolConfig<PoolableConnection> config = new GenericObjectPoolConfig<PoolableConnection>();
        config.setMaxTotal(maximumConnections);
        config.setMaxIdle(maximumIdleConnections);
        config.setTestOnReturn(true);
        config.setTestWhileIdle(true);
        config.setTestOnBorrow(true);
        config.setTimeBetweenEvictionRunsMillis(10000);
        final ObjectPool<PoolableConnection> pool = new GenericObjectPool<PoolableConnection>(poolableFactory, config) {
            @Override
            public PoolableConnection borrowObject() throws Exception {
                final PoolableConnection object = super.borrowObject();
                final int maxTotal = this.getMaxTotal();
                final int maxIdle = this.getMaxIdle();
                final int numActive = this.getNumActive();
                final int numIdle = this.getNumIdle();
                final int numWaiters = this.getNumWaiters();

                logger.debug("maxTotal : " + maxTotal + " maxIdle : " + maxIdle + " numActive : " + numActive + " numIdle : " + numIdle + " numWaiters : " + numWaiters);
                return object;
            }

        };
        logger.trace("Pool Size : " + pool);
        poolableFactory.setPool(pool);
        return new PoolingDataSource<PoolableConnection>(pool);
    }
}
