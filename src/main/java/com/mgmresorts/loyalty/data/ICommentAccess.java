package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;

public interface ICommentAccess<T> {
    List<T> databaseCallNative(String player, boolean isHighPriority) throws AppException;

}
