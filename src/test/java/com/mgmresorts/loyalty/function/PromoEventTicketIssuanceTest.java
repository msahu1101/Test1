package com.mgmresorts.loyalty.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.mock.FunctionResponseMessage;
import com.mgmresorts.common.utils.JSonMapper;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketRequest;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketResponse;
import com.mgmresorts.loyalty.dto.services.Result;
import com.mgmresorts.loyalty.service.IPromoEventTicketIssuanceService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

class PromoEventTicketIssuanceTest {

    @InjectMocks
    PromoEventTicketIssuance issuePromoFunction;

    @Mock
    InjectionContext context;

    @BeforeEach
    public void setUp(TestInfo info) throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIssueEventPromotion() throws AppException {

        ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);

        InjectionContext mockInjectionContext = Mockito.mock(InjectionContext.class);
        InjectionContext.setMock(mockInjectionContext);

        IPromoEventTicketIssuanceService mockSourceSystemService = Mockito.mock(IPromoEventTicketIssuanceService.class);

        when(mockInjectionContext.instanceOf(IPromoEventTicketIssuanceService.class)).thenReturn(mockSourceSystemService);

        Mockito.doAnswer(invocation -> {
            IssuePromoEventTicketResponse issuePromoEventTicketResponse = new IssuePromoEventTicketResponse();
            Result result = new Result();
            result.setStatus("200");
            issuePromoEventTicketResponse.setHeader(HeaderBuilder.buildHeader());
            issuePromoEventTicketResponse.setResult(result);

            return issuePromoEventTicketResponse;
        }).when(mockSourceSystemService).issueEventPromo(Mockito.any());

        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> mockHttpRequestMessage = Mockito.mock(HttpRequestMessage.class);

        Optional<String> request = Optional.of(issuePromeRequest());

        when(mockHttpRequestMessage.getBody()).thenReturn(request);

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
                return HttpMethod.PUT;
            }
        }).when(mockHttpRequestMessage).getHttpMethod();

        HttpResponseMessage httpResponseMessage = issuePromoFunction.issueEventPromotion(mockHttpRequestMessage, mockExecutionContext);
        Assertions.assertNotNull((String) httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());
    }

    private String issuePromeRequest() {
        IssuePromoEventTicketRequest updateRequest = new IssuePromoEventTicketRequest();
        updateRequest.setBlockId(43);
        updateRequest.setPickedUp("test");
        updateRequest.setPlayerId(34);
        updateRequest.setPromoId(67);
        updateRequest.setTicketCount(34);

        return new JSonMapper().writeValueAsString(updateRequest);
    }
}
