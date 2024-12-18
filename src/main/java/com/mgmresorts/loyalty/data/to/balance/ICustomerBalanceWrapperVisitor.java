package com.mgmresorts.loyalty.data.to.balance;

import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;

public interface ICustomerBalanceWrapperVisitor {

    void visit(BalanceWrapper balanceWrapper);

    void visit(GiftBalancesWrapper giftBalancesWrapper);

    void visit(GiftPointsWrapper giftPointsWrapper);

    void visit(TierWrapper tierWrapper);

    CustomerBalances getCustomerBalances();
}
