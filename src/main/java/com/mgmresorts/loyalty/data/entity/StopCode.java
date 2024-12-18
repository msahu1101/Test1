package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NamedStoredProcedureQuery(//
        name = StopCode.STOP_CODES_CALL, //
        procedureName = "Proc_StopCodeCheck", //
        resultClasses = StopCode.class, //
        parameters = { //
                @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), //
                @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), //
                @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class), //
                @StoredProcedureParameter(mode = ParameterMode.IN, type = int.class)//
        }//
)
@Entity
public class StopCode {

    public static final String STOP_CODES_CALL = "customer-stopcodes-procedure";

    @Id
    @Column(name = "StopCodeID")
    private String id = "1";

    @Column(name = "priority")
    private int priority;

    @Column(name = "Description")
    private String description;

    @Column(name = "Information")
    private String information;

    @Column(name = "Active")
    private Boolean isActive;

    public StopCode() {

    }

    @Override
    public String toString() {
        return "[StopCodeID = " + getId() + " , priority = " + getPriority() + " , Description = " + getDescription()
                + " , Information = " + getInformation() + " , Active = " + getIsActive() + "]";
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
