package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IStopCodeAccess;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;

public class StopCodeAccess extends SqlSupport<StopCode, String> implements IStopCodeAccess {

    private final Logger logger = Logger.get(StopCodeAccess.class);

    private static int userId() {
        return Runtime.get().getInt("database.patron.userid", -1);
    }

    private static int siteId() {
        return Runtime.get().getInt("database.patron.siteid", -1);
    }

    private static int priority() {
        return Runtime.get().getInt("database.patron.priority", -1);
    }

    @Override
    public List<StopCode> getStopCode(int player) throws AppException {
        try {
            List<StopCode> stopCodes = callByNamedProcedure(DB.PLAYER, StopCode.STOP_CODES_CALL, StopCode.class, (ps) -> {
                ps.setParameter(1, player);
                ps.setParameter(2, userId());
                ps.setParameter(3, siteId());
                ps.setParameter(4, priority());
            });
            logger.debug("Stop code database response", stopCodes.toString());
            return stopCodes;
        } catch (AppException e) {
            e.printStackTrace();
            logger.error("Patron DB Exception", e.getMessage());
            throw new AppException(ApplicationError.NO_STOP_CODES, "Player doesn't have stop codes");
        } catch (Exception e) {
            e.printStackTrace();

            final List<Integer> errors = SqlSupport.getDatabseErrorCodes(e);
            if (errors.contains(60002)) {
                logger.error("No Stop code found for the given Mlife number", e.getMessage());
                throw new AppException(ApplicationError.NO_STOP_CODES, "Player doesn't have stop codes");
            } else {
                logger.error("Failed to create a connection to source, execute query, or return a result set from a call while fetching player comments {} ", e);
                throw new AppException(Errors.UNABLE_TO_CALL_BACKEND,
                        "Failed to create a connection to source, execute query, or return a result set from a call.");
            }
        }
    }

    @Override
    protected Class<StopCode> getEntityType() {
        return StopCode.class;
    }
}