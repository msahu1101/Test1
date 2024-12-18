package com.mgmresorts.loyalty.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;

public class CustomerBalanceGiftBalancesTransformerTest {
    private BalanceGiftBalancesTransformer customerBalanceGiftBalancesTransformer = new BalanceGiftBalancesTransformer();

    private static final List<GiftBalances> giftBalancesEntitys = new LinkedList<>();

    @BeforeAll
    public static void setUp() {
        for (int i = 0; i < 3; i++) {
            GiftBalances balance = new GiftBalances();

            balance.setStatus("A");
            balance.setExpired(0);
            balance.setPoints(2271.0);
            balance.setProgramName("2007 Holiday Gift Shoppe");
            balance.setProgramId(1);
            giftBalancesEntitys.add(balance);
        }
    }

    @Test
    public void toLeftPositiveTest() {
        GiftBalancesWrapper wrapper = customerBalanceGiftBalancesTransformer.toLeft(giftBalancesEntitys);
        assertEquals(giftBalancesEntitys.size(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().size());
        for (int i = 0; i < 3; i++) {
            assertEquals(giftBalancesEntitys.get(i).getStatus(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(i).getStatus());
            assertEquals(giftBalancesEntitys.get(i).getExpired(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(i).getExpired());
            assertEquals(giftBalancesEntitys.get(i).getPoints(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(i).getPoints());
            assertEquals(giftBalancesEntitys.get(i).getProgramName(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(i).getProgramName());
            assertEquals(giftBalancesEntitys.get(i).getProgramId(), wrapper.getGiftPointBalancesInfo().getGiftPointBalances().get(i).getProgramId());
        }
    }

    @Test
    public void toLeftNullListTest() {
        GiftBalancesWrapper wrapper = customerBalanceGiftBalancesTransformer.toLeft(null);
        assertEquals(0, wrapper.getGiftPointBalancesInfo().getGiftPointBalances().size());
    }

    @Test
    public void toLeftEmptyListTest() {
        GiftBalancesWrapper wrapper = customerBalanceGiftBalancesTransformer.toLeft(new ArrayList<>());
        assertEquals(0, wrapper.getGiftPointBalancesInfo().getGiftPointBalances().size());
    }

    @Test
    public void toLeftTypeTest() {
        assertEquals(GiftBalancesWrapper.class, customerBalanceGiftBalancesTransformer.getLeftType());
    }

    @Test
    public void toRightTest() {
        assertEquals(null, customerBalanceGiftBalancesTransformer.toRight(null));
    }
}
