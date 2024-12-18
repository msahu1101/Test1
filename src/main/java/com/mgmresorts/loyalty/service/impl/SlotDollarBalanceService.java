package com.mgmresorts.loyalty.service.impl;

import com.google.inject.Inject;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.ISlotDollarBalanceAccess;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;
import com.mgmresorts.loyalty.dto.customer.GetSlotDollarBalanceResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.ISlotDollarBalanceService;

public class SlotDollarBalanceService implements ISlotDollarBalanceService {
    private final Logger logger = Logger.get(SlotDollarBalanceService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.slotdollarbalance");

    @Inject
    private ICache<SlotDollarBalance> caches;

    @Inject
    private ISlotDollarBalanceAccess slotDollarBalanceAccess;

    @Override
    public GetSlotDollarBalanceResponse getSlotDollarBalance(String playerId) throws AppException {
        if (!Utils.isNumeric(playerId)) {
            throw new AppException(Errors.INVALID_PATRON_ID, playerId);
        }

        final SlotDollarBalance output = callSprocWithCircuitBreaker(playerId);
        logger.trace("output: [{}]", output);

        return buildResponse(output);
    }

    protected SlotDollarBalance callSprocWithCircuitBreaker(String playerId) throws AppException {
        String cacheKey = String.format("{}:{}", playerId);
        logger.trace("Call caches.nonBlockingGet({}} in circuit breaker", cacheKey);
        return circuit.flow(() -> caches.nonBlockingGet(cacheKey, SlotDollarBalance.class, (s) -> {
            logger.trace("Call getSlotDollarBalance({}) in sdbAccess...", playerId);
            final SlotDollarBalance dbOutput = slotDollarBalanceAccess.getSlotDollarBalance(playerId);

            if (dbOutput == null) {
                logger.warn("ERROR no records returned from db");
                throw new AppException(Errors.NO_SLOT_DOLLAR_BALANCE, playerId);
            }
            logger.trace("output: [{}]", dbOutput);
            return dbOutput;
        }), SystemError.UNABLE_TO_CALL_BACKEND);
    }

    protected GetSlotDollarBalanceResponse buildResponse(SlotDollarBalance slotDollarsEntity) {
        com.mgmresorts.loyalty.dto.customer.SlotDollarBalance slotDollarBalance = new com.mgmresorts.loyalty.dto.customer.SlotDollarBalance()
            .withPlayerId(slotDollarsEntity.getPlayerId())
            .withSlotDollars(slotDollarsEntity.getSlotDollars());

        GetSlotDollarBalanceResponse response = new GetSlotDollarBalanceResponse()
            .withSlotDollarBalance(slotDollarBalance);

        response.setHeader(HeaderBuilder.buildHeader());
        return response;
    }
}