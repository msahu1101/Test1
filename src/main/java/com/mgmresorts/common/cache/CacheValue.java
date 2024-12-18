package com.mgmresorts.common.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CacheValue<T> {

    public enum Status {
        UPDATED, PENDING
    }

    private int expire;
    private int version;
    @JsonIgnore
    private T payload;
    @JsonIgnore
    private boolean own = false;

    private String payloadString;

    private Status status;

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPayloadString() {
        return payloadString;
    }

    public void setPayloadString(String payloadString) {
        this.payloadString = payloadString;
    }

    public boolean isOwn() {
        return own;
    }

    public void setOwn(boolean own) {
        this.own = own;
    }

    @Override
    public String toString() {
        return "CacheValue [expire=" + expire + ", version=" + version + ", payload=" + payload + ", own=" + own + ", payloadString=" + payloadString + ", status=" + status + "]";
    }

}
