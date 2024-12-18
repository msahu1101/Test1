package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.LinkedPlayersResponse;

public interface ILinkedPlayerInfoService extends IService {

    LinkedPlayersResponse getLinkedPlayerInfo(String playerId) throws AppException;

}
