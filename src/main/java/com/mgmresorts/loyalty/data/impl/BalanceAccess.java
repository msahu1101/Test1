package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.support.SqlSupport;

public class BalanceAccess extends SqlSupport<Balance, String> implements IBalanceAccess<Balance> {
    private final Logger logger = Logger.get(BalanceAccess.class);

    public List<Balance> databaseCall(int player, int site) throws AppException {
        logger.trace("CustomerBalanceAccessBalance is executing database call.");

        List<Balance> balances = findByNamedQuery(DB.WORK, Balance.BALANCE_CALL, (query) -> {
            query.setParameter(1, Integer.toString(player));
            query.setParameter(2, site);
        });
        logger.debug("customer balance database response", balances.toString());
        return balances;
    }

    @Override
    public Class<Balance> getEntityType() {
        return Balance.class;
    }
}
