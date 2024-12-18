package com.mgmresorts.loyalty.data.support;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.mgmresorts.common.errors.HttpStatus;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.common.lambda.Worker;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.telemetry.Execution;
import com.mgmresorts.common.telemetry.Telemetry;
import com.mgmresorts.common.telemetry.TelemetryRecorder;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.data.support.Telemetry.MssqlDependency;
import com.mgmresorts.loyalty.errors.Errors;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.queries.SQLCall;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public abstract class SqlSupport<T, P> {

    private final Logger logger = Logger.get(SqlSupport.class);

    @FunctionalInterface
    public interface ListMapper<T> {
        List<T> call(final List<Object[]> resultList);
    }

    public enum DB {
        LME, PCIS, MLIFE, WORK, PLAYER, REPORTING
    }

    @Inject
    @Named("no-backend")
    protected EntityManager noBackendManager;

    @Inject
    protected com.mgmresorts.common.config.Runtime runtime;

    @Inject
    private TelemetryRecorder recorder;

    public final void create(T element) throws AppException {
        throw new UnsupportedOperationException();
    }

    public final void update(T element) throws AppException {
        throw new UnsupportedOperationException();
    }

    public final void delete(T element) throws AppException {
        throw new UnsupportedOperationException();
    }

    public final T read(DB db, P pk) throws AppException {
        final Execution execution = Execution.begin();
        final EntityManager entityManager = entityManager(db);
        T out = null;
        try {
            out = entityManager.find(getEntityType(), pk);
            execution.success(HttpStatus.OK.value());
        } finally {
            if (execution.getCode() == -1) {
                execution.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error on sql.read()");
            }
            recorder.recordDependency(Telemetry.createDependency(db.name(), MssqlDependency.init(db), execution.getDuration(), execution.getCode(), execution.isSuccess()));
        }
        return out;
    }

    protected abstract Class<T> getEntityType();

    public final List<T> findByNativeQuery(DB db, String nativeQuery, Consumer<Query> parameter, ListMapper<T> resultList) throws AppException {
        return doInTransaction((manager) -> {
            final Query query = manager.createNativeQuery(nativeQuery);
            parameter.accept(query);
            List<T> list = resultList.call(query.getResultList());
            logger.debug("findByNativeQuery Response: ");
            if (list != null) {
                if (list.isEmpty()) {
                    logger.debug("Empty Response");
                }
            } else {
                logger.debug("NULL/No Response");
            }
            return list;
        }, db, nativeQuery);
    }

    public final List<T> findByNamedQuery(DB db, String name, Consumer<Query> parameter) throws AppException {
        return doInTransaction((manager) -> {
            final Query query = manager.createNamedQuery(name);
            parameter.accept(query);
            List<T> resultList = query.getResultList();
            if (resultList != null) {
                if (resultList.isEmpty()) {
                    logger.debug("Empty Response");
                }
            } else {
                logger.debug("NULL/No Response");
            }
            return resultList;
        }, db, name);
    }

    public final List<T> callByNamedProcedure(DB db, String namedProcedure, Class<?> mapping, Consumer<StoredProcedureQuery> parameter) throws AppException {
        return doInTransaction((manager) -> {
            final StoredProcedureQuery query = manager.createNamedStoredProcedureQuery(namedProcedure);
            parameter.accept(query);
            List<T> resultList = query.getResultList();
            logger.debug("callByNamedProcedure Response: ");
            if (resultList != null) {
                if (resultList.isEmpty()) {
                    logger.debug("Empty Response");
                }
            } else {
                logger.debug("NULL/No Response");
            }
            return resultList;
        }, db, namedProcedure);
    }

    public final List<T> callProcByQuery(DB db, String nativeProcedure, Class<?> mapping, Consumer<StoredProcedureQuery> parameter) throws AppException {
        return doInTransaction((manager) -> {
            final EntityManagerImpl impl = (EntityManagerImpl) manager.getDelegate();
            final Query createQuery = impl.createQuery(new SQLCall(nativeProcedure));
            final List<T> list = createQuery.getResultList();
            return list;

        }, db, nativeProcedure);
    }
    
    public final T call(DB db, String nativeQuery,String nativeProcedure, Class<?> mapping, Consumer<StoredProcedureQuery> parameter, Function<Query, T> result)
            throws AppException {
        return doInTransaction((manager) -> {
            Query createNativeQuery = manager.createNativeQuery(nativeQuery, mapping);
            return result.apply(createNativeQuery);
        }, db, nativeProcedure);
    }
    
    
    
    
    public static final List<Integer> getDatabseErrorCodes(Exception ex) throws AppException {
        final List<Integer> error = new ArrayList<>();
        Throwable aex = ex;
        while (aex != null && aex.getCause() != null && aex.getCause().getClass() != aex.getClass()) {
            aex = aex.getCause();
            if (aex instanceof DatabaseException) {
                DatabaseException cause = (DatabaseException) aex;
                error.add(cause.getErrorCode());
            } else if (aex instanceof SQLServerException) {
                SQLServerException cause = (SQLServerException) aex;
                error.add(cause.getErrorCode());
            } else if (aex instanceof SQLException) {
                SQLServerException cause = (SQLServerException) aex;
                error.add(cause.getErrorCode());
            }
        }
        return error;
    }

    private <R> R doInTransaction(Worker<EntityManager, R> worker, DB db, String name) throws AppException {
        final Execution execution = Execution.begin();
        final EntityManager manager = entityManager(db);
        R apply = null;
        try {
            apply = worker.apply(manager);
            execution.success(HttpStatus.OK.value());
            return apply;
        } catch (Exception e) {
            logger.error("Database error {}", e);
            throw new AppException(Errors.UNABLE_TO_CALL_BACKEND, e, e.getMessage());
        } finally {
            manager.close();
            if (execution.getCode() == -1) {
                execution.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error in sql.doInTransaction");
            }
            recorder.recordDependency(
                    Telemetry.createDependency(db.name() + "(" + name + ")", MssqlDependency.init(db), execution.getDuration(), execution.getCode(), execution.isSuccess()));

        }
    }

    protected EntityManager entityManager(DB db) {
        if (runtime != null && Boolean.parseBoolean(runtime.getConfiguration("application.backend.canned-mode", "false"))) {
            return noBackendManager;
        }
        final InjectionContext injectionContext = InjectionContext.get();
        switch (db) {
        case LME:
            return injectionContext.instanceOf(EntityManager.class, Names.named("lme"));
        case PCIS:
            return injectionContext.instanceOf(EntityManager.class, Names.named("pcis"));
        case MLIFE:
            return injectionContext.instanceOf(EntityManager.class, Names.named("mlife"));
        case WORK:
            return injectionContext.instanceOf(EntityManager.class, Names.named("work"));
        case PLAYER:
            return injectionContext.instanceOf(EntityManager.class, Names.named("player"));
        case REPORTING:
            return injectionContext.instanceOf(EntityManager.class, Names.named("reporting"));
        default:
            throw new AppRuntimeException(Errors.UNEXPECTED_SYSTEM);
        }
    }
}
