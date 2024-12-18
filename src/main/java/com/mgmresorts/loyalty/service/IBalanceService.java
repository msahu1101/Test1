package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.BalancesResponse;

public interface IBalanceService {
    BalancesResponse getCustomerBalance(String player, String site) throws AppException;
}
