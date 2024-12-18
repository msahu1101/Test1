package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.Offers;

public interface IPromotionsAccess {

    List<Offers> readPatronCustPromotions(int mlifeNumber) throws AppException;
}
