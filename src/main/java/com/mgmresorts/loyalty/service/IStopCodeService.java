package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;

public interface IStopCodeService extends IService {
    StopCodeResponse getCustomerStopCode(String mlife) throws AppException;

}