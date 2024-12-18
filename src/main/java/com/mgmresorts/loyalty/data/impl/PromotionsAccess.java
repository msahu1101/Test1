package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IPromotionsAccess;
import com.mgmresorts.loyalty.data.entity.Offers;
import com.mgmresorts.loyalty.data.support.SqlSupport;

public class PromotionsAccess extends SqlSupport<Offers, String> implements IPromotionsAccess {

    private final Logger logger = Logger.get(PromotionsAccess.class);
    
    @Override
    public List<Offers> readPatronCustPromotions(int mlifeNumber) throws AppException {
        List<Offers> offers = callByNamedProcedure(DB.WORK, Offers.OFFERS_CALL, Offers.class, (sp) -> {
            sp.setParameter(1, mlifeNumber);
        });
        logger.debug("promotion database response", offers.toString());
        return offers;
    }

    @Override
    protected Class<Offers> getEntityType() {
        return Offers.class;
    }
}
