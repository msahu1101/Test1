package com.mgmresorts.loyalty.data.entity;

public class PlayerGiftPointId {

    private int playerId;
    private int giftProgramId;

    public int getGiftProgramId() {
        return giftProgramId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setGiftProgramId(int giftProgramId) {
        this.giftProgramId = giftProgramId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public String toString() {
        return "[PlayerId = " + getPlayerId() + " , GiftProgramId = " + getGiftProgramId() + "]";
    }
}
