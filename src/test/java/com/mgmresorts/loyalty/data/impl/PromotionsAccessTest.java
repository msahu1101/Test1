package com.mgmresorts.loyalty.data.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.telemetry.TelemetryRecorder;
import com.mgmresorts.loyalty.data.entity.Offers;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.data.support.SqlSupport.DB;

class PromotionsAccessTest {
    @Mock
    private TelemetryRecorder recorder;

    @Mock
    private EntityManager workManager;

    @Mock
    private SqlSupport<List<StopCode>, String> sqlSupportMock;

    @Mock
    private Connection connectionMock;

    @Mock
    Runtime runtime;

    @InjectMocks
    private PromotionsAccess customerPromoCodeAccess = new PromotionsAccess() {
        @Override
        protected EntityManager entityManager(DB db) {
            return workManager;
        }
    };

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testReadPatronCustPromotions() throws AppException, SQLException {
        List<Offers> result = null;
        DataSource dataSource = Mockito.mock(DataSource.class);
        StoredProcedureQuery StoredProcedureQueryMock = Mockito.mock(StoredProcedureQuery.class);
        when(workManager.createNamedStoredProcedureQuery(Mockito.anyString())).thenReturn(StoredProcedureQueryMock);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(StoredProcedureQueryMock.getFirstResult()).thenReturn(1);
        result = customerPromoCodeAccess.readPatronCustPromotions(3485384);
        customerPromoCodeAccess.getEntityType();
        customerPromoCodeAccess.callByNamedProcedure(DB.WORK, "", StopCode.class, (ps) -> {
            return;
        });
        assertNotNull(result);
    }

}
