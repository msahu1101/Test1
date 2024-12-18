package com.mgmresorts.loyalty.common;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.dto.patron.promotickets.Header;

public class SoapUtil {

    private static final Logger logger = Logger.get(SoapUtil.class);

    public static Header buildHeader(String promoId, String eventId, String data) throws AppException {
        Header header = new Header();
        Header.Operation operation = new Header.Operation();
        try {
            header.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
            operation.setOperand(Runtime.get().getConfiguration("patron.http.header.addOperand.Request"));

            if (data.equals(Runtime.get().getConfiguration("patron.http.header.get.promo.events.data"))) { // getEvents
                operation.setData(Runtime.get().getConfiguration("patron.http.header.get.promo.events.data"));
            }
            if (data.equals(Runtime.get().getConfiguration("patron.http.header.get.promo.events.data"))) { // getEvents
                operation.setWhereClause("PromoId='" + promoId + "'");
            }

            if (data.equals(Runtime.get().getConfiguration("patron.http.header.get.event.blocks.data"))) { // getBlocks
                operation.setData(Runtime.get().getConfiguration("patron.http.header.get.event.blocks.data"));
            }

            if (data.equals(Runtime.get().getConfiguration("patron.http.header.get.event.blocks.data"))) { // getBlocks
                operation.setWhereClause("EventId='" + eventId + "'");
            }
            header.setOperation(operation);

        } catch (Exception e) {
            logger.error("Error while generating XMLGregorianCalendar in operation Issue promo to Patron:", e);
            throw new AppException(SystemError.UNEXPECTED_SYSTEM, "Error while generating XMLGregorianCalendar in operation Issue promo to Patron:");
        }
        return header;
    }

}
