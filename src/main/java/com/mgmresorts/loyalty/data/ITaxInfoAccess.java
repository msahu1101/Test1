package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.TaxInformation;

public interface ITaxInfoAccess {

    List<TaxInformation> readPatronTaxInfoCustomerResponse(int mlifeNumber, int year, int quarter) throws AppException;

}
