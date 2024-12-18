package com.mgmresorts.loyalty.function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.customer.CustomerComment;
import com.mgmresorts.loyalty.dto.services.PlayerCommentsResponse;
import com.mgmresorts.loyalty.service.ICommentService;
import com.mgmresorts.loyalty.service.impl.CommentService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;

@SuppressWarnings({ "unchecked" })
public class CommentTest {

    @InjectMocks
    private Comment comment;

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
        CommentService mockPatronCustomerCommentService = Mockito.mock(CommentService.class);
        when(mockInjectionContext.instanceOf(ICommentService.class)).thenReturn(mockPatronCustomerCommentService);

        Mockito.doAnswer(invocation -> {
            PlayerCommentsResponse getPlayerCommentsResponse = new PlayerCommentsResponse();
            getPlayerCommentsResponse.setComments(new ArrayList<CustomerComment>());
            CustomerComment customerComment = new CustomerComment();

            customerComment.setCreateDate(Dates.toZonedDateTime("2007-07-13T01:14:11", ZoneId.systemDefault()));
            customerComment.setExpiryDate(Dates.toZonedDateTime("2007-07-13T01:14:11", ZoneId.systemDefault()));
            customerComment.setIsHighPriority(true);
            customerComment.setIsPrivate(false);
            customerComment.setNumber("1");
            customerComment.setText(
                    "This customer has enrolled on a Do Not Call list.  It is against federal law and MGM MIRAGE policy to contact this customer by phone if the customer does not have an EBR with MGM MIRAGE.  Please review the customer’s play and trip history to verify that the player has an EBR prior to contacting the customer.");
            getPlayerCommentsResponse.getComments().add(customerComment);
            customerComment = new CustomerComment();
            customerComment.setCreateDate(Dates.toZonedDateTime("2007-11-18T17:30:52", ZoneId.systemDefault()));
            customerComment.setExpiryDate(Dates.toZonedDateTime("2007-12-19T00:00:00", ZoneId.systemDefault()));
            customerComment.setIsHighPriority(true);
            customerComment.setIsPrivate(false);
            customerComment.setNumber("2");
            customerComment.setSiteId("5");
            customerComment.setText(
                    "Mr. ONeil requested special consideration to be able to make improper bets. This was denied by Christina. Mr. ONeil left before he could be told. Refer to Shift if necessary. L Tang");
            getPlayerCommentsResponse.getComments().add(customerComment);
            customerComment = new CustomerComment();
            customerComment.setCreateDate(Dates.toZonedDateTime("2008-03-31T07:18:57", ZoneId.systemDefault()));
            customerComment.setExpiryDate(Dates.toZonedDateTime("2008-04-10T00:00:00", ZoneId.systemDefault()));
            customerComment.setIsHighPriority(true);
            customerComment.setIsPrivate(false);
            customerComment.setNumber("4");
            customerComment.setSiteId("1");
            customerComment.setText("qtd:4nts@cr p/ emurnane( 4/10");
            getPlayerCommentsResponse.getComments().add(customerComment);
            return getPlayerCommentsResponse;
        }).when(mockPatronCustomerCommentService).getCustomerCommentsFromPatron(any(String.class), any(String.class));

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
            queryParameters.put("high-priority", "true");
            return queryParameters;
        });

        when(mockHttpRequestMessage.getHeaders()).thenAnswer(invocation -> {
            Map<String, String> headers = new HashMap<>();
            headers.put("timestamp", "2018-04-10T10:01:04.713");
            return headers;
        });
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
        HttpResponseMessage httpResponseMessage = comment.playerComment(mockHttpRequestMessage, "4387797", mockExecutionContext);
        Assertions.assertNotNull(httpResponseMessage);
        Assertions.assertNotNull(httpResponseMessage.getBody());
        Assertions.assertEquals(HttpStatus.OK, httpResponseMessage.getStatus());
        Assertions.assertEquals(200, httpResponseMessage.getStatusCode());
    }
}
