package com.mgmresorts.loyalty.service;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.dto.Message;
import com.mgmresorts.common.dto.Status.Code;
import com.mgmresorts.common.dto.services.OutHeaderSupport;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.IHttpService.HttpHeaders;
import com.mgmresorts.common.http.IHttpService.HttpHeaders.HttpHeader;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.registry.OAuthTokenRegistry;
import com.mgmresorts.common.utils.JSonMapper;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;

public abstract class BaseService {
    @Inject
    private OAuthTokenRegistry registry;
    private ObjectMapper mapper = new JSonMapper();
    private Logger logger = Logger.get(BaseService.class);

    protected final HttpHeader getAuthorizationHeader() throws AppException {
        final Runtime runtime = Runtime.get();
        final String endpoint = runtime.getConfiguration("apim.oauth.endpoint");
        final String clientId = runtime.getConfiguration("apim.oauth.client.id");
        final String clientSecret = runtime.getConfiguration("apim.oauth.client.secret@secure");
        final String scopes = runtime.getConfiguration("apim.oauth.profile.scopes");
        final String token = registry.getAccessToken(endpoint, clientId, clientSecret, scopes);
        final HttpHeaders.HttpHeader tokenKey = new HttpHeaders.HttpHeader("Authorization", "Bearer " + token);
        return tokenKey;
    }

    protected void validateResponse(OutHeaderSupport support) throws AppException {
        boolean validate = false;
        if (!anyNull(support.getHeader(), support.getHeader().getStatus())) {
            if (support.getHeader().getStatus().getCode() == Code.SUCCESS) {
                validate = true;
            }
        }
        if (!validate) {
            throw new AppException(Errors.UNEXPECTED_SYSTEM, "Validate Response failed - OutHeader is not with SUCCESS status");
        }
    }

    protected final AppException unwrap(HttpFailureException ex) throws AppException {
        return unwrap(ex, SystemError.UNABLE_TO_CALL_BACKEND);
    }

    protected final AppException unwrap(HttpFailureException ex, int defaultCode) throws AppException {
        AppException exception = null;
        if (ex != null && ex.getPayload() != null) {
            final String payload = ex.getPayload();
            try {
                final OutHeaderSupport support = mapper.readValue(payload, OutHeaderSupport.class);
                if (!anyNull(support.getHeader(), support.getHeader().getStatus(), support.getHeader().getStatus().getMessages())) {
                    final Message first = Utils.first(support.getHeader().getStatus().getMessages());
                    if (first != null) {
                        exception = new AppException(ApplicationError.SOURCE_SYSTEM_ERROR, first.getCode(), first.getText(), first.getType().value());
                    }
                }

            } catch (JsonProcessingException e) {
                logger.error("Unexpected data found in response {}", payload, e);
                exception = new AppException(defaultCode, ex);
            }
        } else {
            exception = new AppException(defaultCode, ex);
            return exception;
        }
        return exception;
    }

    protected final AppException unwrapGseException(HttpFailureException ex) throws AppException {
        return unwrap(ex, SystemError.UNABLE_TO_CALL_BACKEND);
    }

    private boolean anyNull(Object... objects) {
        for (Object o : objects) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }

}