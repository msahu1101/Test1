package com.mgmresorts.loyalty.data.support.nodb;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.Query;
import jakarta.persistence.TemporalType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;

public abstract class NonDatabaseQuery implements Query {
    private final Logger logger = Logger.get(NonDatabaseQuery.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final Class<?> clazz;
    private final String query;
    protected final List<Object> params = new ArrayList<>();

    public NonDatabaseQuery(Class<?> clazz, String query) {
        this.clazz = clazz;
        this.query = query;
    }

    @Override
    public List<?> getResultList() {
        if (clazz != null) {
            final InputStream resource = NonDatabaseQuery.class.getResourceAsStream("/canned-data/" + query + ".json");
            if (resource != null) {
                try {
                    final Class<?> array = Class.forName("[L" + clazz.getCanonicalName() + ";");
                    final Object[] readValue = (Object[]) mapper.readValue(resource, array);
                    return Arrays.asList(readValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        } else {
            logger.error("Unregistered entity...");
        }
        return null;
    }

    @Override
    public Object getSingleResult() {
        final List<?> resultList = getResultList();
        return Utils.isEmpty(resultList) ? resultList.get(0) : null;
    }

    @Override
    public int executeUpdate() {
        return 0;
    }

    @Override
    public Query setMaxResults(int maxResult) {
        return null;
    }

    @Override
    public int getMaxResults() {
        return 0;
    }

    @Override
    public Query setFirstResult(int startPosition) {
        return null;
    }

    @Override
    public int getFirstResult() {
        return 0;
    }

    @Override
    public Query setHint(String hintName, Object value) {
        return null;
    }

    @Override
    public Map<String, Object> getHints() {
        return null;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return null;
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return null;
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return null;
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return null;
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return false;
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return null;
    }

    @Override
    public Object getParameterValue(String name) {
        return null;
    }

    @Override
    public Object getParameterValue(int position) {
        return null;
    }

    @Override
    public FlushModeType getFlushMode() {
        return null;
    }

    @Override
    public LockModeType getLockMode() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public <T> Query setParameter(Parameter<T> param, T value) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(String name, Object value) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(String name, Date value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(int position, Object value) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setParameter(int position, Date value, TemporalType temporalType) {
        params.add(value);
        return this;
    }

    @Override
    public Query setFlushMode(FlushModeType flushMode) {
        return this;
    }

    @Override
    public Query setLockMode(LockModeType lockMode) {
        return this;
    }

}
