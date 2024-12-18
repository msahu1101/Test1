package com.mgmresorts.loyalty.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.lambda.Worker;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.impl.LinkedPlayerInfoAccess;
import com.mgmresorts.loyalty.data.to.LinkedPlayersWrapper;
import com.mgmresorts.loyalty.dto.customer.LinkedPlayer;
import com.mgmresorts.loyalty.dto.services.LinkedPlayersResponse;
import com.mgmresorts.loyalty.service.IService;
import com.mgmresorts.loyalty.transformer.LinkedPlayerTransformer;

class LinkedPlayerInfoServiceTest {

	@Mock
	private Runtime runtime;

	@Mock
	private LinkedPlayerInfoAccess accessMock;

	@InjectMocks
	private LinkedPlayerInfoService service;

	@Mock
	Thread threadMock;

	@Mock
	private ICache<LinkedPlayersWrapper> mockCache;

	@Mock
	IService iServiceMock;

	@Mock
	LinkedPlayerTransformer linkedPlayerTransformerMock;

	@Mock
	LinkedPlayersWrapper linkedPlayersWrapper;

	List<LinkedPlayer> linkedPlayers;

	LinkedPlayersResponse linkedPlayersResponse;

	@BeforeEach
	public void setUp() throws Exception {

		linkedPlayersResponse = new LinkedPlayersResponse();
		List<LinkedPlayer> linkedPlayers = new ArrayList<>();
		LinkedPlayer linkPlayer2 = new LinkedPlayer();
		linkPlayer2.setLinkNumber(87654321);
		linkPlayer2.setPlayerId(12345678);
		linkedPlayers.add(linkPlayer2);
		linkedPlayersResponse.setLinkedPlayers(linkedPlayers);

		MockitoAnnotations.initMocks(this);

	}

	private LinkedPlayersWrapper getLinkedPlayersWrapper() {

		LinkedPlayersWrapper linkedPlayersWrapper = new LinkedPlayersWrapper();
		List<PlayerLink> linkedPlayers = new ArrayList<>();
		PlayerLink linkPlayer = new PlayerLink();
		linkPlayer.setLinkNumber(87654321);
		linkPlayer.setPlayerId(12345678);
		linkedPlayers.add(linkPlayer);
		linkedPlayersWrapper.setLinkedPlayers(linkedPlayers);
		return linkedPlayersWrapper;
	}

	@Test
	public void getLinkedPlayerTest() throws AppException {
		String playerId = "12348765";
		List<PlayerLink> playerLinkList = new ArrayList<>();
		PlayerLink playerLink = new PlayerLink();
		playerLink.setPlayerId(12345678);
		playerLink.setLinkNumber(87654321);
		playerLinkList.add(playerLink);
		when(accessMock.getLinkedPlayerInfo(Mockito.anyInt())).thenReturn(playerLinkList);
		Mockito.doAnswer(invocation -> {
			final Worker<String, ?> lambda = invocation.getArgument(2);
			return lambda.apply("");
		}).when(mockCache).nonBlockingGet(anyString(), any(), any());
		when(linkedPlayerTransformerMock.toLeft(any())).thenReturn(linkedPlayersResponse.getLinkedPlayers());
		LinkedPlayersResponse linkedPlayersResponse = service.getLinkedPlayerInfo(playerId);
		assertEquals(playerLink.getPlayerId(), linkedPlayersResponse.getLinkedPlayers().get(0).getPlayerId());
		assertEquals(playerLink.getLinkNumber(), linkedPlayersResponse.getLinkedPlayers().get(0).getLinkNumber());
	}

	@Test
	public void getLinkedPlayerWithNoMembersTest() throws AppException {
		when(mockCache.nonBlockingGet(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(linkedPlayersWrapper);
		Assertions.assertThrows(AppException.class, () -> {
			service.getLinkedPlayerInfo("1234");
		});
	}

	@Test
	public void getLinkedPlayersNonBlockingGetTest() throws AppException {
		String playerId = "12348765";
		List<PlayerLink> playerLinkList = new ArrayList<>();
		PlayerLink playerLink = new PlayerLink();
		playerLink.setPlayerId(12345678);
		playerLink.setLinkNumber(87654321);
		playerLinkList.add(playerLink);
		when(accessMock.getLinkedPlayerInfo(Mockito.anyInt())).thenReturn(playerLinkList);
		when(mockCache.nonBlockingGet(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(getLinkedPlayersWrapper());
		when(linkedPlayerTransformerMock.toLeft(any())).thenReturn(linkedPlayersResponse.getLinkedPlayers());
		LinkedPlayersResponse linkedPlayersResponse = service.getLinkedPlayerInfo(playerId);
		assertEquals(playerLink.getPlayerId(), linkedPlayersResponse.getLinkedPlayers().get(0).getPlayerId());
		assertEquals(playerLink.getLinkNumber(), linkedPlayersResponse.getLinkedPlayers().get(0).getLinkNumber());
	}

	@Test
	public void getLinkedPlayersExceptionTest() throws AppException {
		Assertions.assertThrows(AppException.class, () -> {
			service.getLinkedPlayerInfo("str");
		});
	}
}
