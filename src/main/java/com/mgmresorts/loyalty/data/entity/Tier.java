package com.mgmresorts.loyalty.data.entity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.loyalty.errors.Errors;

@NamedStoredProcedureQuery(name = Tier.TIER_CALL, procedureName = "TIBCO_PlayerProfile_get", resultClasses = Tier.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), })

@Entity
public class Tier {
    public static final String TIER_CALL = "customer-tier-procedure";

    @EmbeddedId
    private TierId tierId;

    @Column(name = "Tier")
    private String playerTierName;

    @Column(name = "TierAchievedDate")
    private String achievedDate;

    @Column(name = "TierEarnedDate")
    private String earnedDate;

    @Column(name = "TierExpirationDate")
    private String expireDate;

    @Column(name = "NextTier")
    private String nextTier;

    @Column(name = "PreviousTier")
    private String previousTier;

    @Column(name = "CreditsToNextTier")
    private String creditToNextTier;

    @Column(name = "IsPlayerLinked")
    private String isPlayerLinked;

    @Column(name = "LinkTierCredit")
    private Integer linkTierCredit;

    @Column(name = "TierCredit")
    private Integer tierCredit;

    @Override
    public String toString() {
        return "[Tier = " + getPlayerTierName() + " , TierAchievedDate = " + getAchievedDate() + " , TierEarnedDate = " + getEarnedDate().toString() + " , TierExpirationDate = "
                + getExpireDate().toString() + " , NextTier = " + getNextTier() + " , PreviousTier = " + getPreviousTier() + " , CreditsToNextTier = " + getCreditToNextTier()
                + " , IsPlayerLinked = " + getIsPlayerLinked() + " , LinkTierCredit = " + getLinkTierCredit() + " , TierCredit = " + getTierCredit() + "]";
    }

    public TierId getTierId() {
        return tierId;
    }

    public void setTierId(TierId tierId) {
        this.tierId = tierId;
    }

    public String getPlayerTierName() {
        return playerTierName;
    }

    public void setPlayerTierName(String playerTierName) {
        this.playerTierName = playerTierName;
    }

    public ZonedDateTime getAchievedDate() {

        try {
            return achievedDate != null ? Dates.toZonedDateTime(achievedDate, ZoneId.systemDefault()) : null;
        } catch (DateTimeParseException e) {
            throw new AppRuntimeException(Errors.INVALID_DATE_FORMAT);
        }
    }

    public void setAchievedDate(ZonedDateTime achievedDate) {
        this.achievedDate = Dates.format(achievedDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ZonedDateTime getEarnedDate() {
        try {
            return earnedDate != null ? Dates.toZonedDateTime(earnedDate, ZoneId.systemDefault()) : null;
        } catch (DateTimeParseException e) {
            throw new AppRuntimeException(Errors.INVALID_DATE_FORMAT);
        }
    }

    public void setEarnedDate(ZonedDateTime earnedDate) {
        this.earnedDate = Dates.format(earnedDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ZonedDateTime getExpireDate() {
        try {
            return expireDate != null ? Dates.toZonedDateTime(expireDate, ZoneId.systemDefault()) : null;
        } catch (DateTimeParseException e) {
            throw new AppRuntimeException(Errors.INVALID_DATE_FORMAT);
        }
    }

    public void setExpireDate(ZonedDateTime expireDate) {
        this.expireDate = Dates.format(expireDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public String getNextTier() {
        return nextTier;
    }

    public void setNextTier(String nextTier) {
        this.nextTier = nextTier;
    }

    public String getPreviousTier() {
        return previousTier;
    }

    public void setPreviousTier(String previousTier) {
        this.previousTier = previousTier;
    }

    public String getCreditToNextTier() {
        return creditToNextTier;
    }

    public void setCreditToNextTier(String creditToNextTier) {
        this.creditToNextTier = creditToNextTier;
    }

    public String getIsPlayerLinked() {
        return isPlayerLinked;
    }

    public void setIsPlayerLinked(String isPlayerLinked) {
        this.isPlayerLinked = isPlayerLinked;
    }

    public Integer getLinkTierCredit() {
        return linkTierCredit;
    }

    public void setLinkTierCredit(Integer linkTierCredit) {
        this.linkTierCredit = linkTierCredit;
    }

    public Integer getTierCredit() {
        return tierCredit;
    }

    public void setTierCredit(Integer tierCredit) {
        this.tierCredit = tierCredit;
    }

}
