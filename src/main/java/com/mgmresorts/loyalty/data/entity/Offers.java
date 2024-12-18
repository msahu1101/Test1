package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NamedStoredProcedureQuery(name = Offers.OFFERS_CALL, procedureName = "Proc_XML_CRM_PlayerPromo", resultClasses = Offers.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), })
@Entity
public class Offers {
    public static final String OFFERS_CALL = "customer-promotions-procedure";

    @Id
    @Column(name = "XMLString")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "[" + getResult() + "]";
    }
}