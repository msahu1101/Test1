package com.mgmresorts.loyalty.errors;

import com.mgmresorts.common.errors.ErrorManager.ErrorConfig;
import com.mgmresorts.common.errors.ErrorManager.IError.Group;
import com.mgmresorts.common.errors.HttpStatus;

public interface ApplicationError {

    String group = "Application";

    @ErrorConfig(group = Group.BUSINESS, message = "Invalid patron id: %s", httpStatus = HttpStatus.NOT_FOUND)
    int INVALID_PATRON_ID = 3001;
    
    @ErrorConfig(group = Group.BUSINESS, message = "Player does not have any comment", httpStatus = HttpStatus.NOT_FOUND)
    int NO_PLAYER_COMMENT = 3003;

    @ErrorConfig(group = Group.BUSINESS, message = "Player does not have any linked player.", httpStatus = HttpStatus.NOT_FOUND)
    int NO_LINKED_MEMBERS = 3004;

    @ErrorConfig(group = Group.BUSINESS, message = "Player does not have any promotions", httpStatus = HttpStatus.NOT_FOUND)
    int NO_PROMOTION = 3005;

    @ErrorConfig(group = Group.BUSINESS, message = "Player does not have any stop codes", httpStatus = HttpStatus.NOT_FOUND)
    int NO_STOP_CODES = 3006;

    @ErrorConfig(group = Group.BUSINESS, message = "Tax information does not available for the player", httpStatus = HttpStatus.NOT_FOUND)
    int NO_TAX_INFORMATION = 3007;

    @ErrorConfig(group = Group.BUSINESS, message = "Error response from Patron: %s", httpStatus = HttpStatus.BAD_REQUEST)
    int INVALID_RESPONSE_FROM_PATRON = 3008;

    @ErrorConfig(group = Group.BUSINESS, message = "Invalid request parameter", httpStatus = HttpStatus.BAD_REQUEST)
    int INVALID_REQUEST = 3009;

    @ErrorConfig(group = Group.BUSINESS, message = "Unable to update player promotion", httpStatus = HttpStatus.NOT_FOUND)
    int UNABLE_TO_UPDATE_PROMO = 3010;

    @ErrorConfig(group = Group.BUSINESS, message = "Player doesn't have reservation", httpStatus = HttpStatus.NOT_FOUND)
    int PLAYER_ID_WITH_NO_RESERVATION = 3011;

    @ErrorConfig(group = Group.BUSINESS, message = "Player doesn't have promo event.", httpStatus = HttpStatus.NOT_FOUND)
    int INVALID_PROMO_IDS = 3012;
    
    @ErrorConfig(group = Group.BUSINESS, message = "No Promo Event Blocks found.", httpStatus = HttpStatus.NOT_FOUND)
    int NO_PROMO_EVENT_BLOCKS = 3013;
    
    @ErrorConfig(group = Group.BUSINESS, message = "Source system error occured | Code: %s | Description: %s", httpStatus = HttpStatus.INTERNAL_SERVER_ERROR)
    int SOURCE_SYSTEM_ERROR = 3014;

    @ErrorConfig(group = Group.BUSINESS, message = "Player '%s' does not have a slot dollar balance", httpStatus = HttpStatus.NOT_FOUND)
    int NO_SLOT_DOLLAR_BALANCE = 3015;
}
