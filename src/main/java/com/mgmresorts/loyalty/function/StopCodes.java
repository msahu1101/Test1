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
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IStopCodeService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class StopCodes {

    public static final String FUNCTION_PLAYER_STOPCODES = "player-stop_codes";

    @Doc(readme = "This endpoint returns customer stop codes for a patronId number", response = StopCodeResponse.class)
    @DocResult({ Errors.INVALID_PATRON_ID, SystemError.UNSUPPORTED_OPERATION, SystemError.UNEXPECTED_SYSTEM, ApplicationError.NO_STOP_CODES,
                 Errors.UNABLE_TO_CALL_BACKEND, SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE, SystemError.UNAUTHORIZED_ACCESS })
    @FunctionName(FUNCTION_PLAYER_STOPCODES)
    @Scope({ ILoyaltyRole.READ_STOPCODE, IRole.READ })
    public HttpResponseMessage playerStopCodes(//
            @HttpTrigger(name = "req", methods = { HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/stop-codes/{patronId}") //
            HttpRequestMessage<Optional<String>> request, //
            @BindingName("patronId") //
            final String patronId, //
            final ExecutionContext context) {
        try {
            final InjectionContext iContext = InjectionContext.get();
            return new FunctionHandler<String, StopCodeResponse>().handle(//
                    context, //
                    request, //
                    (couldBeNull) -> iContext.instanceOf(IStopCodeService.class).getCustomerStopCode(patronId), //
                    iContext.instanceOf(IAuthorizer.class), //
                    String.class,
                    StopCodeResponse.class, //
                    patronId//
            );
        } catch (Throwable t) {
            t.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
