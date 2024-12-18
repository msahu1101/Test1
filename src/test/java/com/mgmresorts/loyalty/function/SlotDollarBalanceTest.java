package com.mgmresorts.loyalty.function;

import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.mock.FunctionResponseMessage;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.customer.GetSlotDollarBalanceResponse;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.ISlotDollarBalanceService;
import com.mgmresorts.loyalty.service.impl.SlotDollarBalanceService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class SlotDollarBalanceTest {

    @InjectMocks
    private SlotDollarBalance slotDollarBalance;

    @Mock
    private SlotDollarBalanceService slotDollarBalanceService;

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
        when(mockInjectionContext.instanceOf(ISlotDollarBalanceService.class)).thenReturn(slotDollarBalanceService);
        //when(mockInjectionContext.instanceOf(IAuthorizer.class)).thenReturn(EagerAuthorizer.get());

        Mockito.doAnswer(invocation -> {
            GetSlotDollarBalanceResponse getSlotDollarBalanceResponse = new GetSlotDollarBalanceResponse();
            com.mgmresorts.loyalty.dto.customer.SlotDollarBalance slotDollarBalance = new com.mgmresorts.loyalty.dto.customer.SlotDollarBalance()
                .withPlayerId("42424243")
                .withSlotDollars(123);

            getSlotDollarBalanceResponse.setSlotDollarBalance(slotDollarBalance);

            return getSlotDollarBalanceResponse;
        }).when(slotDollarBalanceService).getSlotDollarBalance(anyString());

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> mockHttpRequestMessage = Mockito.mock(HttpRequestMessage.class);
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new FunctionResponseMessage.HttpResponseMessageBuilder(status);
            }
        }).when(mockHttpRequestMessage).createResponseBuilder(any(HttpStatus.class));

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
            return SlotDollarBalance.FUNCTION_NAME;
        });
        doAnswer(new Answer<HttpMethod>() {
            @Override
            public HttpMethod answer(InvocationOnMock invocation) {
                return HttpMethod.GET;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        HttpResponseMessage httpResponseMessage = slotDollarBalance.getSlotDollarBalance(mockHttpRequestMessage, "42424243", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        assertEquals(200, httpResponseMessage.getStatusCode());

        assertEquals("{\"slotDollarBalance\":{\"playerId\":\"42" +
                "424243\",\"slotDollars\":123}}", httpResponseMessage.getBody(), "body");

    }
}
