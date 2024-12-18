package com.mgmresorts.loyalty.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.IHttpService;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.common.SoapUtil;
import com.mgmresorts.loyalty.dto.customer.PromoEventBlocks;
import com.mgmresorts.loyalty.dto.patron.promotickets.Body;
import com.mgmresorts.loyalty.dto.patron.promotickets.CRMAcresMessage;
import com.mgmresorts.loyalty.dto.services.PromoEventsBlockResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.service.IPromoEventsService;

public class PromoEventsService implements IPromoEventsService {
    private final Logger logger = Logger.get(PromoEventsBlockResponse.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.promoevent");

    @Inject
    private IHttpService httpService;

    @Override
    public PromoEventsBlockResponse getPromoEventBlockInfo(String id, String type) throws AppException {
        final PromoEventsBlockResponse promoEventsBlockResponse = new PromoEventsBlockResponse();
        List<PromoEventBlocks> promoEventBlocks = new ArrayList<>();
        logger.info("**** Get Promo Events *** " + id);
        final CRMAcresMessage crmAcresMessage = new CRMAcresMessage();
        if (type.equals("promo")) {
            crmAcresMessage.setHeader(SoapUtil.buildHeader(id, "0", "PromoEventsWithFilter"));
            String xmlRequest = generatePromoXmlRequest(crmAcresMessage);
            String xmlResponse = postPatronPromo(xmlRequest);
            promoEventBlocks = getEventsInfoResponse(xmlResponse);

        } else if (type.equals("event")) {
            promoEventBlocks = getEventBlocksInfo(id);
        }
        if (promoEventBlocks.isEmpty()) {
            throw new AppException(ApplicationError.NO_PROMO_EVENT_BLOCKS);

        } else {
            promoEventsBlockResponse.setPromoEventBlocks(promoEventBlocks);
        }
        promoEventsBlockResponse.setHeader(HeaderBuilder.buildHeader());
        promoEventsBlockResponse.setPromoEventBlocks(promoEventBlocks);
        return promoEventsBlockResponse;
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
            return httpService.post(Runtime.get().getConfiguration("patron.http.url"), xmlRequest, "promo:patron", "patron-promo-event");
        } catch (HttpFailureException e) {
            logger.error("Error while calling the ADI service to Parton", e.getMessage());
            throw new AppException(SystemError.UNABLE_TO_CALL_BACKEND, e, "Error while calling the ADI service (Get Promo Event) to Parton");
        }
    }

    private List<PromoEventBlocks> getEventBlocksInfo(String eventId) throws AppException {
        logger.info("**** Get Promo Events *** " + eventId);
        final CRMAcresMessage crmAcresMessage = new CRMAcresMessage();
        crmAcresMessage.setHeader(SoapUtil.buildHeader(null, eventId, "PromoEventBlock"));
        String xmlRequest = generatePromoXmlRequest(crmAcresMessage);
        logger.debug("**** Patron request for GET Blocks By Event *** " + xmlRequest);
        String xmlResponse = postPatronPromo(xmlRequest);
        logger.debug("**** Patron response for GET Blocks By Event *** " + xmlResponse);
        List<PromoEventBlocks> result = getPromoEventBlocksResponse(xmlResponse);
        return result;
    }

    private List<PromoEventBlocks> getPromoEventBlocksResponse(String xmlResponse) throws AppException {
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
        final List<PromoEventBlocks> promoEventBlocks = new ArrayList<>();
        if (crmAcresMessage != null && crmAcresMessage.getBody() != null) {

            if (crmAcresMessage != null && !crmAcresMessage.getBody().getPromoEventBlock().isEmpty()) {
                for (Body.PromoEventBlock promoEventBlockCrm : crmAcresMessage.getBody().getPromoEventBlock()) {
                    final PromoEventBlocks promoEventBlock = new PromoEventBlocks();
                    promoEventBlock.setEventId(promoEventBlockCrm.getEventID());
                    promoEventBlock.setBlockId(promoEventBlockCrm.getBlockID());
                    promoEventBlock.setDescription(promoEventBlockCrm.getDescription());
                    promoEventBlock.setTickets(promoEventBlockCrm.getTickets());
                    promoEventBlock.setMaxPerPlayer(promoEventBlockCrm.getMaxPerPlayer());
                    promoEventBlock.setDefaultAmount(promoEventBlockCrm.getDefaultAmount());
                    promoEventBlock.setStatus(promoEventBlockCrm.getStatus());
                    promoEventBlock.setSiteId(promoEventBlockCrm.getSiteID().getValue());
                    promoEventBlocks.add(promoEventBlock);
                }
            }
        }

        return promoEventBlocks;
    }

    private List<PromoEventBlocks> getEventsInfoResponse(String xmlResponse) throws AppException {
        CRMAcresMessage crmAcresMessage = null;
        List<com.mgmresorts.loyalty.dto.customer.PromoEventBlocks> promoEventBlocksList = new ArrayList<>();
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
        if (crmAcresMessage != null && !crmAcresMessage.getBody().getPromoEvent().isEmpty()) {
            List<com.mgmresorts.loyalty.dto.customer.PromoEventBlocks> promoEventBlocks = new ArrayList<>();
            List<com.mgmresorts.loyalty.dto.customer.PromoEventBlocks> totalPromoEventBlocks = new ArrayList<>();

            for (Body.PromoEvent promoEventsCrm : crmAcresMessage.getBody().getPromoEvent()) {

                promoEventBlocks = getEventBlocksInfo(String.valueOf(promoEventsCrm.getEventID()));
                totalPromoEventBlocks.addAll(promoEventBlocks);
            }

            for (com.mgmresorts.loyalty.dto.customer.PromoEventBlocks promoEventBlockCrm : totalPromoEventBlocks) {
                final PromoEventBlocks promoEventBlock = new PromoEventBlocks();
                promoEventBlock.setEventId(promoEventBlockCrm.getEventId());
                promoEventBlock.setBlockId(promoEventBlockCrm.getBlockId());
                promoEventBlock.setDescription(promoEventBlockCrm.getDescription());
                promoEventBlock.setTickets(promoEventBlockCrm.getTickets());
                promoEventBlock.setMaxPerPlayer(promoEventBlockCrm.getMaxPerPlayer());
                promoEventBlock.setDefaultAmount(promoEventBlockCrm.getDefaultAmount());
                promoEventBlock.setStatus(promoEventBlockCrm.getStatus());
                promoEventBlock.setSiteId(promoEventBlockCrm.getSiteId());
                promoEventBlocksList.add(promoEventBlock);
            }

        }
        return promoEventBlocksList;

    }

}
