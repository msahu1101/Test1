package com.mgmresorts.loyalty.data.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.data.support.SqlSupport.DB;

class StopCodeAccessTest {
    @Mock
    private TelemetryRecorder recorder;

    @Mock
    private EntityManager playerManager;

    @Mock
    private SqlSupport<List<StopCode>, String> sqlSupportMock;

    @Mock
    private Connection connectionMock;

    @Mock
    Runtime runtime;

    @InjectMocks
    private StopCodeAccess customerStopCodeAccess = new StopCodeAccess() {
        @Override
        protected EntityManager entityManager(DB db) {
            return playerManager;
        }
    };

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetStopCode() throws AppException, SQLException {
        List<StopCode> result = null;
        List<StopCode> stopCodes = new ArrayList<>();
        StopCode stopCode = new StopCode();
        stopCode.setDescription("test");
        stopCodes.add(stopCode);
        DataSource dataSource = Mockito.mock(DataSource.class);
        StoredProcedureQuery StoredProcedureQueryMock = Mockito.mock(StoredProcedureQuery.class);
        when(playerManager.createNamedStoredProcedureQuery(Mockito.anyString())).thenReturn(StoredProcedureQueryMock);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(StoredProcedureQueryMock.getFirstResult()).thenReturn(1);
        result = customerStopCodeAccess.getStopCode(535345);
        customerStopCodeAccess.getEntityType();
        customerStopCodeAccess.callByNamedProcedure(DB.PLAYER, "", StopCode.class, (ps) -> {
            return;
        });
        assertNotNull(result);
    }

    @Test
    public void testGetStopCodeException() throws AppException {
        List<StopCode> result = null;
        try {
            result = customerStopCodeAccess.getStopCode(535345);
        } catch (Exception e) {
            e.getMessage();
        }
        assertNull(result);
    }

}
