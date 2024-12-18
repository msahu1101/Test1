package com.mgmresorts.loyalty.data;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;

public interface ISlotDollarBalanceAccess {
    SlotDollarBalance getSlotDollarBalance(String playerId) throws AppException;

}