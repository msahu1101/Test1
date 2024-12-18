package com.mgmresorts.loyalty.function;

public interface IFunctions {

    static final String FUNCTION_ISSUE_PROMO = "issue-promo";
    static final String FUNCTION_ISSUE_PROMO_URL = "patron/issue-promo";

    static final String FUNCTION_PROMO_EVENTS = "promoevents";
    static final String FUNCTION_PROMO_EVENTS_URL = "patron/promo-events/{id}";

    static final String FUNCTION_SLOT_DOLLAR_BALANCE = "patron/slot-dollar-balance/{playerId}";
}
