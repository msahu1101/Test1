package com.mgmresorts.loyalty.master;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mgmresorts.common.registry.StaticDataRegistry.PrimaryIndex;
import com.mgmresorts.common.registry.StaticDataRegistry.StaticData;

public class SiteMetaData implements StaticData {

    @PrimaryIndex
    @JsonProperty("siteId")
    private String siteId;

    @JsonProperty("name")
    private String name;
    

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String folder() {
        return "/static-data";
    }

    @Override
    public String file() {
        return "site-master.json";
    }
}
