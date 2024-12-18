package com.mgmresorts.loyalty.data.impl;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.common.AppError;
import com.mgmresorts.loyalty.data.ISlotDollarBalanceAccess;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SlotDollarBalanceAccess implements ISlotDollarBalanceAccess {
    Logger logger = Logger.get(SlotDollarBalanceAccess.class);

    private static final short SITE_ID = 1;     // Use 'Las Vegas' as default
    private static final String STORED_PROCEDURE_NAME = "Proc_GetPlayer_PointBalance";
    private static final int COLUMN_IDX = 1;
    private static final String ERROR_MSG_INVALID_ID = "<Error><ErrorCode>INVALIDPLAYERID</ErrorCode><ErrorDescription>"
        + "Proc_GetPlayer_PointBalance: PlayerID / CardID is Invalid or not active</ErrorDescription></Error>";

    @Inject
    @Named("work")
    private DataSource dataSourceWork;

    public SlotDollarBalance getSlotDollarBalance(String playerId) throws AppException {
        logger.info("Entering SlotDollarBalanceAccess.getSlotDollarBalance(): playerId: [{}]", playerId);
        String sqlStatement = String.format("{call dbo.%s(?,?)}", STORED_PROCEDURE_NAME);
        try (Connection con = dataSourceWork.getConnection();
             CallableStatement cs = con.prepareCall(sqlStatement);
             ) {
            cs.setInt(1, Integer.parseInt(playerId));
            cs.setShort(2, SITE_ID);        // slotDollar balance is a global bucket now and not per site...

            try (ResultSet resultSet = cs.executeQuery()) {
                if (resultSet.next()) {
                    try {
                        int slotDollars = resultSet.getInt(COLUMN_IDX);
                        logger.info("Successfully called storedProcedure '{}' for playerId '{}':  returnValue: {}", STORED_PROCEDURE_NAME, playerId, slotDollars);
                        return getSlotDollarBalanceEntity(playerId, slotDollars);
                    } catch (SQLException parseException) {
                        // Stored procedure returns crmAcres Xml if error is thrown.  Try to read it...
                        try {
                            String returnValue = resultSet.getString(COLUMN_IDX);
                            if (!returnValue.contains(ERROR_MSG_INVALID_ID)) {
                                logger.debug("sql returnValue: [{}]", returnValue);
                                logger.error("Exception thrown while reading data: {}", parseException);
                            }
                        } catch (Exception err) {
                            // ignore err.  throw parseException instead...
                            throw parseException;
                        }
                    }
                }
                logger.info("Failed to retrieve data from '{}' for playerId '{}'...", STORED_PROCEDURE_NAME, playerId);

            } catch (SQLException sqlException) {
                logger.error("Exception executing query and/or reading resultset", sqlException);
                throw sqlException;
            }
        } catch (Exception outerException) {
            logger.error("Exception trying to execute Sql: " + sqlStatement, outerException);
            throw new AppException(AppError.PATRON_ERROR, outerException.getMessage());
        }
        return null;
    }

    private SlotDollarBalance getSlotDollarBalanceEntity(String playerId, int slotDollars) {
        return new SlotDollarBalance().withPlayerId(playerId).withSlotDollars(slotDollars);
    }
}
