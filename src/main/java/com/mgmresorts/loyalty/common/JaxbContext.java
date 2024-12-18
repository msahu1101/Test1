package com.mgmresorts.loyalty.common;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.common.logging.Logger;

public class JaxbContext {
    public static final Charset APP_DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final JaxbContext CONTEXT = new JaxbContext();
    private final Logger logger = Logger.get(JaxbContext.class);
    private final JAXBContext jaxbContext;
    private final JAXBContext jaxbContextBalance;
    private final JAXBContext jaxbContextPromoEvent;

    private JaxbContext() {
        try {
            jaxbContext = JAXBContext.newInstance(com.mgmresorts.loyalty.dto.patron.customerpromo.ObjectFactory.class,
                    com.mgmresorts.loyalty.dto.patron.taxinformation.ObjectFactory.class, com.mgmresorts.loyalty.dto.patron.comments.ObjectFactory.class);
            jaxbContextBalance = JAXBContext.newInstance(com.mgmresorts.loyalty.dto.patron.balance.ObjectFactory.class);
            jaxbContextPromoEvent = JAXBContext.newInstance(com.mgmresorts.loyalty.dto.patron.promotickets.ObjectFactory.class);
        } catch (JAXBException e) {
            logger.error(String.format("Error {%s}: Failed to get an instance of JAXBContext.", SystemError.INVALID_DATA), e);
            throw new AppRuntimeException(SystemError.INVALID_DATA, e);

        }
    }

    public Unmarshaller getBalanceUnmarshaller() throws AppException {
        final Unmarshaller unmarshaller;
        try {
            unmarshaller = this.jaxbContextBalance.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error(String.format("Error {%s}: Failed to get an unmarshaller.", SystemError.INVALID_DATA), e);
            throw new AppException(SystemError.INVALID_DATA);
        }
        return unmarshaller;
    }

    public Unmarshaller getUnmarshaller() throws AppException {
        final Unmarshaller unmarshaller;
        try {
            unmarshaller = this.jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error(String.format("Error {%s}: Failed to get an unmarshaller.", SystemError.INVALID_DATA), e);
            throw new AppException(SystemError.INVALID_DATA);
        }
        return unmarshaller;
    }

    public static JaxbContext getInstance() {
        return CONTEXT;
    }

    public <T> T unmarshal(String string, Class<T> t) throws AppException {
        try {

            final byte[] bytes = string.getBytes(APP_DEFAULT_CHARSET);
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            final StreamSource source = new StreamSource(stream);
            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final JAXBElement<T> unmarshal = unmarshaller.unmarshal(source, t);
            return unmarshal.getValue();
        } catch (JAXBException e) {
            throw new AppException(SystemError.INVALID_DATA, e);
        }

    }
    
    
    public <T> T unmarshalPromoEvent(String string, Class<T> t) throws AppException {
        try {

            final byte[] bytes = string.getBytes(APP_DEFAULT_CHARSET);
            final ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            final StreamSource source = new StreamSource(stream);
            final Unmarshaller unmarshaller = jaxbContextPromoEvent.createUnmarshaller();
            final JAXBElement<T> unmarshal = unmarshaller.unmarshal(source, t);
            return unmarshal.getValue();
        } catch (JAXBException e) {
            throw new AppException(SystemError.INVALID_DATA, e);
        }

    }

    public <T> String marshal(T t, boolean standalone) throws AppException {
        try {
            final StringWriter writer = new StringWriter();
            final Marshaller marshaller = jaxbContext.createMarshaller();
            if (!standalone) {
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            }
            marshaller.marshal(t, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new AppException(SystemError.INVALID_DATA, e);
        }

    }

    public <T> String marshal(T t) throws AppException {
        return this.marshal(t, true);

    }
    
    
    public <T> String marshalPromoEvent(T t, boolean standalone) throws AppException {
        try {
            final StringWriter writer = new StringWriter();
            final Marshaller marshaller = jaxbContextPromoEvent.createMarshaller();
            if (!standalone) {
                marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            }
            marshaller.marshal(t, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new AppException(SystemError.INVALID_DATA, e);
        }

    }

    public <T> String marshalPromoEvent(T t) throws AppException {
        return this.marshalPromoEvent(t, true);

    }
    
    
    
}
