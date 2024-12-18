package com.mgmresorts.loyalty.data.to;


import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceResponseWrapper;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPoints;

public class GiftPointsWrapper implements ICustomerBalanceResponseWrapper {
    private GiftPoints giftPoints;

    public GiftPointsWrapper() {
        giftPoints = new GiftPoints();
    }

    @Override
    public void accept(ICustomerBalanceWrapperVisitor wrapperVisitor) {
        wrapperVisitor.visit(this);
    }

    @Override
    public void mapToResponseJson(CustomerBalances customerBalances) {
        customerBalances.setGiftPoints(giftPoints);
    }

    public GiftPoints getGiftPoints() {
        return giftPoints;
    }

    public void setGiftPoints(GiftPoints giftPoints) {
        this.giftPoints = giftPoints;
    }
}
