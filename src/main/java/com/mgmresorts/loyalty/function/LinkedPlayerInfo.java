package com.mgmresorts.loyalty.function;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.function.FunctionHandler;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.security.IAuthorizer;
import com.mgmresorts.common.security.scope.IRole;
import com.mgmresorts.common.security.scope.Scope;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.LinkedPlayersResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.ILinkedPlayerInfoService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class LinkedPlayerInfo {

    private static final String PLAYER_LINKS = "player-links";

    @FunctionName(PLAYER_LINKS)
    @Doc(readme = "This endpoint returns other players associated to patronId number in patron", response = LinkedPlayersResponse.class)
    @DocResult({ Errors.INVALID_PATRON_ID, ApplicationError.NO_LINKED_MEMBERS, SystemError.UNEXPECTED_SYSTEM, SystemError.UNSUPPORTED_OPERATION,
                 SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE, SystemError.UNAUTHORIZED_ACCESS })
    @Scope({ ILoyaltyRole.READ_LINK, IRole.READ })
    public HttpResponseMessage playerLinks(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/linked-player/{patronId}") HttpRequestMessage<Optional<String>> request, //
            @BindingName("patronId") String patronId, final ExecutionContext context) {
        final InjectionContext iContext = InjectionContext.get();

        return new FunctionHandler<String, LinkedPlayersResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(ILinkedPlayerInfoService.class).getLinkedPlayerInfo(patronId), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                LinkedPlayersResponse.class, //
                patronId//
        );

    }

}
