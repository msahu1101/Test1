package com.mgmresorts.loyalty.function;

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
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.ISlotDollarBalanceService;
import com.mgmresorts.loyalty.dto.customer.GetSlotDollarBalanceResponse;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class SlotDollarBalance {

    public static final String FUNCTION_NAME = "slot-dollar-balance";

    @FunctionName(FUNCTION_NAME)
    @Doc(readme = "This endpoint returns slot dollar balance associated to a playerId", response = GetSlotDollarBalanceResponse.class)
    @DocResult({ Errors.INVALID_PATRON_ID, SystemError.UNSUPPORTED_OPERATION, SystemError.UNEXPECTED_SYSTEM, SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE,
            SystemError.UNAUTHORIZED_ACCESS })
    @DocParams({ @Param(name = "playerId", description = "player id is the representation of player") })
    @Scope({ ILoyaltyRole.READ_BALANCE, ILoyaltyRole.READ_PROFILE_MOBILE, IRole.READ })
    public HttpResponseMessage getSlotDollarBalance(
            @HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = IFunctions.FUNCTION_SLOT_DOLLAR_BALANCE) //
            final HttpRequestMessage<Optional<String>> request, //
            @BindingName("playerId") //
            final String playerId, //
            final ExecutionContext context) {
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<String, GetSlotDollarBalanceResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(ISlotDollarBalanceService.class).getSlotDollarBalance(playerId), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                GetSlotDollarBalanceResponse.class, //
                playerId //
        );
    }
}
