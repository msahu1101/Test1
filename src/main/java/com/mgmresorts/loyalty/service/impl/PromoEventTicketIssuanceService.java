package com.mgmresorts.loyalty.service.impl;

import java.util.GregorianCalendar;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang3.StringUtils;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.IHttpService;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.dto.patron.promotickets.Body;
import com.mgmresorts.loyalty.dto.patron.promotickets.CRMAcresMessage;
import com.mgmresorts.loyalty.dto.patron.promotickets.Header;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketRequest;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketResponse;
import com.mgmresorts.loyalty.dto.services.Result;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.service.IPromoEventTicketIssuanceService;

public class PromoEventTicketIssuanceService implements IPromoEventTicketIssuanceService {

    private final Logger logger = Logger.get(PromoEventTicketIssuanceService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.promoeventticket");

    @Inject
    private IHttpService httpService;

    private Header buildHeader() throws AppException {
        Header header = new Header();
        Header.Operation operation = new Header.Operation();
        try {
            header.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
            operation.setOperand(Runtime.get().getConfiguration("patron.http.header.promo.event.ticket.addOperand"));
            operation.setData(Runtime.get().getConfiguration("patron.http.header.promo.event.ticket.data"));
            header.setOperation(operation);
        } catch (Exception e) {
            logger.error("Error while generating XMLGregorianCalendar in operation Issue promo to Patron:", e);
            throw new AppException(SystemError.UNEXPECTED_SYSTEM, "Error while generating XMLGregorianCalendar in operation Issue promo to Patron:");
        }
        return header;
    }

    private Body buildBody(IssuePromoEventTicketRequest request) {
        Body.PlayerPromoEvent promoEventAdd = new Body.PlayerPromoEvent();
        promoEventAdd.setUserLogin(Runtime.get().getConfiguration("patron.body.promo.event.ticket.userLogin"));
        promoEventAdd.setPromoID(request.getPromoId());
        promoEventAdd.setBlockID(request.getBlockId());
        promoEventAdd.setTickets(request.getTicketCount());
        promoEventAdd.setReceived(request.getPickedUp());
        Body body = new Body();
        body.getPlayerPromoEvent().add(promoEventAdd);
        return body;
    }

    @Override
    public IssuePromoEventTicketResponse issueEventPromo(IssuePromoEventTicketRequest request) throws AppException {
        logger.info("**** Issuing Promo Event Tickets  *** " + request.toString());
        CRMAcresMessage crmAcresMessage = new CRMAcresMessage();
        crmAcresMessage.setPlayerID(Integer.toString(request.getPlayerId()));
        crmAcresMessage.setHeader(buildHeader());
        crmAcresMessage.setBody(buildBody(request));

        String xmlRequest = generatePromoXmlRequest(crmAcresMessage);
        logger.debug("**** Patron request for ISSUE PROMO EVENT *** " + xmlRequest);
        String xmlResponse = postPatronPromo(xmlRequest);
        logger.debug("**** Patron response for ISSUE PROMO EVENT *** " + xmlResponse);
        return getPatronResponse(xmlResponse);
    }

    private String generatePromoXmlRequest(CRMAcresMessage crmAcresMessage) throws AppException {
        try {
            String xmlRequest = JaxbContext.getInstance().marshalPromoEvent(crmAcresMessage);
            logger.debug(" *** Promo Xml Request ***** " + xmlRequest);
            return xmlRequest;
        } catch (Exception e) {
            logger.error("Error while marshalling to XML generatePromoXmlRequest promo request.", crmAcresMessage, e);
            throw new AppException(SystemError.UNEXPECTED_SYSTEM, "Error while marshalling to XML add promo request.");
        }
    }

    private String postPatronPromo(String xmlRequest) throws AppException {
        return circuit.flow(() -> createPatronPromo(xmlRequest), SystemError.UNABLE_TO_CALL_BACKEND);
    }

    protected String createPatronPromo(String xmlRequest) throws AppException {
        try {
            return httpService.post(Runtime.get().getConfiguration("patron.http.url"), xmlRequest, "Patron");
        } catch (HttpFailureException e) {
            logger.error("Error while calling the ADI service to Parton", e.getMessage());
            throw new AppException(SystemError.UNABLE_TO_CALL_BACKEND, e, "Error while calling the ADI service (Add Promo) to Parton");
        }
    }

    private IssuePromoEventTicketResponse getPatronResponse(String xmlResponse) throws AppException {
        CRMAcresMessage crmAcresMessage = null;
        try {
            crmAcresMessage = JaxbContext.getInstance().unmarshalPromoEvent(xmlResponse, CRMAcresMessage.class);
        } catch (AppException e) {
            String errorDescription = StringUtils.substringBetween(xmlResponse, "<ErrorDescription>", "</ErrorDescription>");
            logger.error("Multiple errors in patron response.", errorDescription);
            throw new AppException(ApplicationError.INVALID_RESPONSE_FROM_PATRON, errorDescription);
        }
        if (!Utils.anyNull(crmAcresMessage.getBody())) {
            if (!Utils.anyNull(crmAcresMessage.getBody().getError())) {
                logger.debug(crmAcresMessage.getBody().getError().getErrorCode(), crmAcresMessage.getBody().getError().getErrorDescription());
                throw new AppException(ApplicationError.INVALID_RESPONSE_FROM_PATRON, crmAcresMessage.getBody().getError().getErrorDescription());
            }
        }
        IssuePromoEventTicketResponse response = new IssuePromoEventTicketResponse();
        Result result = new Result();
        if (crmAcresMessage != null) {
            result.setStatus(crmAcresMessage.getHeader().getOperation().getOperand());
        }
        response.setResult(result);
        return response;
    }
}
