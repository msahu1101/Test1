package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.entity.StopCode;

public interface IStopCodeAccess {
    List<StopCode> getStopCode(int player) throws AppException;

}