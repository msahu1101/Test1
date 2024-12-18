package com.mgmresorts.loyalty.errors;

import java.lang.reflect.Field;

import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.errors.ErrorManager.AppError;
import com.mgmresorts.common.errors.ErrorManager.ErrorConfig;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppRuntimeException;

public class Errors implements SystemError, ApplicationError {

    public Errors() {
        ErrorManager.load(Errors.class);
    }


}
