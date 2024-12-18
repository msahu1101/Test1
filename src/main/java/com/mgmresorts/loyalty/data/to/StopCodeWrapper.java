package com.mgmresorts.loyalty.data.to;

import java.util.List;

import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;

public class StopCodeWrapper {
    private List<CustomerStopCode> customerStopCodes;

    public List<CustomerStopCode> getCustomerStopCodes() {
        return customerStopCodes;
    }

    public void setCustomerStopCodes(List<CustomerStopCode> customerStopCodes) {
        this.customerStopCodes = customerStopCodes;
    }

}
