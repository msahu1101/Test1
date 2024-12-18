package com.mgmresorts.loyalty.data.impl;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.ITaxInfoAccess;
import com.mgmresorts.loyalty.data.entity.TaxInformation;
import com.mgmresorts.loyalty.data.support.SqlSupport;

import java.util.List;

public class TaxInfoAccess extends SqlSupport<TaxInformation, String> implements ITaxInfoAccess {
    
    @Override
    public List<TaxInformation> readPatronTaxInfoCustomerResponse(int player, int year, int quarter) throws AppException {
        
        List<TaxInformation> taxInformations = callByNamedProcedure(DB.REPORTING, TaxInformation.TAX_INFORMATION_CALL, TaxInformation.class, (sp) -> {
            sp.setParameter(1, player);
            sp.setParameter(2, String.valueOf(year));
            sp.setParameter(3, quarter);
        });
        
        return taxInformations;
    }

    @Override
    public Class<TaxInformation> getEntityType() {
        return TaxInformation.class;
    }

}
