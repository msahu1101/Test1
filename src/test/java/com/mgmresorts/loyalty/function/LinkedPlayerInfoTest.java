package com.mgmresorts.loyalty.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.mock.FunctionResponseMessage;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.customer.LinkedPlayer;
import com.mgmresorts.loyalty.dto.services.LinkedPlayersResponse;
import com.mgmresorts.loyalty.service.ILinkedPlayerInfoService;
import com.mgmresorts.loyalty.service.impl.LinkedPlayerInfoService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class LinkedPlayerInfoTest {

    @InjectMocks
    private LinkedPlayerInfo linkedPlayerInfo;

    @Mock
    private LinkedPlayerInfoService linkedPlayerInfoServiceMock;

    @BeforeEach
    public void setUp() throws Exception {
        ErrorManager.clean();
        MockitoAnnotations.initMocks(this);
    }

    @AfterAll
    public static void destroy() {
        InjectionContext.setMock(null);
    }

    @Test
    void testRun() throws Exception {

        InjectionContext mockInjectionContext = Mockito.mock(InjectionContext.class);
        InjectionContext.setMock(mockInjectionContext);
        LinkedPlayerInfoService mockLinkPlayerService = Mockito.mock(LinkedPlayerInfoService.class);
        when(mockInjectionContext.instanceOf(ILinkedPlayerInfoService.class)).thenReturn(mockLinkPlayerService);

        Mockito.doAnswer(invocation -> {
            List<LinkedPlayer> linkedPlayers = new ArrayList<>();
            LinkedPlayer playerLink = new LinkedPlayer();
            LinkedPlayersResponse getLinkedPlayerResponse = new LinkedPlayersResponse();
            playerLink.setLinkNumber(54534543);
            playerLink.setPlayerId(4564564);
            linkedPlayers.add(playerLink);

            getLinkedPlayerResponse.setLinkedPlayers(linkedPlayers);

            return getLinkedPlayerResponse;
        }).when(mockLinkPlayerService).getLinkedPlayerInfo(Mockito.anyString());

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> mockHttpRequestMessage = Mockito.mock(HttpRequestMessage.class);
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new FunctionResponseMessage.HttpResponseMessageBuilder(status);
            }
        }).when(mockHttpRequestMessage).createResponseBuilder(any(HttpStatus.class));

        when(mockHttpRequestMessage.getQueryParameters()).thenAnswer(invocation -> {
            Map<String, String> queryParameters = new HashMap<>();
            queryParameters.put("playerId", "4387797");
            return queryParameters;
        });

        doAnswer(new Answer<HttpMethod>() {
            @Override
            public HttpMethod answer(InvocationOnMock invocation) {
                return HttpMethod.GET;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        HttpResponseMessage httpResponseMessage = linkedPlayerInfo.playerLinks(mockHttpRequestMessage, "42424243", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());
    }
}
