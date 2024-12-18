package com.mgmresorts.loyalty.data.to;

import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceResponseWrapper;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalancesInfo;

public class GiftBalancesWrapper implements ICustomerBalanceResponseWrapper {
    private GiftPointBalancesInfo giftPointBalancesInfo;

    public GiftBalancesWrapper() {
        giftPointBalancesInfo = new GiftPointBalancesInfo();
    }

    @Override
    public void accept(ICustomerBalanceWrapperVisitor wrapperVisitor) {
        wrapperVisitor.visit(this);
    }

    @Override
    public void mapToResponseJson(CustomerBalances customerBalances) {
        customerBalances.setGiftPointBalancesInfo(giftPointBalancesInfo);
    }

    public GiftPointBalancesInfo getGiftPointBalancesInfo() {
        return giftPointBalancesInfo;
    }

    public void setGiftPointBalancesInfo(GiftPointBalancesInfo giftPointBalancesInfo) {
        this.giftPointBalancesInfo = giftPointBalancesInfo;
    }
}