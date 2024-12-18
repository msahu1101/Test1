package com.mgmresorts.loyalty.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.mock.FunctionResponseMessage;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalancesInfo;
import com.mgmresorts.loyalty.dto.customer.GiftPoints;
import com.mgmresorts.loyalty.dto.customer.Tier;
import com.mgmresorts.loyalty.dto.services.BalancesResponse;
import com.mgmresorts.loyalty.service.IBalanceService;
import com.mgmresorts.loyalty.service.impl.BalanceService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class BalanceTest {

    @InjectMocks
    private Balance balanceMock;

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
    public void testgetCustomerBalance() throws Exception {
        InjectionContext mockInjectionContext = Mockito.mock(InjectionContext.class);
        InjectionContext.setMock(mockInjectionContext);
        BalanceService mockBalanceService = Mockito.mock(BalanceService.class);
        when(mockInjectionContext.instanceOf(IBalanceService.class)).thenReturn(mockBalanceService);

        Mockito.doAnswer(invocation -> {
            CustomerBalances customerBalances = new CustomerBalances();
            BalancesResponse getBalancesResponse = new BalancesResponse();
            GiftPointBalancesInfo giftPointBalancesInfo = new GiftPointBalancesInfo();
            customerBalances.setGiftPointBalancesInfo(giftPointBalancesInfo);
            GiftPoints giftPoints = new GiftPoints();
            customerBalances.setGiftPoints(giftPoints);
            Tier tier = new Tier();
            customerBalances.setTier(tier);
            getBalancesResponse.setCustomerBalances(customerBalances);
            return getBalancesResponse;
        }).when(mockBalanceService).getCustomerBalance("3454354", "1");

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> mockHttpRequestMessage = Mockito.mock(HttpRequestMessage.class);
        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new FunctionResponseMessage.HttpResponseMessageBuilder(status);
            }
        }).when(mockHttpRequestMessage).createResponseBuilder(any(HttpStatus.class));
        doAnswer(new Answer<HttpMethod>() {
            @Override
            public HttpMethod answer(InvocationOnMock invocation) {
                return HttpMethod.GET;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        when(mockHttpRequestMessage.getHttpMethod()).thenAnswer(invocation -> {
            return HttpMethod.GET;
        });
        ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        HttpResponseMessage httpResponseMessage = balanceMock.getCustomerBalance(mockHttpRequestMessage, "345345", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());

    }
}
