package com.mgmresorts.loyalty.data.impl;

import java.util.ArrayList;
import java.util.List;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.Exceptions;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.entity.Tier;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.errors.Errors;

public class BalanceTierAccess extends SqlSupport<Tier, String> implements IBalanceAccess<Tier> {
    private final Logger logger = Logger.get(BalanceTierAccess.class);
    private final String name = "tier";

    /*
     * To get tier information, a stored procedure query is used.
     */
    

    @Override
    public List<Tier> databaseCall(int playerId, int siteId) throws AppException {
        String procName = "TIBCO_PlayerProfile_get";
        List<Tier> result = new ArrayList<>();
        try {
            final String nativeProcedure = "EXEC " + procName + " @PlayerId = " + playerId + " , @SiteId = " + siteId + "";
            result = callProcByQuery(DB.LME, nativeProcedure, Tier.class, (sp) -> {
            });
        } catch (Exception e) {
            logger.error("Error while calling Patron Proc_GetBorgataOfferDetails store procedure", e);
            throw Exceptions.wrap(e, Errors.UNABLE_TO_CALL_BACKEND, "Failure to complete " + name + " call.");
        }
        return result;
    }
    
    
    @Override
    public Class<Tier> getEntityType() {
        return Tier.class;
    }

}
