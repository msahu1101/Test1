package com.mgmresorts.loyalty.service;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.PlayerCommentsResponse;

public interface ICommentService extends IService {

    PlayerCommentsResponse getCustomerCommentsFromPatron(String player, String highPriority) throws AppException;

}
