package com.mgmresorts.loyalty.security;

import com.mgmresorts.common.security.scope.IRole;
import com.mgmresorts.common.security.scope.Role;

public class LoyaltyRole implements IRole, ILoyaltyRole {

    public static final Role READ_STOPCODE = new Role(ILoyaltyRole.READ_STOPCODE, "Scope to read stop codes of a customer");
    //public static final Role READ_BALANCE = new Role(ILoyaltyRole.READ_BALANCE, "Scope to read balances of a customer");
    public static final Role READ_COMMENT = new Role(ILoyaltyRole.READ_COMMENT, "Scope to read comments of a customer");
    public static final Role READ_LINK = new Role(ILoyaltyRole.READ_LINK, "Scope to read link players of a customer");
    public static final Role READ_PROMO = new Role(ILoyaltyRole.READ_PROMO, "Scope to read promotions of a customer");
    public static final Role READ_TAX = new Role(ILoyaltyRole.READ_TAX, "Scope to read tax information of a customer");
    public static final Role READ_PROFILE = new Role(ILoyaltyRole.READ_PROFILE, "Scope to read promo event info associated to the customer");
    public static final Role UPDATE_PROFILE = new Role(ILoyaltyRole.UPDATE_PROFILE, "Scope to issue tickets to the customer");
    public static final Role READ_PROFILE_MOBILE = new Role(ILoyaltyRole.READ_PROFILE_MOBILE, "Scope to read balance of a customers");
    public static final Role READ_BALANCE = new Role(ILoyaltyRole.READ_BALANCE, "Scope to read balances of a customer",READ_PROFILE_MOBILE);
    public static final Role LOYALTY_ADMIN = new Role(ILoyaltyRole.READ_LOYALTY, "Loyalty administration scope", READ_STOPCODE, READ_BALANCE, READ_COMMENT, READ_LINK, READ_PROMO,
            READ_TAX, READ_PROFILE, UPDATE_PROFILE);
}
