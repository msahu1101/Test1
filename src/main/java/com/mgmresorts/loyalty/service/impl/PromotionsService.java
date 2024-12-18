package com.mgmresorts.loyalty.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.Exceptions;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.ThreadContext;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.AppError;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.IPromotionsAccess;
import com.mgmresorts.loyalty.data.entity.Offers;
import com.mgmresorts.loyalty.dto.customer.CustomerPromotion;
import com.mgmresorts.loyalty.dto.patron.customerpromo.CRMAcresMessage;
import com.mgmresorts.loyalty.dto.services.PromotionsResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.IPromotionsService;
import com.mgmresorts.loyalty.transformer.ITransformer;

public class PromotionsService implements IPromotionsService {

    private final Logger logger = Logger.get(PromotionsService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.promotion");

    @Inject
    private IPromotionsAccess customerPromotionsAccess;

    @Inject
    private ITransformer<List<CustomerPromotion>, CRMAcresMessage> transformer;

    @Inject
    private ICache<String> caches;

    @Override
    public PromotionsResponse getCustomerOffers(final String mlifeno) throws AppException {
        return circuit.flow(() -> getCustPromotionsOffer(mlifeno), SystemError.UNABLE_TO_CALL_BACKEND);
    }

    private PromotionsResponse getCustPromotionsOffer(String mlifeno) throws AppException {
        logger.trace("controll enter into CustomerPromotionsService", mlifeno);
        if (!Utils.isNumeric(mlifeno)) {
            throw new AppException(Errors.INVALID_PATRON_ID, mlifeno);
        }
        final String custPromoResult;
        custPromoResult = caches.nonBlockingGet(mlifeno, String.class, (s) -> {
                return getCustPromotions(mlifeno);
            });
        List<CustomerPromotion> customerPromotion = new ArrayList<>();
        if (custPromoResult != null) {
            CRMAcresMessage resultSet = null;
            try {
                resultSet = JaxbContext.getInstance().unmarshal(custPromoResult, CRMAcresMessage.class);
            } catch (AppException exp) {
                logger.error("Failed to unmarshall the XML received from Patron in the CustomerPromotionsService call.", exp);
                throw Exceptions.wrap(exp, Errors.INVALID_DATA, "Failed to unmarshall the XML received from Patron in the CustomerPromotionsService call.");
            }
            if (resultSet != null && !resultSet.getBody().getPlayerPromos().isEmpty()) {
                customerPromotion = transformer.toLeft(resultSet);
            } else {

                if (resultSet.getBody().getError().getErrorCode().equals("INVALIDPLAYERID")) {
                    throw new AppException(ApplicationError.INVALID_PATRON_ID, "PlayerID / CardID is Invalid or not active " + mlifeno);
                } else {
                    logger.info("No Promos exist for Player:::", resultSet.getBody().getError().getErrorCode());
                    throw new AppException(ApplicationError.NO_PROMOTION, "No Promos exist for Player");
                }
            }
        }
        final PromotionsResponse promotionsResponse = new PromotionsResponse();
        promotionsResponse.setHeader(HeaderBuilder.buildHeader());
        promotionsResponse.setCustomerPromotions(customerPromotion);
        return promotionsResponse;
    }

    private String getCustPromotions(String mlifeno) throws AppException {
        String resultxml = null;
        try {
            List<Offers> playerPromotionList = customerPromotionsAccess.readPatronCustPromotions(Integer.parseInt(mlifeno));
            if (playerPromotionList != null) {
                resultxml = playerPromotionList.get(0).getResult();
            }
        } catch (AppException e) {
            throw new AppException(AppError.PATRON_ERROR, e, e.getMessage());
        }
        return resultxml;
    }

}