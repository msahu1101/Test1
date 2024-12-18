package com.mgmresorts.loyalty.transformer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.Exceptions;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.registry.StaticDataRegistry;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.dto.customer.SiteTypeCommon;
import com.mgmresorts.loyalty.dto.customer.XtraCreditBalancesLocal;
import com.mgmresorts.loyalty.dto.patron.balance.Body;
import com.mgmresorts.loyalty.dto.patron.balance.CRMPlayerBalance;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.master.SiteMetaData;
import com.mgmresorts.loyalty.service.BaseService;

public class BalanceTransformer extends BaseService implements IBalanceTransformer<BalanceWrapper, List<Balance>> {
    private final Logger logger = Logger.get(BalanceTransformer.class);
    private final String name = "balance";

    @Inject
    private JaxbContext jaxbContext;

    private StaticDataRegistry registry;

    private SiteMetaData siteDetails;

    @Override
    public BalanceWrapper toLeft(List<Balance> right) throws AppException {
        registry = StaticDataRegistry.get(SiteMetaData.class);
        final BalanceWrapper balanceData = new BalanceWrapper();
        final CRMPlayerBalance balanceXml;

        if (Utils.isEmpty(right)) {
            return balanceData;
        }
        /*
         * The expected result from the stored procedure is three xml pieces, of which
         * only one is a proper xml. These pieces can come in any permutation. To be
         * able to marshall this, a schema has been created that allows any order and
         * adds tags for the root-less xml pieces.
         */
        String xml = "<CRMPlayerBalance xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >";
        for (Balance object : right) {
            Balance entity = (Balance) object;
            xml += "<CRMChoice>" + entity.getXml() + "</CRMChoice>";
        }
        xml += "</CRMPlayerBalance>";

        // Preparing for unmarshalling
        // Unmarshaller unmarshallRequest = jaxbContext.getUnmarshaller();
        Unmarshaller unmarshallRequest = jaxbContext.getBalanceUnmarshaller();

        // ********Validation***********
        if (Boolean.valueOf(Runtime.get().getConfiguration("customer.balance.xml.validation"))) {
            validation(xml, unmarshallRequest);
        }

        // Unmarshalling the XML
        try {
            balanceXml = (CRMPlayerBalance) unmarshallRequest.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            logger.error("Failed to unmarshall the XML received from Patron in the " + name + " call.");
            throw Exceptions.wrap(e, Errors.INVALID_DATA, "Failed to unmarshall the XML received from Patron in the " + name + " call.");
        }

        // Assigning the unmarshalled XML piece POJOs for easy access, independent of
        // the order they arrived.
        CRMPlayerBalance.CRMChoice.CRMAcresMessage crmacresmessage = null;
        CRMPlayerBalance.CRMChoice.PlayerXtraCreditBalances playerXtraCreditBalances = null;
        List<CRMPlayerBalance.CRMChoice.TheoValues> theoValueSequence = null;
        for (CRMPlayerBalance.CRMChoice choice : balanceXml.getCRMChoice()) {
            if (choice.getCRMAcresMessage() != null) {
                crmacresmessage = choice.getCRMAcresMessage();
            } else if (choice.getPlayerXtraCreditBalances() != null) {
                playerXtraCreditBalances = choice.getPlayerXtraCreditBalances();
            } else if (choice.getTheoValues() != null && !choice.getTheoValues().isEmpty()) {
                theoValueSequence = choice.getTheoValues();
            } else {
                logger.warn("One of the XML segments in the " + name + " call did not match the expected segments.");
            }
        }

        // Checking whether the CRM Acres XML returned an error instead of balances.
        if (crmacresmessage == null) {
            logger.error(name + " call's XML response did not include the CRM portion.");
            throw new AppException(Errors.INVALID_DATA, name + " call's XML response did not include the CRM portion.");
        }
        if (crmacresmessage.getBody().getError() != null) {
            if (crmacresmessage.getBody().getError().getErrorCode().matches("INVALIDPLAYERID")) {
                logger.error("MLife number is invalid. " + crmacresmessage.getBody().getError().getErrorDescription());
                throw new AppException(Errors.INVALID_PATRON_ID, "MLife number is invalid.", crmacresmessage.getBody().getError().getErrorDescription());
            } else {
                logger.error(
                        String.format("CRM Code{%s}: ", crmacresmessage.getBody().getError().getErrorCode()) + " " + crmacresmessage.getBody().getError().getErrorDescription());
                throw new AppException(Errors.UNEXPECTED_SYSTEM, String.format("CRM Code{%s}: ", crmacresmessage.getBody().getError().getErrorCode()),
                        crmacresmessage.getBody().getError().getErrorDescription());
            }
        }
        // For ease of use, the reference is stored.
        Body.PlayerBalances playerBalance = crmacresmessage.getBody().getPlayerBalances();
        balanceData.getBalance().setRewardBalance(playerBalance.getRewardBalance());
        
        balanceData.getBalance().setCompBalance(playerBalance.getCompBalance().doubleValue());
        balanceData.getBalance().setSecondCompBalance(0.0);
        
        
        
        // Later this values will map with some other service
        balanceData.getBalance().setGiftPoints(playerBalance.getGiftPoints().doubleValue());
        // xtraCreditBalanceLocal is mapped in the next section
        // xtraCreditBalanceGlobal is mapped in the next section
        balanceData.getBalance().setPointBalance(playerBalance.getPointBalance().intValue());
        balanceData.getBalance().setCompBalanceLinked(playerBalance.getCompBalanceLinked().doubleValue());
        balanceData.getBalance().setSecondCompBalanceLinked(0.0);
        balanceData.getBalance().setPointBalanceLinked(playerBalance.getPointBalanceLinked().intValue());

        // giftPointBalancesInfo as well as xtraCreditBalanceLocal
        if (playerXtraCreditBalances == null) {
            logger.error(name + " call's XML reponse did not include the xtra credit balances portion.");
            throw new AppException(Errors.INVALID_DATA, name + " call's XML reponse did not include the xtra credit balances portion.");
        }
        BigDecimal xtraCreditBalanceLocal = BigDecimal.ZERO;
        XtraCreditBalancesLocal localBalance;
        balanceData.getBalance().setXtraCreditBalanceGlobal(playerXtraCreditBalances.getGlobalBalance().doubleValue());
        List<CRMPlayerBalance.CRMChoice.PlayerXtraCreditBalances.LocalBalances.LocalBalance> localBalanceList = playerXtraCreditBalances.getLocalBalances().getLocalBalance();
        for (int i = 0; i < localBalanceList.size(); i++) {
            localBalance = new XtraCreditBalancesLocal();
            localBalance.setSiteId(localBalanceList.get(i).getSiteID().toString());
            siteDetails = registry.cache(SiteMetaData.class).get(localBalanceList.get(i).getSiteID().toString());

            if (siteDetails != null) {
                localBalance.setSiteName(siteDetails.getName());
            } else {
                localBalance.setSiteName("");
            }

            localBalance.setXtraCreditBalanceLocal(localBalanceList.get(i).getBalance().doubleValue());
            xtraCreditBalanceLocal = xtraCreditBalanceLocal.add(localBalanceList.get(i).getBalance());
            balanceData.getBalance().getXtraCreditBalancesLocal().add(localBalance);
        }
        balanceData.getBalance().setXtraCreditBalanceLocal(xtraCreditBalanceLocal.doubleValue());

        // Site Info
        if (theoValueSequence == null) {
            logger.error(name + " call's XML reponse did not include the theo values portion.");
            throw new AppException(Errors.INVALID_DATA, name + " call's XML reponse did not include the theo values portion.");
        }
        if (!theoValueSequence.isEmpty()) {
            for (CRMPlayerBalance.CRMChoice.TheoValues theoValue : theoValueSequence) {
                final SiteTypeCommon siteTypeCommon = new SiteTypeCommon();

                siteTypeCommon.setSiteId(theoValue.getSiteID().toString());
                siteDetails = registry.cache(SiteMetaData.class).get(theoValue.getSiteID().toString());

                if (siteDetails != null) {
                    siteTypeCommon.setSiteName(siteDetails.getName());
                } else {
                    siteTypeCommon.setSiteName("");
                }
                // siteDescription is not currently mapped (in Tibco)
                siteTypeCommon.setId(theoValue.getID());
                siteTypeCommon.setTotalTheoWin(theoValue.getTotalTheoWin());
                siteTypeCommon.setRoomNights(theoValue.getRoomNights());
                siteTypeCommon.setLastStayDate(Dates.toZonedDateTime(theoValue.getLastStayDate()));
                siteTypeCommon.setSitemasterId(theoValue.getSitemasterID());
                siteTypeCommon.setSiteName(theoValue.getSiteName());
                siteTypeCommon.setSiteIdPm(theoValue.getSiteIDPM());
                siteTypeCommon.setConfigHotelSystemId(theoValue.getConfigHotelSystemID());
                siteTypeCommon.setCorpSortOrder(theoValue.getCorpSortOrder());

                balanceData.getBalance().getSites().getSiteInfo().add(siteTypeCommon);
            }
        }
        return balanceData;
    }

    private void validation(String xml, Unmarshaller unmarshaller) throws AppException {
        final InputStream inputStream;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("xml-schema/customer-balance/customer-balance.xsd");
            unmarshaller.setSchema(SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(inputStream)));
            Validator validator = unmarshaller.getSchema().newValidator();
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (SAXException e) {
            logger.error("The XML from source in the \" + name + \" call failed validation. {},{}", e.getMessage(), e);
            throw Exceptions.wrap(e, Errors.INVALID_DATA, "The XML from source in the " + name + " call failed validation.");
        } catch (IOException e) {
            logger.error("An IO exception occurred while validating the XML from source in the \" + name + \" call. {},{}", e.getMessage(), e);
            throw Exceptions.wrap(e, Errors.UNEXPECTED_SYSTEM, "An IO exception occurred while validating the XML from source in the " + name + " call.");
        }
    }

    @Override
    public Class<BalanceWrapper> getLeftType() {
        return BalanceWrapper.class;
    }

    @Override
    public List<Balance> toRight(BalanceWrapper left) {
        return null;
    }
}
