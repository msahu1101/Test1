package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;

@NamedNativeQuery(name = Balance.BALANCE_CALL, query = "{call Proc_XML_CRM_PlayerBalances (?, ?)}", resultClass = Balance.class)
@Entity
public class Balance {
    public static final String BALANCE_CALL = "customer-balance-procedure";

    @Id
    @Column(name = "XMLString")
    private String xml;

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public String toString() {
        return "[" + getXml() + "]";
    }
}
