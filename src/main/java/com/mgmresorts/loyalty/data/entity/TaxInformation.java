package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NamedStoredProcedureQuery(name = TaxInformation.TAX_INFORMATION_CALL, procedureName = "MlifeTaxInformation", resultClasses = TaxInformation.class, parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class) })
@Entity
public class TaxInformation {
    public static final String TAX_INFORMATION_CALL = "customer-taxinformation-procedure";

    @Id
    @Column(name = "C1")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return  getResult();
    }
}
