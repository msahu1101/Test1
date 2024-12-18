package com.mgmresorts.loyalty.transformer;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.dto.customer.CustomerPromotion;
import com.mgmresorts.loyalty.dto.patron.customerpromo.CRMAcresMessage;
import com.mgmresorts.loyalty.dto.patron.customerpromo.PlayerPromos;
import com.mgmresorts.loyalty.dto.promo.PromoSiteInfo;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class PromoTransformer implements ITransformer<List<CustomerPromotion>, CRMAcresMessage> {

    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Override
    public List<CustomerPromotion> toLeft(CRMAcresMessage right) throws AppException {

        List<CustomerPromotion> customerPromotions = new ArrayList<>();
        if (right != null) {
            for (PlayerPromos playerPromo : right.getBody().getPlayerPromos()) {
                CustomerPromotion customerPromo = new CustomerPromotion();
                final PromoSiteInfo siteInfo = new PromoSiteInfo();

                if (!Utils.anyNull(playerPromo.getPromoID())) {
                    customerPromo.setPromoId(String.valueOf(playerPromo.getPromoID()));
                }
                customerPromo.setPublicDescription(playerPromo.getPublicDescription());

                customerPromo.setStartDate(Dates.toZonedDateTime(playerPromo.getBeginDate().toString(), ZONE_ID_UTC));
                customerPromo.setEndDate(Dates.toZonedDateTime(playerPromo.getEndDate().toString(), ZONE_ID_UTC));

                customerPromo.setStatus(playerPromo.getStatus());
                customerPromo.setName(playerPromo.getPromoName());
                siteInfo.setSiteDescription(playerPromo.getSiteDescription());
                if (!Utils.anyNull(playerPromo.getSiteID())) {
                    siteInfo.setSiteId(Byte.toString(playerPromo.getSiteID()));
                }

                customerPromo.setSiteInfo(siteInfo);
                customerPromotions.add(customerPromo);
            }
        }
        return customerPromotions;
    }

    @Override
    public CRMAcresMessage toRight(List<CustomerPromotion> left) throws AppException {
        throw new AppRuntimeException(SystemError.UNSUPPORTED_OPERATION);
    }
}
