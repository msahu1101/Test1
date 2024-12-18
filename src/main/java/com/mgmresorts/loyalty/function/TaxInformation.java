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
import com.mgmresorts.loyalty.dto.services.TaxInfoResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.security.ILoyaltyRole;
import com.mgmresorts.loyalty.service.ITaxInfoService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class TaxInformation {

    private static final String PLAYER_TAX_INFORMATION = "player-tax_information";

    @FunctionName(PLAYER_TAX_INFORMATION)
    @Doc(readme = "This endpoint returns tax informations associated to patronId number in patron", response = TaxInfoResponse.class, hidden = false)
    @DocResult({ Errors.INVALID_PATRON_ID, Errors.NO_TAX_INFORMATION, SystemError.INVALID_DATA, Errors.REQUESTED_RESOURCE_NOT_FOUND, Errors.UNEXPECTED_SYSTEM,
            SystemError.INVALID_PAYLOAD, SystemError.SYSTEM_UNAVAILABLE, SystemError.UNAUTHORIZED_ACCESS })
    @DocParams({ @Param(name = "quarter", description = "tax quarter information, default value is all 4 quarters") })
    @Scope({ ILoyaltyRole.READ_TAX, IRole.READ })
    public HttpResponseMessage playerTaxInformation(
            @HttpTrigger(name = "req", methods = {
                    HttpMethod.GET }, authLevel = AuthorizationLevel.ANONYMOUS,
                    // Changing to ANONYMOUS because APIGEE-X can't send Function Key as a query parameter.
                    // However, security risk is eliminated with a common framework's token validation.
                    route = "patron/tax-information/{patronId}/{year}") HttpRequestMessage<Optional<String>> request,
            @BindingName("patronId") String patronId, @BindingName("year") String year, final ExecutionContext context) {

        final String quarter = request.getQueryParameters().get("quarter");
        final InjectionContext iContext = InjectionContext.get();

        return new FunctionHandler<String,TaxInfoResponse>().handle(//
                context, //
                request, //
                (payload) -> iContext.instanceOf(ITaxInfoService.class).readTaxInfoCustomerResponse(patronId, year, quarter), //
                iContext.instanceOf(IAuthorizer.class), //
                String.class,
                TaxInfoResponse.class, //
                patronId//
        );

    }

}
