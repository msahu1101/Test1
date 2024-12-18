package com.mgmresorts.loyalty.data.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalancesInfo;

public class BalanceWrapperTests {

    @Test
    public void mapToResponseTierWrapperTest() {
        TierWrapper wrapper = new TierWrapper();
        CustomerBalances customerBalance = new CustomerBalances();
        wrapper.getTier().setPlayerTierName("Gold");
        wrapper.mapToResponseJson(customerBalance);
        assertEquals(wrapper.getTier(), customerBalance.getTier());
        assertEquals(wrapper.getTier().getPlayerTierName(), customerBalance.getTier().getPlayerTierName());
    }

    @Test
    public void getSetTierTest() {
        com.mgmresorts.loyalty.dto.customer.Tier tier = new com.mgmresorts.loyalty.dto.customer.Tier();
        TierWrapper wrapper = new TierWrapper();
        tier.setPlayerTierName("Gold");
        wrapper.setTier(tier);
        assertEquals(tier, wrapper.getTier());
        assertEquals(tier.getPlayerTierName(), wrapper.getTier().getPlayerTierName());
    }

    @Test
    public void mapToResponseBalanceWrapperTest() {
        BalanceWrapper wrapper = new BalanceWrapper();
        CustomerBalances customerBalance = new CustomerBalances();
        wrapper.getBalance().setRewardBalance(1698);
        wrapper.mapToResponseJson(customerBalance);
        assertEquals(wrapper.getBalance(), customerBalance.getBalance());
        assertEquals(wrapper.getBalance().getRewardBalance(), customerBalance.getBalance().getRewardBalance());
    }

    @Test
    public void getSetBalanceTest() {
        com.mgmresorts.loyalty.dto.customer.Balance balance = new com.mgmresorts.loyalty.dto.customer.Balance();
        BalanceWrapper wrapper = new BalanceWrapper();
        balance.setRewardBalance(399);
        wrapper.setBalance(balance);
        assertEquals(balance, wrapper.getBalance());
        assertEquals(balance.getRewardBalance(), wrapper.getBalance().getRewardBalance());
    }

    @Test
    public void mapToResponseGiftBalancesWrapperTest() {
        GiftBalancesWrapper wrapper = new GiftBalancesWrapper();
        CustomerBalances customerBalance = new CustomerBalances();
        GiftPointBalances giftPointBalances = new GiftPointBalances();
        giftPointBalances.setProgramId(4);
        wrapper.getGiftPointBalancesInfo().getGiftPointBalances().add(giftPointBalances);
        wrapper.mapToResponseJson(customerBalance);
        assertEquals(wrapper.getGiftPointBalancesInfo(), customerBalance.getGiftPointBalancesInfo());
        assertEquals(wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(0).getProgramId(),
                customerBalance.getGiftPointBalancesInfo().getGiftPointBalances().get(0).getProgramId());
    }

    @Test
    public void getSetGiftBalancesTest() {
        GiftBalancesWrapper wrapper = new GiftBalancesWrapper();
        GiftPointBalances giftPointBalances = new GiftPointBalances();
        GiftPointBalancesInfo giftPointBalancesInfo = new GiftPointBalancesInfo();
        giftPointBalances.setProgramId(4);
        giftPointBalancesInfo.getGiftPointBalances().add(giftPointBalances);
        wrapper.setGiftPointBalancesInfo(giftPointBalancesInfo);
        assertEquals(giftPointBalancesInfo, wrapper.getGiftPointBalancesInfo());
        assertEquals(giftPointBalancesInfo.getGiftPointBalances().get(0).getProgramId(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(0).getProgramId());
    }

    @Test
    public void mapToResponseGiftPointsWrapperTest() {
        GiftPointsWrapper wrapper = new GiftPointsWrapper();
        CustomerBalances customerBalance = new CustomerBalances();
        wrapper.getGiftPoints().setGiftPointsLinked(583d);
        wrapper.mapToResponseJson(customerBalance);
        assertEquals(wrapper.getGiftPoints(), customerBalance.getGiftPoints());
        assertEquals(wrapper.getGiftPoints().getGiftPointsLinked(), customerBalance.getGiftPoints().getGiftPointsLinked());
    }

    @Test
    public void getSetGiftPointsTest() {
        com.mgmresorts.loyalty.dto.customer.GiftPoints giftPoints = new com.mgmresorts.loyalty.dto.customer.GiftPoints();
        GiftPointsWrapper wrapper = new GiftPointsWrapper();
        giftPoints.setGiftPointsLinked(583d);
        wrapper.setGiftPoints(giftPoints);
        assertEquals(giftPoints, wrapper.getGiftPoints());
        assertEquals(giftPoints.getGiftPointsLinked(), wrapper.getGiftPoints().getGiftPointsLinked());
    }
}
