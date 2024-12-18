package com.mgmresorts.loyalty.common.endpoint;

import com.mgmresorts.common.endpoint.AdminFunctions;

public interface AdminFunctionsExtension extends AdminFunctions {
    String SPECIFICATION_OPENAPI_APIM_NAME = "control-openapi-apim";
    String SPECIFICATION_OPENAPI_APIM_ROUTE = "control/openapi/apim";

    String SPECIFICATION_OPENAPI_APIM_WEBHOOK_NAME = "control-webhook-openapi-apim";
    String SPECIFICATION_OPENAPI_APIM_WEBHOOK_ROUTE = "control/webhook-openapi/apim";
}
