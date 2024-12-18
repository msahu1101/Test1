/**
 * 
 */
package com.mgmresorts.loyalty.data.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
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
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.support.SqlSupport;

class LinkedPlayerInfoAccessTest {
    @Mock
    private TelemetryRecorder recorder;

	@Mock
	private EntityManager playerManager;

	@Mock
	private SqlSupport<PlayerLink, String> sqlSupportMock;

	@Mock
	private Connection connectionMock;

	@Mock
	Runtime runtime;

	@InjectMocks
	private LinkedPlayerInfoAccess access = new LinkedPlayerInfoAccess() { 
	@Override
	protected EntityManager entityManager(DB db) {
	    return playerManager;
	}
	};

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testreadPatronPlayerLinkResponse() throws AppException, SQLException {
		Query mockQuery = Mockito.mock(Query.class);
		List<PlayerLink> playerInfoList = new ArrayList<>();
		DataSource dataSource = Mockito.mock(DataSource.class);
		@SuppressWarnings("rawtypes")
		TypedQuery typedQueryMock = Mockito.mock(TypedQuery.class);
		when(playerManager.createQuery(Mockito.anyString(), Mockito.any())).thenReturn(typedQueryMock);
		when(dataSource.getConnection()).thenReturn(connectionMock);
		when(typedQueryMock.getResultList()).thenReturn(playerInfoList);
		
		
		
		Mockito.doAnswer(invocation -> {
			return mockQuery;
		}).when(playerManager).createNamedQuery(Mockito.any());

		Mockito.doAnswer(invocation -> {
			List<Object[]> results = new ArrayList<>();
			results.add(new Object[] { "79145357", "79145357"});
			results.add(new Object[] { "79258482", "79145357",});
			results.add(new Object[] { "79258482", "79145357",});
			return results;
		}).when(mockQuery).getResultList();
		
		List<PlayerLink> result = access.getLinkedPlayerInfo(12345678);
		access.getEntityType();
		assertNotNull(result);
	}

	@Test
	public void testPlayerLinkException() throws AppException {
		Assertions.assertThrows(Exception.class, () -> {
			access.getLinkedPlayerInfo(12345678);
		});
	}

}