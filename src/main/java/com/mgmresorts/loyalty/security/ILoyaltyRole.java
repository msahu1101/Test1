package com.mgmresorts.loyalty.security;

import com.mgmresorts.common.security.scope.IRole;

public interface ILoyaltyRole extends IRole {

    String READ_STOPCODE = "loyalty:stopcodes:read";
    String READ_BALANCE = "loyalty:balances:read";
    String READ_COMMENT = "loyalty:comments:read";
    String READ_LINK = "loyalty:linkedprofiles:read";
    String READ_PROMO = "loyalty:promos:read";
    String READ_TAX = "loyalty:taxinfo:read";
    String READ_LOYALTY = "loyalty:admin";
    String READ_PROFILE = "loyalty:profile:read";
    String UPDATE_PROFILE = "loyalty:profile:update";
    String READ_PROFILE_MOBILE = "profile:core";

}
