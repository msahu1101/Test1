package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.rcxplatform.pojo.GetMembersResponse;

public interface IRcxService extends IService {

    double rcxGetPendingPoints(String playerId) throws AppException;

    GetMembersResponse getPlayerBalances(String playerId) throws AppException, HttpFailureException;
}
