package com.mgmresorts.loyalty.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mgmresorts.common.dto.services.OutHeaderSupport;

import javax.annotation.Generated;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "slotDollarBalance"
})
@Generated("jsonschema2pojo")
public class GetSlotDollarBalanceResponse  extends OutHeaderSupport implements Serializable {
    private SlotDollarBalance slotDollarBalance;

    private static final long serialVersionUID = 3958447775713563725L;

    @JsonProperty("slotDollarBalance")
    public SlotDollarBalance getSlotDollarBalance() {
        return slotDollarBalance;
    }

    public void setSlotDollarBalance(SlotDollarBalance slotDollarBalance) {
        this.slotDollarBalance = slotDollarBalance;
    }

    public GetSlotDollarBalanceResponse withSlotDollarBalance(SlotDollarBalance slotDollarBalance) {
        this.slotDollarBalance = slotDollarBalance;
        return this;
    }
}
