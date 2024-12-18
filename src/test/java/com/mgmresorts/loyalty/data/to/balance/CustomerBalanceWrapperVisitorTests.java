package com.mgmresorts.loyalty.data.to.balance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;

public class CustomerBalanceWrapperVisitorTests {

    @Mock
    CustomerBalances customerBalances = Mockito.mock(CustomerBalances.class);

    @InjectMocks
    CustomerBalanceWrapperVisitor customerBalanceWrapperVisitor = new CustomerBalanceWrapperVisitor();

    @Test
    public void balanceVisitTest(){
        BalanceWrapper balanceWrapper = new BalanceWrapper();
        customerBalanceWrapperVisitor.visit(balanceWrapper);
        assertEquals(balanceWrapper.getBalance(), customerBalanceWrapperVisitor.getCustomerBalances().getBalance());
    }

    @Test
    public void tierVisitTest(){
        TierWrapper tierWrapper = new TierWrapper();
        customerBalanceWrapperVisitor.visit(tierWrapper);
        assertEquals(tierWrapper.getTier(), customerBalanceWrapperVisitor.getCustomerBalances().getTier());
    }

    @Test
    public void giftPointsVisitTest(){
        GiftPointsWrapper giftPointsWrapper = new GiftPointsWrapper();
        customerBalanceWrapperVisitor.visit(giftPointsWrapper);
        assertEquals(giftPointsWrapper.getGiftPoints(), customerBalanceWrapperVisitor.getCustomerBalances().getGiftPoints());
    }

    @Test
    public void giftBalancesVisitTest(){
        GiftBalancesWrapper giftBalancesWrapper = new GiftBalancesWrapper();
        customerBalanceWrapperVisitor.visit(giftBalancesWrapper);
        assertEquals(giftBalancesWrapper.getGiftPointBalancesInfo(), customerBalanceWrapperVisitor.getCustomerBalances().getGiftPointBalancesInfo());
    }

    @Test
    public void getCustomerBalancesTest(){
        assertNotEquals(null, customerBalanceWrapperVisitor.getCustomerBalances());
    }
}
