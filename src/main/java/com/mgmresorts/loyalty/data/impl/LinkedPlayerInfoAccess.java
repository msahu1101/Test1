package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.ILinkedPlayerInfoAccess;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.support.SqlSupport;

public class LinkedPlayerInfoAccess extends SqlSupport<PlayerLink, String> implements ILinkedPlayerInfoAccess {

    private final Logger logger = Logger.get(LinkedPlayerInfoAccess.class);
    
    @Override
    public List<PlayerLink> getLinkedPlayerInfo(int playerId) throws AppException {
        List<PlayerLink> playerLinks = findByNamedQuery(DB.PLAYER, PlayerLink.LINKED_PLAYER_QUERY, (typedQueryParameter) -> {
            typedQueryParameter.setParameter("playerId", playerId);
        });
        logger.debug("Player Link database response", playerLinks.toString());
        
        return playerLinks;
    }

    @Override
    protected Class<PlayerLink> getEntityType() {
        return PlayerLink.class;
    }
}
