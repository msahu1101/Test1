package com.mgmresorts.loyalty.data.to;

import java.util.List;

import com.mgmresorts.loyalty.data.entity.PlayerLink;

public class LinkedPlayersWrapper {

    private List<PlayerLink> linkedPlayers;

    public List<PlayerLink> getLinkedPlayers() {
        return linkedPlayers;
    }

    public void setLinkedPlayers(List<PlayerLink> linkedPlayers) {
        this.linkedPlayers = linkedPlayers;
    }

}
