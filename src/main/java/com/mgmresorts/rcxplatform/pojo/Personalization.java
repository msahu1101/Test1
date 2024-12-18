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
    "nextTier",
    "creditsToNextTier"
})
@Generated("jsonschema2pojo")
public class Personalization implements Serializable {

    private String nextTier;
    private int creditsToNextTier;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private static final long serialVersionUID = -7259350892892311527L;

    @JsonProperty("nextTier")
    public String getNextTier() {
        return nextTier;
    }

    public void setNextTier(String nextTier) {
        this.nextTier = nextTier;
    }

    public Personalization withNextTier(String nextTier) {
        this.nextTier = nextTier;
        return this;
    }

    @JsonProperty("creditsToNextTier")
    public int getCreditsToNextTier() {
        return creditsToNextTier;
    }

    public void setCreditsToNextTier(int creditsToNextTier) {
        this.creditsToNextTier = creditsToNextTier;
    }

    public Personalization withCreditsToNextTier(int creditsToNextTier) {
        this.creditsToNextTier = creditsToNextTier;
        return this;
    }

}
