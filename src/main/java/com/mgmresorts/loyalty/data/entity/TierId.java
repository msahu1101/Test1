package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class TierId {
    @Column(name = "PlayerIdentification")
    private Integer playerId;

    @Column(name = "TierId")
    private Integer tierId;

    public Integer getPlayerId() {
        return playerId;
    }

    public Integer getTierId() {
        return tierId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public void setTierId(Integer tierId) {
        this.tierId = tierId;
    }

    @Override
    public String toString() {
        return "[PlayerIdentification = " + getPlayerId() + " , TierId = " + getTierId() + "]";
    }
}
