package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.customer.GetSlotDollarBalanceResponse;

public interface ISlotDollarBalanceService extends IService {
    GetSlotDollarBalanceResponse getSlotDollarBalance(String playerId) throws AppException;
}
