package com.mgmresorts.loyalty.data.entity;

public class SlotDollarBalance {
    public static final String SLOT_DOLLAR_BALANCE_CALL = "slot-dollar-balance-procedure";

    private String playerId;
    private int slotDollars;

    public String getPlayerId() {
        return String.valueOf(this.playerId);
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public SlotDollarBalance withPlayerId(String playerId) {
        this.playerId = playerId;
        return this;
    }

    public int getSlotDollars() {
        return this.slotDollars;
    }

    public void setSlotDollars(int slotDollars) {
        this.slotDollars = slotDollars;
    }

    public SlotDollarBalance withSlotDollars(int slotDollars) {
        this.slotDollars = slotDollars;
        return this;
    }

    public String toString() {
        return String.format("[playerId = {}, pointsBalance = {}", this.playerId, this.slotDollars);
    }
}
