package com.mgmresorts.loyalty.data.to;

import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceResponseWrapper;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.Tier;

public class TierWrapper implements ICustomerBalanceResponseWrapper {

    private Tier tier;

    public TierWrapper() {
        tier = new Tier();
    }

    @Override
    public void accept(ICustomerBalanceWrapperVisitor wrapperVisitor) {
        wrapperVisitor.visit(this);
    }

    @Override
    public void mapToResponseJson(CustomerBalances customerBalances) {
        customerBalances.setTier(tier);
    }

    public Tier getTier() {
        return tier;
    }

    public void setTier(Tier tier) {
        this.tier = tier;
    }
}
