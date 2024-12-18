package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.Exceptions;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.errors.Errors;

public class BalanceGiftBalancesAccess extends SqlSupport<GiftBalances, String> implements IBalanceAccess<GiftBalances> {
    private final Logger logger = Logger.get(this.getClass());

    /*
     * To get gift balance information, a JPQL query is executed.
     */
    public List<GiftBalances> databaseCall(int player, int site) throws AppException {
        logger.trace("CustomerBalanceAccessGiftBalances is executing database call.");
        try {
            List<GiftBalances> giftBalances = findByNamedQuery(DB.MLIFE, GiftBalances.GIFT_BALANCES_QUERY, (query) -> {
                query.setParameter(1, player);
            });
            logger.debug("customer gift balance database response", giftBalances.toString());
            return giftBalances;
        } catch (Exception e) {
            logger.error("Error: {}", e);
            throw Exceptions.wrap(e, Errors.UNABLE_TO_CALL_BACKEND, "Failure to complete gift balances call.");
        }
    }

    @Override
    public Class<GiftBalances> getEntityType() {
        return GiftBalances.class;
    }

}
