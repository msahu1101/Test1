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
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IStopCodeService;
import com.mgmresorts.loyalty.service.impl.StopCodeService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class StopCodesTest {

    @InjectMocks
    private StopCodes stopCodesMock;

    @Mock
    private StopCodeService stopCodeServiceMock;

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
    public void testRun() throws Exception {
        InjectionContext mockInjectionContext = Mockito.mock(InjectionContext.class);
        InjectionContext.setMock(mockInjectionContext);
        StopCodeService mockStopCodeService = Mockito.mock(StopCodeService.class);
        when(mockInjectionContext.instanceOf(IStopCodeService.class)).thenReturn(mockStopCodeService);
        //when(mockInjectionContext.instanceOf(IAuthorizer.class)).thenReturn(EagerAuthorizer.get());

        Mockito.doAnswer(invocation -> {
            List<CustomerStopCode> customerStopCodes = new ArrayList<>();
            CustomerStopCode customerStopCode = new CustomerStopCode();
            StopCodeResponse getStopCodeResponse = new StopCodeResponse();
            customerStopCode.setDescription("dsfsdfsdfds");
            customerStopCode.setId("1");
            customerStopCode.setIsActive(true);
            customerStopCode.setPriority(10);
            customerStopCodes.add(customerStopCode);
            getStopCodeResponse.setCustomerStopCode(customerStopCodes);

            return getStopCodeResponse;
        }).when(mockStopCodeService).getCustomerStopCode(Mockito.anyString());

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
            queryParameters.put("highPriority", "true");
            return queryParameters;
        });

        when(mockHttpRequestMessage.getHeaders()).thenAnswer(invocation -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("timestamp", "2018-04-10T10:01:04.713");
            headers.put("x-scopes", ILoyaltyRole.READ_LOYALTY);
            return headers;
        });
        when(mockHttpRequestMessage.getHttpMethod()).thenAnswer(invocation -> {
            return HttpMethod.GET;
        });
        ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        when(mockExecutionContext.getFunctionName()).thenAnswer(invocation -> {
            return StopCodes.FUNCTION_PLAYER_STOPCODES;
        });
        doAnswer(new Answer<HttpMethod>() {
            @Override
            public HttpMethod answer(InvocationOnMock invocation) {
                return HttpMethod.GET;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        HttpResponseMessage httpResponseMessage = stopCodesMock.playerStopCodes(mockHttpRequestMessage, "42424243", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());

    }
}
