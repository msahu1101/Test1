package com.mgmresorts.loyalty.function;

import java.util.Optional;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.function.FunctionHandler;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.openapi.DocResult;
import com.mgmresorts.common.registry.FunctionRegistry;
import com.mgmresorts.common.security.IAuthorizer;
import com.mgmresorts.common.security.scope.Scope;
import com.mgmresorts.loyalty.common.InjectionContext;
import com.mgmresorts.loyalty.dto.services.PromoEventsBlockResponse;
import com.mgmresorts.loyalty.dto.services.PromoEventsRequest;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.IPromoEventsService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class PromoEventBlocks {

    @FunctionName(IFunctions.FUNCTION_PROMO_EVENTS)
    @Doc(readme = "This endpoint returns promotion events based on the promo id/event id", response = PromoEventsBlockResponse.class, request = PromoEventsRequest.class)
    @DocResult({ Errors.INVALID_PAYLOAD, ApplicationError.INVALID_PROMO_IDS, ApplicationError.INVALID_REQUEST, SystemError.UNABLE_TO_CALL_BACKEND,
            SystemError.UNEXPECTED_SYSTEM,SystemError.UNSUPPORTED_OPERATION,SystemError.INVALID_PAYLOAD,
            SystemError.SYSTEM_UNAVAILABLE,SystemError.UNAUTHORIZED_ACCESS})
    @Scope({ ILoyaltyRole.READ_PROFILE })
    public HttpResponseMessage getPromoEvents(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = IFunctions.FUNCTION_PROMO_EVENTS_URL) HttpRequestMessage<Optional<String>> request,
            @BindingName("id") String id, final ExecutionContext context) {
        final String type = request.getQueryParameters().get("type");
        final InjectionContext iContext = InjectionContext.get();
        return new FunctionHandler<String, PromoEventsBlockResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(IPromoEventsService.class).getPromoEventBlockInfo(id,type), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                PromoEventsBlockResponse.class, //
                id //
        );
    }

    public static void main(String[] args) throws AppException, Exception {
        System.setProperty("runtime.environment", "local");
        final HttpResponseMessage call = FunctionRegistry.getRegistry().call(IFunctions.FUNCTION_PROMO_EVENTS, "patron/promo-events/99?type=event", null,
                new String[] { "x-mgm-correlation-id", "tes123" });
        System.out.println(call.getBody());
        System.exit(-1);
    }

}
