package com.mgmresorts.loyalty.data.support.nodb;

import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.entity.Offers;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.data.entity.PlayerGiftPoint;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.entity.TaxInformation;
import com.mgmresorts.loyalty.data.entity.Tier;
import com.mgmresorts.loyalty.data.support.nodb.NonDatabaseQueries.CannedStoredProcedureQuery;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class NonDatabaseEntityManager implements EntityManager {
    private final Map<String, Class<?>> entities = new HashMap<String, Class<?>>();

    public NonDatabaseEntityManager() {
        register(Balance.class);
        register(Offers.class);
        register(GiftPoints.class);
        register(PlayerGiftPoint.class);
        register(PlayerLink.class);
        register(StopCode.class);
        register(TaxInformation.class);
        register(Tier.class);
        register(PlayerComment.class);
        register(GiftBalances.class);
        register(SlotDollarBalance.class);
    }

    private void register(Class<?> clazz) {
        final NamedNativeQuery[] annotations = clazz.getAnnotationsByType(NamedNativeQuery.class);
        if (annotations != null) {
            for (NamedNativeQuery namedNativeQuery : annotations) {
                entities.put(namedNativeQuery.name(), namedNativeQuery.resultClass());
            }
        }
        final NamedStoredProcedureQuery[] annotations2 = clazz.getAnnotationsByType(NamedStoredProcedureQuery.class);

        if (annotations2 != null) {
            for (NamedStoredProcedureQuery namedNativeQuery : annotations2) {
                entities.put(namedNativeQuery.name(), namedNativeQuery.resultClasses()[0]);
            }
        }
        final NamedQuery[] annotations3 = clazz.getAnnotationsByType(NamedQuery.class);
        if (annotations3 != null) {
            for (NamedQuery namedNativeQuery : annotations3) {
                entities.put(namedNativeQuery.name(), clazz);
            }
        }
    }

    /*
     * We will override only used methods in the code base. In case of any new
     * method, please implement the same
     */
    @Override
    public Query createNativeQuery(String query) {
        return new NonDatabaseQueries.CannedFileQuery(entities.get(query), query);

    }

    @Override
    public Query createNativeQuery(String query, Class resultClass) {
        return new NonDatabaseQueries.CannedFileQuery(entities.get(query), query);

    }

    @Override
    public Query createNativeQuery(String query, String resultSetMapping) {
        return new NonDatabaseQueries.CannedFileQuery(entities.get(query), query);

    }

    @Override
    public Query createQuery(String query) {
        return new NonDatabaseQueries.CannedFileQuery(entities.get(query), query);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String query, Class<T> resultClass) {
        return new NonDatabaseQueries.CannedTypedQuery<T>(entities.get(query), query);

    }

    @Override
    public Query createNamedQuery(String query) {
        return new NonDatabaseQueries.CannedFileQuery(entities.get(query), query);

    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String query) {
        return new CannedStoredProcedureQuery(entities.get(query), query);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String query, Class<T> resultClass) {
        return new NonDatabaseQueries.CannedTypedQuery<T>(entities.get(query), query);

    }

    ///
    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }
    ///

    @Override
    public void persist(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T merge(T entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public void remove(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public FlushModeType getFlushMode() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void refresh(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void detach(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public boolean contains(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void setProperty(String propertyName, Object value) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public Map<String, Object> getProperties() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void joinTransaction() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public boolean isJoinedToTransaction() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public Object getDelegate() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");
    }

    @Override
    public EntityTransaction getTransaction() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public Metamodel getMetamodel() {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        throw new UnsupportedOperationException("Running in canned mode :: Operation not supported");

    }

}
