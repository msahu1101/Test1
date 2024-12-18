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
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.BalancesResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IBalanceService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Balance {

    private static final String PLAYER_BALANCE = "player-balance";

    @FunctionName(PLAYER_BALANCE)
    @Doc(readme = "This endpoint returns balances associated to patronId number in patron", response = BalancesResponse.class)
    @DocResult({ Errors.INVALID_PATRON_ID, SystemError.UNSUPPORTED_OPERATION, SystemError.UNEXPECTED_SYSTEM, SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE,
            SystemError.UNAUTHORIZED_ACCESS })
    @DocParams({ @Param(name = "site", description = "site id is the patron representation of property") })
    @Scope({ ILoyaltyRole.READ_BALANCE,ILoyaltyRole.READ_PROFILE_MOBILE, IRole.READ })
    public HttpResponseMessage getCustomerBalance(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/balance/{patronId}") //
            final HttpRequestMessage<Optional<String>> request, //
            @BindingName("patronId") //
            final String patronId, //
            final ExecutionContext context) {
        final String site = request.getQueryParameters().get("site");
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<String, BalancesResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(IBalanceService.class).getCustomerBalance(patronId, Utils.isEmpty(site) ? "1" : site), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                BalancesResponse.class, patronId//
        );

    }

}
