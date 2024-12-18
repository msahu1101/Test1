package com.mgmresorts.loyalty.data.to.balance;

import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;


public class CustomerBalanceWrapperVisitor implements ICustomerBalanceWrapperVisitor {
    private CustomerBalances customerBalances;

    public CustomerBalanceWrapperVisitor() {
        this.customerBalances = new CustomerBalances();
    }

    public void visit(BalanceWrapper balanceWrapper) {
        customerBalances.setBalance(balanceWrapper.getBalance());
    }

    public void visit(TierWrapper tierWrapper) {
        customerBalances.setTier(tierWrapper.getTier());
    }

    public void visit(GiftBalancesWrapper giftBalancesWrapper) {
        customerBalances.setGiftPointBalancesInfo(giftBalancesWrapper.getGiftPointBalancesInfo());
    }

    public void visit(GiftPointsWrapper giftPointsWrapper) {
        customerBalances.setGiftPoints(giftPointsWrapper.getGiftPoints());
    }

    public CustomerBalances getCustomerBalances() {
        return customerBalances;
    }
}
