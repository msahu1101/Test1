package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.PromoEventsBlockResponse;

public interface IPromoEventsService {

    public PromoEventsBlockResponse getPromoEventBlockInfo(String id, String type) throws AppException;

}
