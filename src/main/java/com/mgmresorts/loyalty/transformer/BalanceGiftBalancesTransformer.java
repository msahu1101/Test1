package com.mgmresorts.loyalty.transformer;

import java.util.List;

import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalances;

public class BalanceGiftBalancesTransformer implements IBalanceTransformer<GiftBalancesWrapper, List<GiftBalances>> {

    @Override
    public GiftBalancesWrapper toLeft(List<GiftBalances> right) {
        GiftBalancesWrapper giftBalancesWrapper = new GiftBalancesWrapper();
        List<GiftPointBalances> balances = giftBalancesWrapper.getGiftPointBalancesInfo().getGiftPointBalances();
        if (right != null) {
            for (Object entity : right) {
                GiftBalances giftBalancesEntity = (GiftBalances) entity;
                GiftPointBalances giftPointBalances = new GiftPointBalances();
                giftPointBalances.setStatus(giftBalancesEntity.getStatus());
                giftPointBalances.setExpired(giftBalancesEntity.getExpired());
                giftPointBalances.setPoints(giftBalancesEntity.getPoints());
                giftPointBalances.setProgramName(giftBalancesEntity.getProgramName());
                giftPointBalances.setProgramId(giftBalancesEntity.getProgramId());
                balances.add(giftPointBalances);
            }
        }
        return giftBalancesWrapper;
    }

    @Override
    public Class<GiftBalancesWrapper> getLeftType() {
        return GiftBalancesWrapper.class;
    }

    @Override
    public List<GiftBalances> toRight(GiftBalancesWrapper left) {
        return null;
    }
}
