package com.mgmresorts.loyalty.data.to;

import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceResponseWrapper;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.dto.customer.Balance;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.SiteInfo;

public class BalanceWrapper implements ICustomerBalanceResponseWrapper {

    private Balance balance;

    public BalanceWrapper() {
        balance = new Balance();
        balance.setSites(new SiteInfo());
    }

    @Override
    public void accept(ICustomerBalanceWrapperVisitor wrapperVisitor) {
        wrapperVisitor.visit(this);
    }

    @Override
    public void mapToResponseJson(CustomerBalances customerBalances) {
        customerBalances.setBalance(balance);
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }
}
