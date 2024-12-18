package com.mgmresorts.loyalty.data;

import java.util.List;

import com.mgmresorts.common.exception.AppException;

public interface IBalanceAccess<T> {
    List<T> databaseCall(int player, int site) throws AppException;

    Class<T> getEntityType();
}
