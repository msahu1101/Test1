package com.mgmresorts.loyalty.data.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.ArrayRecord;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.Exceptions;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.support.SqlSupport;
import com.mgmresorts.loyalty.errors.Errors;

public class BalanceGiftPointsAccess extends SqlSupport<GiftPoints, String> implements IBalanceAccess<GiftPoints> {
    private final Logger logger = Logger.get(BalanceGiftPointsAccess.class);
    private final String name = "gift points";

    /*
     * To get gift points information, a stored procedure query is used.
     */
    @Override
    public List<GiftPoints> databaseCall(int playerId, int siteId) throws AppException {
        String procName = "GetHGSPlayerGiftPoints";
        final int giftProgramId = com.mgmresorts.common.config.Runtime.get().getInt("customer.balance.gift.program.id", 0);
        List<GiftPoints> result = new ArrayList<>();
        try {
            final String nativeProcedure = "EXEC " + procName + " @playerid1 = " + playerId + " , @giftprogramid = " + giftProgramId + "";
            List<GiftPoints> callProcByQuery = callProcByQuery(DB.PCIS, nativeProcedure, GiftPoints.class, (sp) -> {
            });
            result = getValues(callProcByQuery);
        } catch (Exception e) {
            logger.error("Error while calling Patron Proc_GetBorgataOfferDetails store procedure", e);
            throw Exceptions.wrap(e, Errors.UNABLE_TO_CALL_BACKEND, "Failure to complete " + name + " call.");
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    public List<GiftPoints> getValues(List<GiftPoints> giftpoints) {
        final List<GiftPoints> giftPoints = new ArrayList<>();
        for (Object object : giftpoints) {
            final ArrayRecord record = (ArrayRecord) object;
            final Vector fields = record.getFields();
            final Vector values = record.getValues();
            Iterator iteratorField = fields.iterator();
            final GiftPoints giftPoint = new GiftPoints();
            if (record.getValues().get(0) != null) {
                giftPoint.setPlayerId1(Integer.parseInt(record.getValues().get(0).toString()));
            }
            if (record.getValues().get(28) != null) {
                giftPoint.setHgsPoints(Double.parseDouble(record.getValues().get(28).toString()));
            }
            if (record.getValues().get(28) != null) {
                giftPoint.setGiftPointsLinked(Double.parseDouble(record.getValues().get(30).toString()));
            }
            giftPoints.add(giftPoint);
        }
        return giftpoints;
    }

    @Override
    public Class<GiftPoints> getEntityType() {
        return GiftPoints.class;
    }

}
