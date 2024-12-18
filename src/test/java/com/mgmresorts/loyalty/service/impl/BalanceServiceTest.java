package com.mgmresorts.loyalty.service.impl;

import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.loyalty.dto.customer.Balance;
import com.mgmresorts.loyalty.dto.customer.CustomerBalances;
import com.mgmresorts.loyalty.dto.customer.GiftPointBalancesInfo;
import com.mgmresorts.loyalty.dto.customer.GiftPoints;
import com.mgmresorts.loyalty.dto.customer.Tier;
import com.mgmresorts.loyalty.dto.services.BalancesResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.rcxplatform.pojo.GetMembersResponse;
import com.mgmresorts.rcxplatform.pojo.Level;
import com.mgmresorts.rcxplatform.pojo.Member;
import com.mgmresorts.rcxplatform.pojo.Personalization;
import com.mgmresorts.rcxplatform.pojo.Purse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BalanceServiceTest {
    @Spy
    private BalanceService balanceService;

    private final static String PURSE_NAME_REWARDS_POINTS = "Rewards Points";
    private final static String PURSE_NAME_TIER_CREDITS = "Tier Credits";
    private final static String PURSE_NAME_DEFERRED_DEBITS = "Deferred Debits";

    private final static double DEFAULT_PATRON_VALUE = 111.11;
    private final static double DEFAULT_RCX_VALUE = 777.77;

    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private final static ZonedDateTime DEFAULT_PATRON_ZDT = ZonedDateTime.now(ZONE_ID_UTC);
    private final static Instant DEFAULT_RCX_INSTANT = Instant.now();

    private final static int PATRON_ACHIEVED_DATE_OFFSET = -3;
    private final static int PATRON_EARNED_DATE_OFFSET = -1;
    private final static int PATRON_EXPIRED_DATE_OFFSET = 12;

    private final static int RCX_ACHIEVED_DATE_OFFSET = -5;
    private final static int RCX_EXPIRED_DATE_OFFSET = 15;

    private final static String DEFAULT_CURRENT_TIER = "Sapphire";
    private final static String DEFAULT_NEXT_TIER = "Pearl";
    private final static String DEFAULT_CREDIT_TO_NEXT_TIER = "20000";
    private final static String DEFAULT_EXPIRE_DATE = "2024-02-01T07:59:59.999Z[UTC]";

    @BeforeEach
    public void setUp() throws Exception {
        ErrorManager.load(Errors.class);
        System.setProperty("runtime.environment", "junit");
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void setDefaultTierValues_nullTier_returnsDefaults() {
        CustomerBalances balances = new CustomerBalances();
        Balance balance = new Balance();
        balance.setSecondCompBalance(DEFAULT_PATRON_VALUE);
        balance.setSecondCompBalanceLinked(DEFAULT_PATRON_VALUE);
        balances.setBalance(balance);

        balanceService.setDefaultTierValues(balances);

        verifyTierValues(balances);
    }

    @Test
    public void setDefaultTierValues_notNullTier_returnsDefaults() {
        CustomerBalances balances = new CustomerBalances();
        Balance balance = new Balance();
        balance.setPendingPointsBalance(12.3456);
        balance.setSecondCompBalance(DEFAULT_PATRON_VALUE);
        balance.setSecondCompBalanceLinked(DEFAULT_PATRON_VALUE);
        balances.setBalance(balance);
        balances.setTier(getPatronTier("Platinum", "12345", 54321));

        balanceService.setDefaultTierValues(balances);

        verifyTierValues(balances);
    }

    private void verifyTierValues(CustomerBalances balances) {
        Tier actual = balances.getTier();
        assertNotNull(actual, "balances.getTier");

        ZonedDateTime zdtExpireDate = ZonedDateTime.parse(DEFAULT_EXPIRE_DATE);

        assertEquals(DEFAULT_CURRENT_TIER, actual.getPlayerTierName(), "playerTierName");
        assertEquals(DEFAULT_NEXT_TIER, actual.getNextTier(), "nextTier");
        assertEquals(DEFAULT_CREDIT_TO_NEXT_TIER, actual.getCreditToNextTier(), "creditToNextTier");
        assertEquals(0, actual.getTierCredits(), "tierCredits");
        assertEquals(zdtExpireDate, actual.getExpireDate(), "expireDate");
        assertEquals(actual.getAchievedDate(), actual.getEarnedDate(), "earnedDate == achievedDate");
        verifyZonedDateTime(actual.getEarnedDate());

        assertEquals(0.000, balances.getBalance().getPendingPointsBalance(), "pendingPointsBalance");
    }

    private void verifyZonedDateTime(ZonedDateTime zdt) {
        assertEquals(ZoneOffset.UTC, zdt.getOffset(), "zone offset");
        assertTrue(zdt.toInstant().isAfter(Instant.now().minusSeconds(1)), "1 sec difference");
    }

    @Test
    public void setResponseWithRcxPendingPointsBalance_happyPath_returnsRcxValues() {
        CustomerBalances patronBalances = getDefaultPatronCustomerBalances();
        Member rcxMember = getDefaultMainMember();

        Purse rpPurse = rcxMember.getRewardsPoints();
        Purse ddPurse = rcxMember.getDeferredDebits();

        double expectedPendingPointsBalance = rpPurse.getBalance() - rpPurse.getAvailBalance() - ddPurse.getBalance();
        double initialPatronPendingPointsBalance = patronBalances.getBalance().getPendingPointsBalance();

        assertEquals(DEFAULT_PATRON_VALUE, initialPatronPendingPointsBalance, "initial Pending Points Balance");

        balanceService.setResponseWithRcxPendingPointsBalance(patronBalances, rcxMember);

        assertEquals(expectedPendingPointsBalance, patronBalances.getBalance().getPendingPointsBalance(), "final patron pendingPointsBalance");

        assertEquals(rpPurse.getBalance(), DEFAULT_RCX_VALUE, "rewards points balance");
        assertEquals(rpPurse.getAvailBalance(), DEFAULT_RCX_VALUE, "rewards points avail balance");
        assertEquals(ddPurse.getBalance(), DEFAULT_RCX_VALUE, "deferred debits balance");
    }

    @Test
    public void setResponseWithRcxPersonalizationInfo_happyPath_returnsRcxValues() {
        final String rcxNextTier = "new Next Tier";
        final int rcxCreditToNextTier = 777;
        CustomerBalances patronBalances = getDefaultPatronCustomerBalances();
        Personalization personalization = getPersonalization(rcxNextTier, rcxCreditToNextTier);

        assertEquals("inital patron nextTier", patronBalances.getTier().getNextTier(), "initial patron nextTier");
        assertEquals("initial patron creditsToNextTier", patronBalances.getTier().getCreditToNextTier(), "initial patron creditsToNextTier");

        balanceService.setResponseWithRcxPersonalizationInfo(patronBalances, personalization);

        assertEquals(rcxNextTier, patronBalances.getTier().getNextTier(), "final patron nextTier");
        assertEquals(String.valueOf(rcxCreditToNextTier), patronBalances.getTier().getCreditToNextTier(), "final patron creditToNextTier");
        assertEquals(rcxNextTier, personalization.getNextTier(), "final rcx nextTier");
        assertEquals(rcxCreditToNextTier, personalization.getCreditsToNextTier(), "final rcx creditsToNextTier");
    }

    @Test
    public void setResponseWithRcxCompBalanceInfo_mainOnly_returnsRcxValues() {
        CustomerBalances patronCustomerBalances = getDefaultPatronCustomerBalances();
        GetMembersResponse getMembersResponse = getDefaultGetMembersResponse(false);

        assertEquals((int) DEFAULT_PATRON_VALUE, patronCustomerBalances.getTier().getTierCredits(), "initial patron tier credits value");
        assertEquals(DEFAULT_PATRON_VALUE, patronCustomerBalances.getBalance().getSecondCompBalance(), "initial patron second comp balance value");
        assertEquals(DEFAULT_PATRON_VALUE, patronCustomerBalances.getBalance().getSecondCompBalanceLinked(), "initial patron second comp balance linked value");

        balanceService.setResponseWithRcxCompBalanceInfo(patronCustomerBalances, getMembersResponse);

        assertEquals((int) DEFAULT_RCX_VALUE, patronCustomerBalances.getTier().getTierCredits(), "final response tier credits value");
        assertEquals(DEFAULT_RCX_VALUE / 100, patronCustomerBalances.getBalance().getSecondCompBalance(), "final response second comp balance");
        assertEquals(DEFAULT_RCX_VALUE / 100, patronCustomerBalances.getBalance().getSecondCompBalanceLinked(), "final response second comp balance linked");

        assertEquals(DEFAULT_RCX_VALUE, getMembersResponse.getMainMember().getTierCredits().getAvailBalance(), "final rcx tier credits value");
        assertEquals(DEFAULT_RCX_VALUE, getMembersResponse.getMainMember().getRewardsPoints().getAvailBalance(), "final rcx second comp balance linked");
    }

    @Test
    public void setResponseWithRcxCompBalanceInfo_withLinked_returnsRcxValues() {
        CustomerBalances patronCustomerBalances = getDefaultPatronCustomerBalances();
        GetMembersResponse getMembersResponse = getDefaultGetMembersResponse(true);

        Balance patronBalance = patronCustomerBalances.getBalance();
        Tier patronTier = patronCustomerBalances.getTier();

        assertEquals((int) DEFAULT_PATRON_VALUE, patronTier.getTierCredits(), "initial patron tier credits value");
        assertEquals(DEFAULT_PATRON_VALUE, patronBalance.getSecondCompBalance(), "initial patron second comp balance value");
        assertEquals(DEFAULT_PATRON_VALUE, patronBalance.getSecondCompBalanceLinked(), "initial patron second comp balance linked value");

        verifyRcxPurses("mainMember initial", getMembersResponse.getMainMember(), DEFAULT_RCX_VALUE);
        verifyRcxPurses("linkedMember initial", getMembersResponse.getLinkedMember(), 2 * DEFAULT_RCX_VALUE);

        balanceService.setResponseWithRcxCompBalanceInfo(patronCustomerBalances, getMembersResponse);

        assertEquals((int) (3 * DEFAULT_RCX_VALUE), patronTier.getTierCredits(), "final response tier credits value");
        assertEquals(DEFAULT_RCX_VALUE / 100, patronBalance.getSecondCompBalance(), "final response second comp balance");
        assertEquals((3 * DEFAULT_RCX_VALUE) / 100, patronBalance.getSecondCompBalanceLinked(), "final response second comp balance linked");

        verifyRcxPurses("mainMember final", getMembersResponse.getMainMember(), DEFAULT_RCX_VALUE);
        verifyRcxPurses("linkedMember final", getMembersResponse.getLinkedMember(), 2 * DEFAULT_RCX_VALUE);
    }

    @Test
    public void setResponseWithRcxCompBalanceInfo_zeroAvailableBalance_returnsRcxValues() {
        CustomerBalances patronCustomerBalances = getDefaultPatronCustomerBalances();
        GetMembersResponse getMembersResponse = getDefaultGetMembersResponse(true);
        getMembersResponse.getMainMember().getRewardsPoints().setAvailBalance(0.0);
        getMembersResponse.getLinkedMember().getRewardsPoints().setAvailBalance(0.0);

        Balance patronBalance = patronCustomerBalances.getBalance();
        Tier patronTier = patronCustomerBalances.getTier();

        assertEquals((int) DEFAULT_PATRON_VALUE, patronTier.getTierCredits(), "initial patron tier credits value");
        assertEquals(DEFAULT_PATRON_VALUE, patronBalance.getSecondCompBalance(), "initial patron second comp balance value");
        assertEquals(DEFAULT_PATRON_VALUE, patronBalance.getSecondCompBalanceLinked(), "initial patron second comp balance linked value");

        verifyRcxPurses("mainMember initial", getMembersResponse.getMainMember(), DEFAULT_RCX_VALUE, 0.0);
        verifyRcxPurses("linkedMember initial", getMembersResponse.getLinkedMember(), 2 * DEFAULT_RCX_VALUE, 0.0);

        balanceService.setResponseWithRcxCompBalanceInfo(patronCustomerBalances, getMembersResponse);

        assertEquals((int) (3 * DEFAULT_RCX_VALUE), patronTier.getTierCredits(), "final response tier credits value");
        assertEquals(0, patronBalance.getSecondCompBalance(), "final response second comp balance");
        assertEquals(0, patronBalance.getSecondCompBalanceLinked(), "final response second comp balance linked");

        verifyRcxPurses("mainMember final", getMembersResponse.getMainMember(), DEFAULT_RCX_VALUE, 0.0);
        verifyRcxPurses("linkedMember final", getMembersResponse.getLinkedMember(), 2 * DEFAULT_RCX_VALUE, 0.0);
    }

    private void verifyRcxPurses(String desc, Member member, double expectedTierCredits, double expectedRewardsPoints) {
        assertEquals(expectedTierCredits, member.getTierCredits().getAvailBalance(), desc +" final rcx tier credits avail balance value");
        assertEquals(expectedRewardsPoints, member.getRewardsPoints().getAvailBalance(), desc + " final rcx rp avail balance value");
    }

    private void verifyRcxPurses(String desc, Member member, double expectedValue) {
        verifyRcxPurses(desc, member, expectedValue, expectedValue);
     }

    @Test
    public void setResponseWithRcxTierInfo_happyPath_returnsRcxValues() {
        CustomerBalances customerBalances = getDefaultPatronCustomerBalances();
        Member mainMember = getDefaultMainMember();

        Tier patronTier = customerBalances.getTier();
        assertEquals("default-patron-player-tier-name", patronTier.getPlayerTierName(), "initial patron player tier name");
        assertEquals("default-patron-previous-tier", patronTier.getPreviousTier(), "initial patron previous tier");
        assertEquals(DEFAULT_PATRON_ZDT.plus(Period.ofMonths(PATRON_ACHIEVED_DATE_OFFSET)), patronTier.getAchievedDate(), "initial patron achieved date");
        assertEquals(DEFAULT_PATRON_ZDT.plus(Period.ofMonths(PATRON_EARNED_DATE_OFFSET)), patronTier.getEarnedDate(), "initial patron earned date");
        assertEquals(DEFAULT_PATRON_ZDT.plus(Period.ofMonths(PATRON_EXPIRED_DATE_OFFSET)), patronTier.getExpireDate(), "initial patron expire date");

        balanceService.setResponseWithRcxTierInfo(customerBalances, mainMember);

        ZonedDateTime rcxDefaultZdt = DEFAULT_RCX_INSTANT.atZone(ZONE_ID_UTC);
        assertEquals("rcx-default-level-name", patronTier.getPlayerTierName(), "final patron player tier name");
        assertEquals("rcx-default-prev-level-name", patronTier.getPreviousTier(), "final patron previous tier");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_ACHIEVED_DATE_OFFSET)), patronTier.getAchievedDate(), "final patron achieved date");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_ACHIEVED_DATE_OFFSET)), patronTier.getEarnedDate(), "final patron earned date");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_EXPIRED_DATE_OFFSET)), patronTier.getExpireDate(), "final patron expire date");
    }

    @Test
    public void setResponseWithRcxTierInfo_nullPatronTier_returnsRcxValues() {
        CustomerBalances customerBalances = getDefaultPatronCustomerBalances();
        customerBalances.setTier(new Tier());
        Member mainMember = getDefaultMainMember();

        Tier patronTier = customerBalances.getTier();
        assertNull(patronTier.getPlayerTierName(), "initial patron player tier name");
        assertNull(patronTier.getPreviousTier(), "initial patron previous tier");
        assertNull(patronTier.getAchievedDate(), "initial patron achieved date");
        assertNull(patronTier.getEarnedDate(), "initial patron earned date");
        assertNull(patronTier.getExpireDate(), "initial patron expire date");

        balanceService.setResponseWithRcxTierInfo(customerBalances, mainMember);

        ZonedDateTime rcxDefaultZdt = DEFAULT_RCX_INSTANT.atZone(ZONE_ID_UTC);
        assertEquals("rcx-default-level-name", patronTier.getPlayerTierName(), "final patron player tier name");
        assertEquals("rcx-default-prev-level-name", patronTier.getPreviousTier(), "final patron previous tier");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_ACHIEVED_DATE_OFFSET)), patronTier.getAchievedDate(), "final patron achieved date");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_ACHIEVED_DATE_OFFSET)), patronTier.getEarnedDate(), "final patron earned date");
        assertEquals(rcxDefaultZdt.plus(Period.ofDays(RCX_EXPIRED_DATE_OFFSET)), patronTier.getExpireDate(), "final patron expire date");
    }

    private CustomerBalances getDefaultPatronCustomerBalances() {
        Balance balance = new Balance();
        balance.setPendingPointsBalance(DEFAULT_PATRON_VALUE);
        balance.setSecondCompBalance(DEFAULT_PATRON_VALUE);
        balance.setSecondCompBalanceLinked(DEFAULT_PATRON_VALUE);

        Tier patronTier = getPatronTier("inital patron nextTier", "initial patron creditsToNextTier", (int) DEFAULT_PATRON_VALUE);

        CustomerBalances customerBalances = new CustomerBalances();
        customerBalances.setBalance(balance);
        customerBalances.setTier(patronTier);

        return customerBalances;
    }

    private Tier getPatronTier(String nextTier, String creditsToNextTier, int tierCredits) {
        Tier tier = new Tier();
        tier.setNextTier(nextTier);
        tier.setCreditToNextTier(creditsToNextTier);
        tier.setTierCredits(tierCredits);
        tier.setPlayerTierName("default-patron-player-tier-name");
        tier.setPreviousTier("default-patron-previous-tier");

        tier.setAchievedDate(DEFAULT_PATRON_ZDT.plusMonths(-3));
        tier.setEarnedDate(DEFAULT_PATRON_ZDT.plusMonths(-1));
        tier.setExpireDate(DEFAULT_PATRON_ZDT.plusMonths(12));

        return tier;
    }

    private GetMembersResponse getDefaultGetMembersResponse(boolean includeLinkedMember) {
        GetMembersResponse membersResponse = new GetMembersResponse();
        membersResponse.getMembers().add(getDefaultMainMember());

        if (includeLinkedMember) {
            membersResponse.getMembers().add(getDefaultLinkedMember());
        }

        return membersResponse;
    }

    private Personalization getPersonalization(String nextTier, int creditsToNextTier) {
        return new Personalization().withNextTier(nextTier).withCreditsToNextTier(creditsToNextTier);
    }

    private com.mgmresorts.rcxplatform.pojo.Tier getDefaultRcxTier() {
        return new com.mgmresorts.rcxplatform.pojo.Tier()
            .withAchievedOn(DEFAULT_RCX_INSTANT.plus(RCX_ACHIEVED_DATE_OFFSET, ChronoUnit.DAYS).toString())
            .withRequalsOn(DEFAULT_RCX_INSTANT.plus(RCX_EXPIRED_DATE_OFFSET, ChronoUnit.DAYS).toString())
            .withPrevLevelName("rcx-default-prev-level-name")
            .withLevel((new Level()).withName("rcx-default-level-name"));
    }

    private Member getDefaultLinkedMember() {
        Member member = new Member();
        double defaultLinkedValue = 2 * DEFAULT_RCX_VALUE;
        member.getPurses().add(getPurse(PURSE_NAME_DEFERRED_DEBITS, defaultLinkedValue, defaultLinkedValue));
        member.getPurses().add(getPurse(PURSE_NAME_REWARDS_POINTS, defaultLinkedValue, defaultLinkedValue));
        member.getPurses().add(getPurse(PURSE_NAME_TIER_CREDITS, defaultLinkedValue, defaultLinkedValue));

        return member;
    }

    private Member getDefaultMainMember() {
        Member member = new Member();
        member.setPurses(getDefaultPurses());
        member.getTiers().add(getDefaultRcxTier());

        return member;
    }

    private List<Purse> getDefaultPurses() {
        List<Purse> purses = new ArrayList<>();
        purses.add(getDefaultPurse(PURSE_NAME_TIER_CREDITS));
        purses.add(getDefaultPurse(PURSE_NAME_REWARDS_POINTS));
        purses.add(getDefaultPurse(PURSE_NAME_DEFERRED_DEBITS));

        return purses;
    }

    private Purse getDefaultPurse(String purseName) {
        return getPurse(purseName, DEFAULT_RCX_VALUE, DEFAULT_RCX_VALUE);
    }

    private Purse getPurse(String purseName, double balance, double availBalance) {
        Purse newPurse = new Purse();
        newPurse.setName(purseName);
        newPurse.setBalance(balance);
        newPurse.setAvailBalance(availBalance);
        newPurse.setPolicyId(purseName + " - POLICY_ID");

        return newPurse;
    }

    private BalancesResponse getBalancesResponse() {
        CustomerBalances customerBalances = new CustomerBalances();
        BalancesResponse getBalancesResponse = new BalancesResponse();
        GiftPointBalancesInfo giftPointBalancesInfo = new GiftPointBalancesInfo();
        customerBalances.setGiftPointBalancesInfo(giftPointBalancesInfo);
        GiftPoints giftPoints = new GiftPoints();
        customerBalances.setGiftPoints(giftPoints);
        Tier tier = new Tier();
        customerBalances.setTier(tier);
        getBalancesResponse.setCustomerBalances(customerBalances);
        return getBalancesResponse;
    }
}
