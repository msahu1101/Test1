package com.mgmresorts.loyalty.transformer;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxBySite;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxInfo;
import com.mgmresorts.loyalty.dto.patron.taxinformation.ResultSet;
import com.mgmresorts.loyalty.dto.patron.taxinformation.Site;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.profile.dto.common.Address;

public class TaxInfoTransformer implements ITransformer<CustomerTaxInfo, ResultSet> {

    private final Logger logger = Logger.get(TaxInfoTransformer.class);

    public CustomerTaxInfo toLeft(ResultSet patronResultSetPojo) throws AppException {

        logger.trace("Customer profile Read Tax Information transformer invoked...");
        if (!Utils.anyNull(patronResultSetPojo.getBody().getErrorMessage())) {
            logger.trace(patronResultSetPojo.getBody().getErrorMessage());
            throw new AppException(Errors.NO_TAX_INFORMATION);
        }
        final CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();
        customerTaxInfo.setCustomerId(patronResultSetPojo.getBody().getPlayerDetails().getPlayerID());

        if (patronResultSetPojo.getBody().getPlayerDetails().getName() != null) {
            customerTaxInfo.setTitle(patronResultSetPojo.getBody().getPlayerDetails().getName().getTitle());
            customerTaxInfo.setFirstName(patronResultSetPojo.getBody().getPlayerDetails().getName().getFirstName());
            customerTaxInfo.setLastName(patronResultSetPojo.getBody().getPlayerDetails().getName().getLastName());
        }

        if (patronResultSetPojo.getBody().getPlayerDetails().getAddress() != null) {
            Address address = new Address();
            address.setStreet1(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getAddressLine1());
            address.setStreet2(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getAddressLine2());
            address.setCity(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getMailingCity());
            address.setState(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getMailingState());
            address.setCountry(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getMailingCountry());
            address.setZipCode(patronResultSetPojo.getBody().getPlayerDetails().getAddress().getZipCode());
            customerTaxInfo.setAddress(address);
        }
        customerTaxInfo.setTotalCoinIn(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalCoinIn().doubleValue());
        customerTaxInfo.setTotalCoinOut(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalCoinOut().doubleValue());
        customerTaxInfo.setTotalJackPot(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalJackPot().doubleValue());
        customerTaxInfo.setTotalSlotWin(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalSlotWin().doubleValue());
        customerTaxInfo.setTotalTableWin(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalTableWin().doubleValue());
        customerTaxInfo.setTotalWin(patronResultSetPojo.getBody().getPlayerDetails().getTotalPlayDetails().getTotalTotalWin().doubleValue());
        if (!Utils.anyNull(patronResultSetPojo.getBody().getPlayerDetails().getSite())) {
            customerTaxInfo.setSiteTotals(mapSiteDetails(patronResultSetPojo.getBody().getPlayerDetails().getSite()));
        }
        return customerTaxInfo;
    }

    private List<CustomerTaxBySite> mapSiteDetails(List<Site> sitesList) {
        final Set<CustomerTaxBySite> customerTaxBySiteSet = new LinkedHashSet<>();

        sitesList.forEach(site -> {
            CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
            customerTaxBySite.setSiteId(site.getID());
            customerTaxBySite.setCoinIn(site.getCoinIn().doubleValue());
            customerTaxBySite.setCoinOut(site.getCoinOut().doubleValue());
            customerTaxBySite.setJackPot(site.getJackpot().doubleValue());
            customerTaxBySite.setSlotWin(site.getSlotWin().doubleValue());
            customerTaxBySite.setTableWin(site.getTableWin().doubleValue());
            customerTaxBySite.setTotalWin(site.getTotalWin().doubleValue());
            customerTaxBySite.setMonth(site.getMonth());
            customerTaxBySiteSet.add(customerTaxBySite);
        });

        return customerTaxBySiteSet.stream().collect(Collectors.toList());
    }

    @Override
    public ResultSet toRight(CustomerTaxInfo left) throws AppException {
        throw new AppException(Errors.UNSUPPORTED_OPERATION);
    }

}
