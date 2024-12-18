package com.mgmresorts.rcxplatform.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "balance",
    "availBalance",
    "policyId"
})
@Generated("jsonschema2pojo")
public class Purse implements Serializable {
    public static final String REWARDS_POINTS = "Rewards Points";
    public static final String TIER_CREDITS = "Tier Credits";
    public static final String DEFERRED_DEBITS = "Deferred Debits";

    private String name;
    private double balance;
    private double availBalance;
    private String policyId = "";
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private static final long serialVersionUID = 515391576571540365L;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Purse withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("balance")
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Purse withBalance(double balance) {
        this.balance = balance;
        return this;
    }

    @JsonProperty("availBalance")
    public double getAvailBalance() {
        return availBalance;
    }

    public void setAvailBalance(double availBalance) {
        this.availBalance = availBalance;
    }

    public Purse withAvailBalance(double availBalance) {
        this.availBalance = availBalance;
        return this;
    }

    @JsonProperty("policyId")
    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public Purse withPolicyId(String policyId) {
        this.policyId = policyId;
        return this;
    }

}
