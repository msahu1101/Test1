package com.mgmresorts.loyalty.function;

import java.util.Optional;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.function.FunctionHandler;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.security.IAuthorizer;
import com.mgmresorts.common.security.scope.IRole;
import com.mgmresorts.common.security.scope.Scope;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.PromotionsResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IPromotionsService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Promotions {
    private static final String PLAYER_PROMO = "player-promo";

    @FunctionName(PLAYER_PROMO)
    @Doc(readme = "This endpoint returns raw promotions associated to patronId number in patron", response = PromotionsResponse.class)
    @DocResult({ Errors.INVALID_DATA, ApplicationError.INVALID_PATRON_ID, ApplicationError.NO_PROMOTION, SystemError.INVALID_PAYLOAD,
                 SystemError.SYSTEM_UNAVAILABLE, SystemError.UNAUTHORIZED_ACCESS, SystemError.UNEXPECTED_SYSTEM, SystemError.UNSUPPORTED_OPERATION })
    @Scope({ ILoyaltyRole.READ_PROMO, IRole.READ })
    public HttpResponseMessage playerPromo(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/player-promo/{patronId}") HttpRequestMessage<Optional<String>> request,
            @BindingName("patronId") String patronId, final ExecutionContext context) {
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<String, PromotionsResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(IPromotionsService.class).getCustomerOffers(patronId), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                PromotionsResponse.class, //
                patronId//
        );
    }
}
