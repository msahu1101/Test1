package com.mgmresorts.loyalty.dto.customer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.mgmresorts.common.utils.JSonMapper;

import javax.annotation.Generated;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "playerId",
    "slotDollars"
})
@Generated("jsonschema2pojo")
public class SlotDollarBalance implements Serializable {
    private String playerId;
    private int slotDollars = 0;
    private static final long serialVersionUID = 3958447775713563725L;

    @JsonProperty("playerId")
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public SlotDollarBalance withPlayerId(String playerId) {
        this.playerId = playerId;
        return this;
    }

    @JsonProperty("slotDollars")
    public int getSlotDollars() {
        return slotDollars;
    }

    public void setSlotDollars(int slotDollars) {
        this.slotDollars = slotDollars;
    }

    public SlotDollarBalance withSlotDollars(int slotDollars) {
        this.slotDollars = slotDollars;
        return this;
    }

    public String toJson() {
        JSonMapper mapper = new JSonMapper();
        return mapper.asJsonString(this);
    }
}
