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
    "level",
    "achievedOn",
    "requalsOn",
    "prevLevelName"
})
@Generated("jsonschema2pojo")
public class Tier implements Serializable {

    private Level level;
    private String achievedOn;
    private String requalsOn;
    private String prevLevelName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private static final long serialVersionUID = 5767495133194579135L;

    @JsonProperty("level")
    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Tier withLevel(Level level) {
        this.level = level;
        return this;
    }

    @JsonProperty("achievedOn")
    public String getAchievedOn() {
        return achievedOn;
    }

    public void setAchievedOn(String achievedOn) {
        this.achievedOn = achievedOn;
    }

    public Tier withAchievedOn(String achievedOn) {
        this.achievedOn = achievedOn;
        return this;
    }

    @JsonProperty("requalsOn")
    public String getRequalsOn() {
        return requalsOn;
    }

    public void setRequalsOn(String requalsOn) {
        this.requalsOn = requalsOn;
    }

    public Tier withRequalsOn(String requalsOn) {
        this.requalsOn = requalsOn;
        return this;
    }

    @JsonProperty("prevLevelName")
    public String getPrevLevelName() {
        return prevLevelName;
    }

    public void setPrevLevelName(String prevLevelName) {
        this.prevLevelName = prevLevelName;
    }

    public Tier withPrevLevelName(String prevLevelName) {
        this.prevLevelName = prevLevelName;
        return this;
    }

}
