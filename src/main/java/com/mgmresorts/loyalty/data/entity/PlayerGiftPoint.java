package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "GiftPoints.dbo.PlayerGiftPoint")
@Entity
@IdClass(PlayerGiftPointId.class)
public class PlayerGiftPoint {
    @Id
    @Column(name = "PlayerID")
    private int playerId;

    @Id
    @Column(name = "GiftProgramID")
    private int giftProgramId;

    @ManyToOne(targetEntity = GiftProgram.class)
    @JoinColumn(name = "GiftProgramID", referencedColumnName = "GiftProgramID", insertable = false, updatable = false)
    private GiftProgram giftProgram;

    @Column(name = "EarnedGiftPoint")
    private double earnedGiftPoint;

    @Column(name = "AddedGiftPoint")
    private double addedGiftPoint;

    @Column(name = "DecreasedGiftPoint")
    private double decreasedGiftPoint;

    @Column(name = "TransferGiftPointIn")
    private double transferGiftPointIn;

    @Column(name = "TransferGiftPointOut")
    private double transferGiftPointOut;

    @Column(name = "RemovedGiftPoint")
    private double removedGiftPoint;

    @Column(name = "RedeemedGiftPoint")
    private double redeemedGiftPoint;

    @Column(name = "Status")
    private String status;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getGiftProgramId() {
        return giftProgramId;
    }

    public void setGiftProgramId(int giftProgramId) {
        this.giftProgramId = giftProgramId;
    }

    public GiftProgram getGiftProgram() {
        return giftProgram;
    }

    public void setGiftProgram(GiftProgram giftProgram) {
        this.giftProgram = giftProgram;
    }

    public double getEarnedGiftPoint() {
        return earnedGiftPoint;
    }

    public void setEarnedGiftPoint(double earnedGiftPoint) {
        this.earnedGiftPoint = earnedGiftPoint;
    }

    public double getAddedGiftPoint() {
        return addedGiftPoint;
    }

    public void setAddedGiftPoint(double addedGiftPoint) {
        this.addedGiftPoint = addedGiftPoint;
    }

    public double getDecreasedGiftPoint() {
        return decreasedGiftPoint;
    }

    public void setDecreasedGiftPoint(double decreasedGiftPoint) {
        this.decreasedGiftPoint = decreasedGiftPoint;
    }

    public double getTransferGiftPointIn() {
        return transferGiftPointIn;
    }

    public void setTransferGiftPointIn(double transferGiftPointIn) {
        this.transferGiftPointIn = transferGiftPointIn;
    }

    public double getTransferGiftPointOut() {
        return transferGiftPointOut;
    }

    public void setTransferGiftPointOut(double transferGiftPointOut) {
        this.transferGiftPointOut = transferGiftPointOut;
    }

    public double getRemovedGiftPoint() {
        return removedGiftPoint;
    }

    public void setRemovedGiftPoint(double removedGiftPoint) {
        this.removedGiftPoint = removedGiftPoint;
    }

    public double getRedeemedGiftPoint() {
        return redeemedGiftPoint;
    }

    public void setRedeemedGiftPoint(double redeemedGiftPoint) {
        this.redeemedGiftPoint = redeemedGiftPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[PlayerId = " + getPlayerId() + " , GiftProgramId = " + getGiftProgramId() + " , GiftProgram = "
                + giftProgram.toString() + " , EarnedGiftPoint = " + getEarnedGiftPoint() + " , AddedGiftPoint = "
                + getAddedGiftPoint() + " , DecreasedGiftPoint = " + getDecreasedGiftPoint()
                + " , TransferGiftPointIn = " + getTransferGiftPointIn() + " , TransferGiftPointOut = "
                + getTransferGiftPointOut() + " , RemovedGiftPoint = " + getRemovedGiftPoint()
                + " , redeemedGiftPoint = " + getRedeemedGiftPoint() + " , Status = " + getStatus() + "]";
    }

}
