package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.PlayerLink;

public interface ILinkedPlayerInfoAccess {

    List<PlayerLink> getLinkedPlayerInfo(int playerId) throws AppException;

}
