package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.TaxInfoResponse;

public interface ITaxInfoService extends IService {

    TaxInfoResponse readTaxInfoCustomerResponse(String mlifeNumber, String year, String quarter) throws AppException;
}
