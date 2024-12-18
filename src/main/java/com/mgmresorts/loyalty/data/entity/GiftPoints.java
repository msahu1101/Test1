package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NamedStoredProcedureQuery(name = GiftPoints.GIFT_POINTS_CALL, procedureName = "GetHGSPlayerGiftPoints", resultClasses = GiftPoints.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), })

@Entity
public class GiftPoints {
    public static final String GIFT_POINTS_CALL = "customer-giftpoints-procedure";
    @Id
    @Column(name = "playerid1")
    private Integer playerId1;

    @Column(name = "ptstotal")
    private Double giftPointsLinked = null;

    @Column(name = "ptstotal1")
    private Double hgsPoints = null;

    public void setGiftPointsLinked(Double giftPointsLinked) {
        this.giftPointsLinked = giftPointsLinked;
    }

    public Double getGiftPointsLinked() {
        return giftPointsLinked;
    }

    public void setHgsPoints(Double hgsPoints) {
        this.hgsPoints = hgsPoints;
    }

    public Double getHgsPoints() {
        return hgsPoints;
    }

    public Integer getPlayerId1() {
        return playerId1;
    }

    public void setPlayerId1(Integer playerId1) {
        this.playerId1 = playerId1;
    }

    @Override
    public String toString() {
        return "[PlayerId = " + getPlayerId1() + " , HgsPoints = " + getHgsPoints() + " , GiftPointsLinked = "
                + getGiftPointsLinked() + "]";
    }
}
