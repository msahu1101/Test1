package com.mgmresorts.loyalty.data.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import javax.sql.DataSource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.telemetry.TelemetryRecorder;
import com.mgmresorts.loyalty.data.entity.TaxInformation;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.data.support.SqlSupport.DB;

class TaxInfoAccessTest {
    @Mock
    private TelemetryRecorder recorder;

    @Mock
    private EntityManager workManager;

    @Mock
    private SqlSupport<TaxInformation, String> sqlSupportMock;

    @Mock
    private Connection connectionMock;

    @Mock
    Runtime runtime;

    @InjectMocks
    private TaxInfoAccess customerTaxInfoAccess = new TaxInfoAccess() {
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
    public void testreadPatronTaxInfoCustomerResponse() throws AppException, SQLException {
        List<TaxInformation> result = null;
        List<TaxInformation> taxInfoList = new ArrayList<>();
        TaxInformation taxInfo = new TaxInformation();
        taxInfo.setResult("");
        taxInfoList.add(taxInfo);
        DataSource dataSource = Mockito.mock(DataSource.class);
        StoredProcedureQuery storedProcedureQueryMock = Mockito.mock(StoredProcedureQuery.class);
        when(workManager.createNamedStoredProcedureQuery(Mockito.anyString())).thenReturn(storedProcedureQueryMock);
        when(dataSource.getConnection()).thenReturn(connectionMock);
        when(storedProcedureQueryMock.getFirstResult()).thenReturn(1);
        result = customerTaxInfoAccess.readPatronTaxInfoCustomerResponse(12345678, 2019, 4);
        customerTaxInfoAccess.getEntityType();
        customerTaxInfoAccess.callByNamedProcedure(DB.WORK, "", TaxInformation.class, (ps) -> {
            return;
        });
        assertNotNull(result);
    }

    @Test
    public void testGetTaxInfoException() throws AppException {
        Assertions.assertThrows(Exception.class, () -> {
            customerTaxInfoAccess.readPatronTaxInfoCustomerResponse(12345678, 2019, 4);
        });
    }

}
