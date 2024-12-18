package com.mgmresorts.loyalty.data.support.nodb;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Parameter;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TypedQuery;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class NonDatabaseQueries {

    public static class CannedFileQuery extends NonDatabaseQuery {

        public CannedFileQuery(Class<?> clazz, String query) {
            super(clazz, query);
        }
    }

    public static class CannedStoredProcedureQuery extends CannedFileQuery implements StoredProcedureQuery {

        public CannedStoredProcedureQuery(Class<?> clazz, String query) {
            super(clazz, query);
        }

        @Override
        public StoredProcedureQuery setHint(String hintName, Object value) {
            return null;
        }

        @Override
        public <T> StoredProcedureQuery setParameter(Parameter<T> param, T value) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Object value) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(String name, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Object value) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setParameter(int position, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public StoredProcedureQuery setFlushMode(FlushModeType flushMode) {
            return null;
        }

        @Override
        public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
            return null;
        }

        @Override
        public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
            return null;
        }

        @Override
        public Object getOutputParameterValue(int position) {
            return null;
        }

        @Override
        public Object getOutputParameterValue(String parameterName) {
            return null;
        }

        @Override
        public boolean execute() {
            return false;
        }

        @Override
        public boolean hasMoreResults() {
            return false;
        }

        @Override
        public int getUpdateCount() {

            return 0;
        }

    }

    public static class CannedTypedQuery<T> extends CannedFileQuery implements TypedQuery<T> {

        public CannedTypedQuery(Class<?> clazz, String query) {
            super(clazz, query);
        }

        @Override
        public <E> E getParameterValue(Parameter<E> param) {
            return null;
        }

        @Override
        public List<T> getResultList() {
            return (List<T>) super.getResultList();
        }

        @Override
        public T getSingleResult() {
            return null;
        }

        @Override
        public TypedQuery<T> setMaxResults(int maxResult) {
            return null;
        }

        @Override
        public TypedQuery<T> setFirstResult(int startPosition) {
            return null;
        }

        @Override
        public TypedQuery<T> setHint(String hintName, Object value) {
            return null;
        }

        @Override
        public TypedQuery<T> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(String name, Object value) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(String name, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(String name, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(int position, Object value) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(int position, Calendar value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setParameter(int position, Date value, TemporalType temporalType) {
            params.add(value);
            return this;
        }

        @Override
        public TypedQuery<T> setFlushMode(FlushModeType flushMode) {
            return null;
        }

        @Override
        public TypedQuery<T> setLockMode(LockModeType lockMode) {
            return null;
        }

        @Override
        public <E> TypedQuery<T> setParameter(Parameter<E> param, E value) {
            params.add(value);
            return this;
        }

    }
}
