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
    "members",
    "personalization"
})
@Generated("jsonschema2pojo")
public class GetMembersResponse implements Serializable {
    public static final int IDX_MEMBER_MAIN = 0;
    public static final int IDX_MEMBER_LINKED = 1;

    private Member mainMember;
    private Member linkedMember;

    private List<Member> members = new ArrayList<Member>();
    private Personalization personalization;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private static final long serialVersionUID = 3958447775713563725L;

    @JsonProperty("members")
    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public GetMembersResponse withMembers(List<Member> members) {
        this.members = members;
        return this;
    }

    @JsonProperty("personalization")
    public Personalization getPersonalization() {
        return personalization;
    }

    public void setPersonalization(Personalization personalization) {
        this.personalization = personalization;
    }

    public GetMembersResponse withPersonalization(Personalization personalization) {
        this.personalization = personalization;
        return this;
    }

    public Member getMainMember() {
        if (mainMember == null) {
            if (this.members.size() > IDX_MEMBER_MAIN) {
                mainMember = this.members.get(IDX_MEMBER_MAIN);
            }
        }
        return mainMember;
    }

    public Member getLinkedMember() {
        if (linkedMember == null) {
            if (this.members.size() > IDX_MEMBER_LINKED) {
                linkedMember = this.members.get(IDX_MEMBER_LINKED);
            }
        }
        return linkedMember;
    }
}
