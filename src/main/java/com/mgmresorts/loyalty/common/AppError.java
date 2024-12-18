package com.mgmresorts.loyalty.common;

import com.mgmresorts.common.errors.ErrorManager.ErrorConfig;
import com.mgmresorts.common.errors.HttpStatus;
import com.mgmresorts.common.errors.SystemError;

public interface AppError extends SystemError {
    @ErrorConfig(group = Group.SYSTEM, message = "Unable to fetch data from patron: %s", httpStatus = HttpStatus.BAD_GATEWAY)
    int PATRON_ERROR = 1020;
}
