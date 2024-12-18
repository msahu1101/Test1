package com.mgmresorts.loyalty.transformer;

public interface IBalanceTransformer<L, R> extends ITransformer<L, R> {

    Class<L> getLeftType();
}
