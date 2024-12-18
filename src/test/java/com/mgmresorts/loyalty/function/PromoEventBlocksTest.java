package com.mgmresorts.loyalty.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
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
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.mock.FunctionResponseMessage;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.customer.PromoEventBlocks;
import com.mgmresorts.loyalty.dto.services.PromoEventsBlockResponse;
import com.mgmresorts.loyalty.service.IPromoEventsService;
import com.mgmresorts.loyalty.service.impl.PromoEventsService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class PromoEventBlocksTest {
    
    @InjectMocks
    private com.mgmresorts.loyalty.function.PromoEventBlocks promoEventBlock;

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
        PromoEventsService promoEventsService = Mockito.mock(PromoEventsService.class);
        when(mockInjectionContext.instanceOf(IPromoEventsService.class)).thenReturn(promoEventsService);

        Mockito.doAnswer(invocation -> {
            List<PromoEventBlocks> promoEventBlocks = new ArrayList<>();
            PromoEventBlocks promoEventBlock = new PromoEventBlocks();
            PromoEventsBlockResponse promoEventsBlockResponse = new PromoEventsBlockResponse();
            promoEventBlock.setBlockId(456);
            promoEventBlock.setDefaultAmount(4798);
            promoEventBlock.setDescription("test result");
            promoEventBlock.setEventId(4545);
            promoEventBlock.setMaxPerPlayer(45454);
            promoEventBlock.setSiteId(54545);
            promoEventBlock.setStatus("A");
            promoEventBlock.setTickets(154544);
            promoEventBlocks.add(promoEventBlock);
            promoEventsBlockResponse.setHeader(HeaderBuilder.buildHeader());
            promoEventsBlockResponse.setPromoEventBlocks(promoEventBlocks);
            return promoEventsBlockResponse;
        }).when(promoEventsService).getPromoEventBlockInfo("78787", "promo");

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
        HttpResponseMessage httpResponseMessage = promoEventBlock.getPromoEvents(mockHttpRequestMessage, "345345", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());

    }
}