package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedQuery;

@NamedQuery(name = GiftBalances.GIFT_BALANCES_QUERY, query = "SELECT new com.mgmresorts.loyalty.data.entity.GiftBalances (pgp.giftProgramId, pgp.giftProgram.giftProgramTitle,"
        + " pgp.status, 0, ((pgp.earnedGiftPoint+pgp.addedGiftPoint+pgp.transferGiftPointIn)"
        + "-(pgp.decreasedGiftPoint+pgp.transferGiftPointOut+pgp.removedGiftPoint+pgp.redeemedGiftPoint)) ) "
        + "FROM PlayerGiftPoint pgp WHERE pgp.playerId = ?1", lockMode = LockModeType.NONE)
@Entity
public class GiftBalances {
    public static final String GIFT_BALANCES_QUERY = "customer-gift-balace-query";

    public GiftBalances() {
    }

    public GiftBalances(Integer giftProgramId, String giftProgramTitle, String status, Integer expired, Double points) {
        this.programId = giftProgramId;
        this.programName = giftProgramTitle;
        this.status = status;
        this.expired = expired;
        this.points = points;
    }

    private String status;

    private Integer expired;

    private Double points;

    private String programName;
    @Id
    private Integer programId;

    public Double getPoints() {
        return points;
    }

    public Integer getExpired() {
        return expired;
    }

    public Integer getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public String getStatus() {
        return status;
    }

    public void setExpired(Integer expired) {
        this.expired = expired;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[ProgramId = " + getProgramId() + " , ProgramName = " + " , Status = " + getStatus() + " , Expired = " + getExpired() + " , Points" + getPoints() + "Status"
                + getStatus() + "Expire" + getExpired() + "]";
    }
}
