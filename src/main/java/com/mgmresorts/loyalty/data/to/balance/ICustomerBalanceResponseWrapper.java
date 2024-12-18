package com.mgmresorts.loyalty.data.to.balance;

import com.mgmresorts.loyalty.dto.customer.CustomerBalances;

public interface ICustomerBalanceResponseWrapper {

    void mapToResponseJson(CustomerBalances customerBalances);

    void accept(ICustomerBalanceWrapperVisitor wrapperVisitor);
}
