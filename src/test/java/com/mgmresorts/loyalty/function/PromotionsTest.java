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
import com.mgmresorts.loyalty.dto.customer.CustomerPromotion;
import com.mgmresorts.loyalty.dto.services.PromotionsResponse;
import com.mgmresorts.loyalty.service.IPromotionsService;
import com.mgmresorts.loyalty.service.impl.PromotionsService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class PromotionsTest {

    @InjectMocks
    private Promotions promotionsMock;

    @Mock
    private PromotionsService promotionServiceMock;

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
        PromotionsService mockPromotionService = Mockito.mock(PromotionsService.class);
        when(mockInjectionContext.instanceOf(IPromotionsService.class)).thenReturn(mockPromotionService);

        Mockito.doAnswer(invocation -> {
            List<CustomerPromotion> customerPromotions = new ArrayList<>();
            CustomerPromotion customerPromotion = new CustomerPromotion();
            PromotionsResponse getStopCodeResponse = new PromotionsResponse();
            customerPromotion.setPublicDescription("test");
            customerPromotion.setName("promo");
            customerPromotion.setPromoId("4242");
            customerPromotions.add(customerPromotion);
            getStopCodeResponse.setCustomerPromotions(customerPromotions);

            return getStopCodeResponse;
        }).when(mockPromotionService).getCustomerOffers(Mockito.anyString());

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
            queryParameters.put("siteId", "true");
            return queryParameters;
        });

        when(mockHttpRequestMessage.getHeaders()).thenAnswer(invocation -> {
            Map<String, String> headers = new HashMap<>();
            return headers;
        });
        doAnswer(new Answer<HttpMethod>() {
            @Override
            public HttpMethod answer(InvocationOnMock invocation) {
                return HttpMethod.GET;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        HttpResponseMessage httpResponseMessage = promotionsMock.playerPromo(mockHttpRequestMessage, "sdfsdfds", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());

    }

}
