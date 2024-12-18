package com.mgmresorts.loyalty.function;

import java.util.Optional;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.function.FunctionHandler;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.security.IAuthorizer;
import com.mgmresorts.common.security.scope.Scope;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketRequest;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IPromoEventTicketIssuanceService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class PromoEventTicketIssuance {

    @FunctionName(IFunctions.FUNCTION_ISSUE_PROMO)
    @Doc(readme = "This endpoint update promo event based on player id", response = IssuePromoEventTicketResponse.class, request = IssuePromoEventTicketRequest.class)
    @DocResult({ Errors.INVALID_PAYLOAD, ApplicationError.INVALID_REQUEST, SystemError.UNABLE_TO_CALL_BACKEND, SystemError.UNEXPECTED_SYSTEM, SystemError.UNSUPPORTED_OPERATION,
            SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE, SystemError.UNAUTHORIZED_ACCESS })
    @Scope({ ILoyaltyRole.UPDATE_PROFILE })
    public HttpResponseMessage issueEventPromotion(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.PUT }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = IFunctions.FUNCTION_ISSUE_PROMO_URL) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<IssuePromoEventTicketRequest, IssuePromoEventTicketResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(IPromoEventTicketIssuanceService.class).issueEventPromo(payload), //
                iContext.instanceOf(IAuthorizer.class), //
                IssuePromoEventTicketRequest.class, //
                IssuePromoEventTicketResponse.class, //
                null //
        );
    }
}
