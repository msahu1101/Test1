package com.mgmresorts.loyalty.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.IStopCodeAccess;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.to.StopCodeWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.IStopCodeService;
import com.mgmresorts.loyalty.transformer.ITransformer;

public class StopCodeService implements IStopCodeService {

    private final Logger logger = Logger.get(StopCodeService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.stop-code");

    @Inject
    private ICache<StopCodeWrapper> caches;

    @Inject
    private IStopCodeAccess customerStopCode;

    @Inject
    private ITransformer<List<CustomerStopCode>, List<StopCode>> transformer;

    @Override
    public StopCodeResponse getCustomerStopCode(String mlife) throws AppException {

        logger.trace("Execution enter in getCustomerStopCode ", StopCodeResponse.class);
        StopCodeResponse response = new StopCodeResponse();
        if (!Utils.isNumeric(mlife)) {
            throw new AppException(Errors.INVALID_PATRON_ID, mlife);
        }

        final StopCodeWrapper out = circuit.flow(() -> caches.nonBlockingGet(mlife, StopCodeWrapper.class, (s) -> {
            StopCodeWrapper customerStopCodes = new StopCodeWrapper();
            List<CustomerStopCode> result = new ArrayList<>();
            final List<StopCode> stopCode = customerStopCode.getStopCode(Integer.parseInt(mlife));
            result = transformer.toLeft(stopCode);
            customerStopCodes.setCustomerStopCodes(result);
            return customerStopCodes;
        }), SystemError.UNABLE_TO_CALL_BACKEND);

        response.setCustomerStopCode(out.getCustomerStopCodes());
        response.setHeader(HeaderBuilder.buildHeader());

        return response;

    }

}