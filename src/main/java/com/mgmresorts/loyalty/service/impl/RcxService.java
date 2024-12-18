package com.mgmresorts.loyalty.service.impl;

import com.google.inject.Inject;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.IHttpService.HttpHeaders;
import com.mgmresorts.common.http.IHttpService.HttpHeaders.HttpHeader;
import com.mgmresorts.common.http.IHttpService;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.dto.common.Purses;
import com.mgmresorts.loyalty.dto.services.RcxPendingBalanceResponse;
import com.mgmresorts.loyalty.service.BaseService;
import com.mgmresorts.loyalty.service.IRcxService;
import com.mgmresorts.rcxplatform.pojo.GetMembersResponse;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class RcxService extends BaseService implements IRcxService {
    private final Logger logger = Logger.get(RcxService.class);

    private final String rcxPlatBaseUrl = Runtime.get().getConfiguration("rcx.platform.base.url");

    private final String rcxUrl = Runtime.get().getConfiguration("rcx.pending.balance.url");

    private final String uriPlayerBalance = "/extapi/v1/members/%s/profile";
    private final String maskGroup = "loyalty:rcx";

    private final String telmetryDependencyPendingPointsBalance = "RCX-Read-PendingPointBalance";
    private final String telemetryDependencyPlayerBalance = "rcx-get-player-balance";

    @Inject
    private IHttpService httpService;

    public double rcxGetPendingPoints(String playerId) throws AppException {
        double rcxPendingBalance = 0;
        try {
            final HttpHeader authorizationHeader = getAuthorizationHeader();
            //logger.debug("authorizationHeader: {} , {}", authorizationHeader.key, authorizationHeader.value);
            final String rcxEndPoint = String.format(rcxUrl, Long.parseLong(playerId));
            final List<HttpHeader> headers = Arrays.asList(HttpHeaders.APPLICATION_JSON, authorizationHeader);

            RcxPendingBalanceResponse rcxPendingBalanceResponse = httpService.get(rcxEndPoint, RcxPendingBalanceResponse.class,
                maskGroup, telmetryDependencyPendingPointsBalance, headers);
            if (rcxPendingBalanceResponse.getMember() != null) {
                List<Purses> purses = rcxPendingBalanceResponse.getMember().getPurses();
                if (!purses.isEmpty()) {
                    purses = rcxPendingBalanceResponse.getMember().getPurses();
                    for (Purses pendingBalance : purses) {
                        if (pendingBalance.getName().equals("Points")) {
                            logger.debug("name = Points, balance = " + pendingBalance.getBalance());
                            rcxPendingBalance = pendingBalance.getBalance() / 10000d;
                            break;
                        }
                    }
                }
            }
        } catch (HttpFailureException e) {
            logger.error("RCX service call Error: {} - {}", e.getMessage(), e.getPayload());
            //PlayerId not existing in RCX would get exception with payload of "The loyalty id # passed to get profile is not valid"
        }
        logger.debug("rcxPendingBalance = " + rcxPendingBalance);
        return rcxPendingBalance;
    }
  
    public GetMembersResponse getPlayerBalances(String playerId) throws AppException, HttpFailureException {
        String url = buildUrl(rcxPlatBaseUrl, uriPlayerBalance, playerId);

        return makeRequest(url, GetMembersResponse.class, telemetryDependencyPlayerBalance);
    }

    protected String buildUrl(String baseUrl, String endpointUri, String playerId) {
        return buildUrl(baseUrl, endpointUri, playerId, null);
    }

    protected String buildUrl(String baseUrl, String endpointUri, String playerId, String queryString) {
        String url = String.format("%s%s", baseUrl, endpointUri);
        url = String.format(url, playerId);

        return StringUtils.isEmpty(queryString) ? url : String.format("%s?%s", url, queryString);
    }

    protected <R> R makeRequest(String url, Class<R> clazz, String telemetryDependency) throws AppException, HttpFailureException {
        final HttpHeader authorizationHeader = getAuthorizationHeader();
        final List<HttpHeader> headers = Arrays.asList(HttpHeaders.APPLICATION_JSON, authorizationHeader);
        return httpService.get(url, clazz, maskGroup, telemetryDependency, headers);

        /*
        // TODO: This is failing in unit tests because return value is null.  Need to figure a way around it.
        final HttpHeader authorizationHeader = getAuthorizationHeader();
        final List<HttpHeader> headers = Arrays.asList(HttpHeaders.APPLICATION_JSON, authorizationHeader);
        logger.info("RCX service call - Request - ({}): Url: [{}]: {} - {}", telemetryDependency, url);

        try {
            R responseObject = httpService.get(url, clazz, maskGroup, telemetryDependency, headers);
            logger.info("RCX service call ({}): Successful", telemetryDependency);
            return responseObject;
        } catch (HttpFailureException err) {
            logger.error("RCX service call Error ({}): {} - {}", telemetryDependency, err.getMessage(), err.getPayload());
            throw(err);
        } */
    }
}
