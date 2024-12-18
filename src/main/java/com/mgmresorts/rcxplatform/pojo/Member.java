package com.mgmresorts.rcxplatform.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tiers",
    "purses",
    "_id"
})
@Generated("jsonschema2pojo")
public class Member implements Serializable {

    private List<Tier> tiers = new ArrayList<Tier>();
    private List<Purse> purses = new ArrayList<Purse>();
    private String id;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private static final long serialVersionUID = 2499467564466684857L;

    @JsonProperty("tiers")
    public List<Tier> getTiers() {
        return tiers;
    }

    public void setTiers(List<Tier> tiers) {
        this.tiers = tiers;
    }

    public Member withTiers(List<Tier> tiers) {
        this.tiers = tiers;
        return this;
    }

    @JsonProperty("purses")
    public List<Purse> getPurses() {
        return purses;
    }

    public void setPurses(List<Purse> purses) {
        this.purses = purses;
    }

    public Member withPurses(List<Purse> purses) {
        this.purses = purses;
        return this;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Member withId(String id) {
        this.id = id;
        return this;
    }

    public Purse getRewardsPoints() {
        return getPurse(Purse.REWARDS_POINTS);
    }

    public Purse getDeferredDebits() {
        return getPurse(Purse.DEFERRED_DEBITS);
    }

    public Purse getTierCredits() {
        return getPurse(Purse.TIER_CREDITS);
    }

    private Purse getPurse(String purseName) {
        Purse purse = purses.stream().filter(x -> x.getName().equalsIgnoreCase(purseName)).findAny().get();
        if (purse == null) {
            purse = new Purse().withName(purseName).withAvailBalance(0.0).withBalance(0.0);
            purses.add(purse);
        }
        return purse;
    }
}
