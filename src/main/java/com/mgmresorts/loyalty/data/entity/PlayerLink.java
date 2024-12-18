package com.mgmresorts.loyalty.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedQuery;

@NamedQuery(name = PlayerLink.LINKED_PLAYER_QUERY, query = "SELECT p FROM PlayerLink p where p.linkNumber = (select l.linkNumber from PlayerLink l where l.playerId=:playerId) "
        + "and p.playerId <> :playerId", lockMode = LockModeType.NONE)

@Entity
public class PlayerLink {

    public static final String LINKED_PLAYER_QUERY = "linked-player-query";

    @Id
    @Column(name = "PlayerId")
    private int playerId;

    @Column(name = "LinkNumber")
    private int linkNumber;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getLinkNumber() {
        return linkNumber;
    }

    public void setLinkNumber(int linkNumber) {
        this.linkNumber = linkNumber;
    }

    @Override
    public String toString() {
        return "[PlayerId = " + getPlayerId() + " , LinkNumber = " + getLinkNumber() + "]";
    }
}
