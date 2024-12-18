package com.mgmresorts.loyalty.transformer;

import java.util.ArrayList;
import java.util.List;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;

public class StopCodeTransformer implements ITransformer<List<CustomerStopCode>, List<StopCode>> {

    @Override
    public List<CustomerStopCode> toLeft(List<StopCode> stopCodes) {
        final List<CustomerStopCode> customerStopCodes = new ArrayList<>();
        if (stopCodes != null) {
            for (StopCode stopCode : stopCodes) {
                CustomerStopCode custStopCode = new CustomerStopCode();
                custStopCode.setId(stopCode.getId());
                custStopCode.setDescription(stopCode.getDescription() + ". " + stopCode.getInformation());
                custStopCode.setIsActive(stopCode.getIsActive());
                custStopCode.setPriority(stopCode.getPriority());
                customerStopCodes.add(custStopCode);
            }
        }
        return customerStopCodes;
    }

    @Override
    public List<StopCode> toRight(List<CustomerStopCode> left) {
        throw new AppRuntimeException(SystemError.UNSUPPORTED_OPERATION);
    }

}
