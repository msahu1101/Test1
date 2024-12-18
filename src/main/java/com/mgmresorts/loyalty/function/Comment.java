package com.mgmresorts.loyalty.function;

import java.util.Optional;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.function.FunctionHandler;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocParams;
import com.mgmresorts.common.openapi.DocParams.Param;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.security.IAuthorizer;
import com.mgmresorts.common.security.scope.IRole;
import com.mgmresorts.common.security.scope.Scope;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.PlayerCommentsResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.ICommentService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Comment {

    private static final String PLAYER_COMMENT = "player-comment";

    @FunctionName(PLAYER_COMMENT)
    @Doc(readme = "This endpoint returns comments associated to patronId number in patron", response = PlayerCommentsResponse.class, hidden = false)
    @DocResult({ Errors.INVALID_PATRON_ID, Errors.NO_PLAYER_COMMENT, Errors.UNABLE_TO_CALL_BACKEND, SystemError.UNEXPECTED_SYSTEM, SystemError.UNSUPPORTED_OPERATION,
            SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE })
    @DocParams({ @Param(name = "high-priority", description = "Control high priority comments") })
    @Scope({ ILoyaltyRole.READ_COMMENT, IRole.READ })
    public HttpResponseMessage playerComment(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/comments/{patronId}") HttpRequestMessage<Optional<String>> request,
            @BindingName("patronId") String patronId, final ExecutionContext context) throws Exception {
        final String highPriority = request.getQueryParameters().getOrDefault("high-priority", "true");
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<String, PlayerCommentsResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(ICommentService.class).getCustomerCommentsFromPatron(patronId, highPriority), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                PlayerCommentsResponse.class, //
                patronId//
        );
    }
}
