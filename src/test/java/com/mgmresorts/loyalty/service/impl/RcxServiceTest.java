package com.mgmresorts.loyalty.service.impl;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.HttpService;
import com.mgmresorts.loyalty.dto.services.RcxPendingBalanceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class RcxServiceTest {
    /*
        Expectations:
            - Caller verifies that playerId parameter is a valid value.
     */

    @InjectMocks
    @Spy
    private RcxService rcxService;

    @Mock
    HttpService httpService;

    private final String playerId = "12345";
    private final String baseUrlRcxPlatform = "https://mgm-nonprod-dev.apigee.net/rcxapi2";
    private final String baseUrlRcxDb = "https://mgm-nonprod-dev.apigee.net/rcxapi/members";

    private final String uriPendingBalance = "/%s/profile?select=purses.balance,purses.name";
    private final String uriPlayerBalance = "/extapi/v1/members/%s/profile";
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    // @Test
    void getPendingPointsBalance_happyPath_makesCall() throws AppException, HttpFailureException {
        Assertions.assertNotNull(rcxService);
        doReturn(new RcxPendingBalanceResponse()).when(rcxService).makeRequest(anyString(), same(RcxPendingBalanceResponse.class), anyString());
        rcxService.rcxGetPendingPoints(playerId);

        String endpointUri = String.format(uriPendingBalance, playerId);
        String expectedUrl = baseUrlRcxDb + endpointUri;
        verify(rcxService).makeRequest(eq(expectedUrl), eq(RcxPendingBalanceResponse.class), eq("rcx-get-pendingPointBalance"));
    }

    // @Test
    void getPlayerBalance_happyPath_makesCall() throws AppException, HttpFailureException {
        Assertions.assertNotNull(rcxService);
        doReturn(new RcxPendingBalanceResponse()).when(rcxService).makeRequest(anyString(), same(RcxPendingBalanceResponse.class), anyString());
        rcxService.getPlayerBalances(playerId);

        String endpointUri = String.format("/extapi/v1/members/%s/profile", playerId);
        String expectedUrl = baseUrlRcxPlatform + endpointUri;
        verify(rcxService).makeRequest(eq(expectedUrl), eq(RcxPendingBalanceResponse.class), eq("rcx-get-player-balance"));
        // assertNotNull()
    }

    @Test
    void buildUrl_noStringToken_callsOverload() {
        String output = rcxService.buildUrl("a", "b", "c");
        verify(rcxService).buildUrl(eq("a"), eq("b"), eq("c"));
        verify(rcxService).buildUrl(eq("a"), eq("b"), eq("c"), isNull());
        Assertions.assertEquals("ab", output);
        verifyNoMoreInteractions(rcxService);
    }

    @Test
    void buildUrl_tokenMiddle_callsOverload() {
        String output = rcxService.buildUrl("a", "b%sb", "c");
        verify(rcxService).buildUrl(eq("a"), eq("b%sb"), eq("c"));
        verify(rcxService).buildUrl(eq("a"), eq("b%sb"), eq("c"), isNull());
        Assertions.assertEquals("abcb", output);
        verifyNoMoreInteractions(rcxService);
    }

    @Test
    void buildUrl_tokenOly_callsOverload() {
        String output = rcxService.buildUrl("a", "%s", "c");
        verify(rcxService).buildUrl(eq("a"), eq("%s"), eq("c"));
        verify(rcxService).buildUrl(eq("a"), eq("%s"), eq("c"), isNull());

        Assertions.assertEquals("ac", output);
        verifyNoMoreInteractions(rcxService);
    }
}
