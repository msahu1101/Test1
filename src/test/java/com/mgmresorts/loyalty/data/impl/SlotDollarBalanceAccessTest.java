package com.mgmresorts.loyalty.data.impl;

import com.mgmresorts.common.errors.HttpStatus;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class SlotDollarBalanceAccessTest {
    @Mock
    private DataSource workDataSource;

    @Mock
    private Connection connectionMock;

    @Mock
    private CallableStatement callableStatementMock;

    @Mock
    private ResultSet resultSetMock;

    @InjectMocks
    private SlotDollarBalanceAccess slotDollarBalanceAccess;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getSlotDollarBalance_happyPath_returnsResponse() throws AppException, SQLException {
        when(workDataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
        when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(eq(1))).thenReturn(54321);

        SlotDollarBalance actualEntity = slotDollarBalanceAccess.getSlotDollarBalance("12345");

        assertNotNull(actualEntity);
        assertEquals("12345", actualEntity.getPlayerId(), "Verify playerId");
        assertEquals(54321, actualEntity.getSlotDollars(), "Verify slotDollars");
    }

    @Test
    public void getSlotDollarBalance_playerNotFound_returnsNull() throws AppException, SQLException {
        String crmAcresXml = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\">"
            + "<Header><OriginalMessageID>0</OriginalMessageID><TimeStamp>2022-08-12T01:35:16.137</TimeStamp><Operation Data=\"Error\" Operand=\"Error\"/></Header>"
            + "<Body><Error><ErrorCode>INVALIDPLAYERID</ErrorCode><ErrorDescription>Proc_GetPlayer_PointBalance: PlayerID / CardID is Invalid or not active</ErrorDescription>"
            + "</Error></Body></CRMAcresMessage>";

        when(workDataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
        when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(eq(1))).thenThrow(new SQLException("Expected int"));
        when(resultSetMock.getString(eq(1))).thenReturn(crmAcresXml);

        SlotDollarBalance actualEntity = slotDollarBalanceAccess.getSlotDollarBalance("12345");

        assertNull(actualEntity);
    }

    @Test
    public void getSlotDollarBalance_resultSetThrows_throwsException() throws SQLException {
        when(workDataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
        when(callableStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenThrow(new RuntimeException("generic exception"));

        AppException thrownException = assertThrows(AppException.class, () -> {
            SlotDollarBalance actualEntity = slotDollarBalanceAccess.getSlotDollarBalance("12345");
        });

        assertEquals("Unable to fetch data from patron: generic exception", thrownException.getDescription(), "description");
        assertNull(thrownException.getMessage(), "message");
        assertEquals("101-0-1020", thrownException.getDisplayCode(), "display code");
        assertEquals(HttpStatus.BAD_GATEWAY, thrownException.getHttpStatus(), "httpStatus");
    }

    @Test
    public void getSlotDollarBalance_callableStatementThrows_throwsException() throws SQLException {
        when(workDataSource.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareCall(anyString())).thenReturn(callableStatementMock);
        when(callableStatementMock.executeQuery()).thenThrow(new RuntimeException("generic exception"));

        AppException thrownException = assertThrows(AppException.class, () -> {
            SlotDollarBalance actualEntity = slotDollarBalanceAccess.getSlotDollarBalance("12345");
        });

        assertEquals("Unable to fetch data from patron: generic exception", thrownException.getDescription(), "description");
        assertNull(thrownException.getMessage(), "message");
        assertEquals("101-0-1020", thrownException.getDisplayCode(), "display code");
        assertEquals(HttpStatus.BAD_GATEWAY, thrownException.getHttpStatus(), "httpStatus");
    }

}
