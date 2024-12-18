package com.mgmresorts.loyalty.transformer;

import java.util.ArrayList;
import java.util.List;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.dto.customer.LinkedPlayer;

public class LinkedPlayerTransformer implements ITransformer<List<LinkedPlayer>, List<PlayerLink>> {

    @Override
    public List<LinkedPlayer> toLeft(List<PlayerLink> right) throws AppException {
        final List<LinkedPlayer> links = new ArrayList<LinkedPlayer>();
        if (right != null) {
            for (PlayerLink playerLink : right) {
                final LinkedPlayer player = new LinkedPlayer();
                player.setLinkNumber(playerLink.getLinkNumber());
                player.setPlayerId(playerLink.getPlayerId());
                links.add(player);
            }
        }
        return links;
    }

    @Override
    public List<PlayerLink> toRight(List<LinkedPlayer> left) throws AppException {
        throw new AppRuntimeException(SystemError.UNSUPPORTED_OPERATION);
    }

}
