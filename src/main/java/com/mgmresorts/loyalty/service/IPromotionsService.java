package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.PromotionsResponse;

public interface IPromotionsService {

    PromotionsResponse getCustomerOffers(String mlifeno) throws AppException;
}
