package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;

@NamedStoredProcedureQuery(name = PlayerComment.PLAYER_COMMENTS_CALL, procedureName = "Proc_XML_MGM_CommentsRequest", resultClasses = PlayerComment.class, //
        parameters = { @StoredProcedureParameter(name = "XMLParam", mode = ParameterMode.IN, type = String.class)//
        })
@Entity
public class PlayerComment {

    public static final String PLAYER_COMMENTS_CALL = "customer-comment-procedure";

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
        return "[" + getResult() + "]";
    }
}
