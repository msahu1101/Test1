package com.mgmresorts.loyalty.function;

import com.mgmresorts.common.endpoint.AdminFunctions;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.openapi.Doc;
import com.mgmresorts.common.specification.BaseSpecification;
import com.mgmresorts.loyalty.common.endpoint.AdminFunctionsExtension;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;

public class OpenApiExtension {
    private final BaseSpecification spec = new com.mgmresorts.loyalty.common.specification.ApimSpecification();

    @FunctionName(AdminFunctionsExtension.SPECIFICATION_OPENAPI_APIM_NAME)
    @Doc(hidden = true, readme = "This generates the swagger documentation")
    public HttpResponseMessage openApi(//
                                       @HttpTrigger(name = "req", methods = { HttpMethod.GET },
                                               authLevel = AuthorizationLevel.ADMIN,
                                               route = AdminFunctionsExtension.SPECIFICATION_OPENAPI_APIM_ROUTE) //
                                       final HttpRequestMessage<Optional<String>> request, //
                                       final ExecutionContext context) {
        try {
            final HttpResponseMessage.Builder builder = request.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.OK);
            String definition = spec.definition(request.getUri().getHost(), request.getQueryParameters().get("id"), request.getQueryParameters().get("output"),
                    request.getQueryParameters().get("key"), request.getQueryParameters().get("specType"), request.getQueryParameters().get("frontDoorHost"));
            return builder.header(HeaderBuilder.CONTENT_TYPE, HeaderBuilder.CONTENT_TYPE_VALUE)
                    .body(definition).build();
        } catch (Exception e) {
            e.printStackTrace();
            final HttpResponseMessage.Builder builder = request.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.INTERNAL_SERVER_ERROR);
            return builder.header(HeaderBuilder.CONTENT_TYPE, HeaderBuilder.CONTENT_TYPE_VALUE).body(e.getMessage()).build();
        }
    }

    @FunctionName(AdminFunctionsExtension.SPECIFICATION_OPENAPI_APIM_WEBHOOK_NAME)
    @Doc(hidden = true, readme = "This generates the swagger documentation")
    public HttpResponseMessage openApiWebHook(//
                                              @HttpTrigger(name = "req", methods = { HttpMethod.GET },
                                                      authLevel = AuthorizationLevel.ADMIN,
                                                      route = AdminFunctionsExtension.SPECIFICATION_OPENAPI_APIM_WEBHOOK_ROUTE) //
                                              final HttpRequestMessage<Optional<String>> request, //
                                              final ExecutionContext context) {
        try {
            final HttpResponseMessage.Builder builder = request.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.OK);
            return builder
                    .header(HeaderBuilder.CONTENT_TYPE, HeaderBuilder.CONTENT_TYPE_VALUE)
                    .body(spec.definition(
                            request.getUri().getHost(),
                            "webhook",
                            BaseSpecification.Output.JSON,
                            true,
                            request.getQueryParameters().get("key"),
                            null, null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            final HttpResponseMessage.Builder builder = request.createResponseBuilder(com.microsoft.azure.functions.HttpStatus.INTERNAL_SERVER_ERROR);
            return builder.header(HeaderBuilder.CONTENT_TYPE, HeaderBuilder.CONTENT_TYPE_VALUE).body(e.getMessage()).build();
        }
    }
}
