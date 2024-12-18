package com.mgmresorts.loyalty.task.balance;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mgmresorts.common.utils.ThreadContext.TransactionContext;
import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.entity.Tier;
import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;

public interface ICustomerBalanceTaskFactory {

    @Named("balance")
    CustomerBalanceTask<BalanceWrapper, Balance> createBalanceTask(@Assisted("player") int player, @Assisted("site") int site, String name, TransactionContext context);

    @Named("gift points")
    CustomerBalanceTask<GiftPointsWrapper, GiftPoints> createGiftPointsTask(@Assisted("player") int player, @Assisted("site") int site, String name, TransactionContext context);

    @Named("gift balances")
    CustomerBalanceTask<GiftBalancesWrapper, GiftBalances> createGiftBalancesTask(@Assisted("player") int player, @Assisted("site") int site, String name,
            TransactionContext context);

    @Named("tier")
    CustomerBalanceTask<TierWrapper, Tier> createTierTask(@Assisted("player") int player, @Assisted("site") int site, String name, TransactionContext context);
}
