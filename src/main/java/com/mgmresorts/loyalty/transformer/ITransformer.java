package com.mgmresorts.loyalty.transformer;

import com.mgmresorts.common.exception.AppException;

public interface ITransformer<L, R> {

    L toLeft(R right) throws AppException;

    R toRight(L left) throws AppException;

}
