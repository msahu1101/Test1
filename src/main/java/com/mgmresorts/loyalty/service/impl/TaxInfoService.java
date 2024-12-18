package com.mgmresorts.loyalty.service.impl;

import java.text.DateFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.registry.StaticDataRegistry;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.ITaxInfoAccess;
import com.mgmresorts.loyalty.data.entity.TaxInformation;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxBySite;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxInfo;
import com.mgmresorts.loyalty.dto.customer.MonthlyInfoTaxes;
import com.mgmresorts.loyalty.dto.patron.taxinformation.ResultSet;
import com.mgmresorts.loyalty.dto.services.TaxInfoResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.master.SiteMetaData;
import com.mgmresorts.loyalty.service.ITaxInfoService;
import com.mgmresorts.loyalty.transformer.ITransformer;
import com.mgmresorts.profile.dto.common.Address;

public class TaxInfoService implements ITaxInfoService {

    private final Logger logger = Logger.get(TaxInfoService.class);
    private final DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.tax-information");

    @Inject
    private ITaxInfoAccess customerTaxInfoDataAccess;

    @Inject
    private ITransformer<CustomerTaxInfo, ResultSet> customerTaxInfoTransformer;

    private final StaticDataRegistry registry;

    private SiteMetaData siteDetails;

    public TaxInfoService() throws AppException {
        this.registry = StaticDataRegistry.get(SiteMetaData.class);
    }

    @Override
    public TaxInfoResponse readTaxInfoCustomerResponse(String mlife, String yearString, String quarterString) throws AppException {
        return circuit.flow(() -> getTaxInfoCustomer(mlife, yearString, quarterString), SystemError.UNABLE_TO_CALL_BACKEND);
    }

    private TaxInfoResponse getTaxInfoCustomer(String mlife, String yearString, String quarterString) throws AppException {
        logger.trace("Customer profile Read Tax Information service invoked...");

        final TaxInfoResponse taxInfoResponse = new TaxInfoResponse();

        if (!Utils.isNumeric(mlife) || !Utils.isNumeric(yearString)) {
            throw new AppException(Errors.REQUESTED_RESOURCE_NOT_FOUND);
        }

        final int mlifeNumber = Integer.parseInt(mlife);
        final int year = Integer.parseInt(yearString);
        final int quart = Utils.isNumeric(quarterString) ? Integer.parseInt(quarterString) : -1;

        CustomerTaxInfo calculatedTaxInfo = null;
        if (quart != -1) {
            calculatedTaxInfo = quarterlyTaxInfo(mlifeNumber, year, quart);
        } else {
            calculatedTaxInfo = yearlyTaxInfo(mlifeNumber, year);
        }
        taxInfoResponse.setCustomerTaxInfo(calculatedTaxInfo);
        taxInfoResponse.setHeader(HeaderBuilder.buildHeader());
        return taxInfoResponse;
    }

    private CustomerTaxInfo quarterlyTaxInfo(final int mlifeNumber, final int year, final int quart) throws AppException {
        final Long startTime = Instant.now().toEpochMilli();
        logger.info("Tax informations DB execution starts here:: ", startTime);
        final List<TaxInformation> patronResultList = customerTaxInfoDataAccess.readPatronTaxInfoCustomerResponse(mlifeNumber, year, quart);
        final Long endTime = Instant.now().toEpochMilli();
        final Long elapsedTime = endTime - startTime;

        logger.info("Tax informations DB execution end here:: ", elapsedTime);

        if (Utils.isEmpty(patronResultList)) {
            throw new AppException(ApplicationError.NO_TAX_INFORMATION);
        }
        final ResultSet resultSet = JaxbContext.getInstance().unmarshal(Utils.first(patronResultList).getResult(), ResultSet.class);
        final CustomerTaxInfo customerTaxInfo = customerTaxInfoTransformer.toLeft(resultSet);

        final List<CustomerTaxBySite> initialSiteTotals = customerTaxInfo.getSiteTotals();
        final Set<Entry<String, List<CustomerTaxBySite>>> siteIdTaxes = initialSiteTotals.stream().collect(Collectors.groupingBy(data -> data.getSiteId())).entrySet();
        final List<CustomerTaxBySite> taxBySiteId = siteIdTaxes.stream().map((entry) -> {
            final CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
            final String key = entry.getKey();

            final List<CustomerTaxBySite> value = entry.getValue();
            customerTaxBySite.setCoinIn(value.stream().mapToDouble(CustomerTaxBySite::getCoinIn).sum());
            customerTaxBySite.setCoinOut(value.stream().mapToDouble(CustomerTaxBySite::getCoinOut).sum());
            customerTaxBySite.setJackPot(value.stream().mapToDouble(CustomerTaxBySite::getJackPot).sum());
            customerTaxBySite.setSlotWin(value.stream().mapToDouble(CustomerTaxBySite::getSlotWin).sum());
            customerTaxBySite.setTableWin(value.stream().mapToDouble(CustomerTaxBySite::getTableWin).sum());
            customerTaxBySite.setTotalWin(value.stream().mapToDouble(CustomerTaxBySite::getTotalWin).sum());
            customerTaxBySite.setSiteId(key);
            return customerTaxBySite;
        }).collect(Collectors.toList());

        final CustomerTaxInfo calcualtedQuarterlyTax = calculateTotalForQuarter(customerTaxInfo, taxBySiteId);
        return calcualtedQuarterlyTax;

    }

    private CustomerTaxInfo calculateTotalForQuarter(CustomerTaxInfo originalTaxInfo, List<CustomerTaxBySite> taxBySiteId) throws AppException {

        final CustomerTaxInfo customerTaxInfoFinal = new CustomerTaxInfo();
        final Set<Entry<String, List<CustomerTaxBySite>>> siteIdTaxes = originalTaxInfo.getSiteTotals().stream().collect(Collectors.groupingBy(data -> data.getSiteId()))
                .entrySet();
        final Map<String, List<MonthlyInfoTaxes>> siteIdVsMonthlyTaxes = siteIdTaxes.stream().map((entry) -> {
            final List<MonthlyInfoTaxes> monthlyInfoTaxes = new ArrayList<>();
            final String key = entry.getKey();
            final List<CustomerTaxBySite> values = entry.getValue();
            for (CustomerTaxBySite site : values) {
                final MonthlyInfoTaxes monthlyInfoTax = new MonthlyInfoTaxes();
                monthlyInfoTax.setCoinIn(site.getCoinIn().doubleValue());
                monthlyInfoTax.setCoinOut(site.getCoinOut().doubleValue());
                monthlyInfoTax.setJackPot(site.getJackPot().doubleValue());
                monthlyInfoTax.setSlotWin(site.getSlotWin().doubleValue());
                monthlyInfoTax.setTableWin(site.getTableWin().doubleValue());
                monthlyInfoTax.setTotalWin(site.getTotalWin().doubleValue());
                final String numericQuarter = site.getMonth();
                if (Utils.isNumeric(numericQuarter)) {
                    final String month = dateFormatSymbols.getMonths()[Integer.parseInt(numericQuarter) - 1];
                    monthlyInfoTax.setMonth(month);
                }
                monthlyInfoTax.setSiteId(key);
                monthlyInfoTaxes.add(monthlyInfoTax);
            }
            return monthlyInfoTaxes;
        }).collect(Collectors.toMap(s -> Utils.first(s).getSiteId(), s -> s));

        // Monthly tax info Display in response
        final List<CustomerTaxBySite> siteTotalsResponse = new ArrayList<>();
        for (CustomerTaxBySite siteTotals : taxBySiteId) {
            final CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
            final List<MonthlyInfoTaxes> list = siteIdVsMonthlyTaxes.get(siteTotals.getSiteId());
            final List<MonthlyInfoTaxes> monthlyInfoTaxResponse = new ArrayList<>();
            list.forEach(monthlyInfoTaxes -> {
                MonthlyInfoTaxes monthlyInfo = new MonthlyInfoTaxes();
                monthlyInfo.setCoinIn(monthlyInfoTaxes.getCoinIn());
                monthlyInfo.setCoinOut(monthlyInfoTaxes.getCoinOut());
                monthlyInfo.setJackPot(monthlyInfoTaxes.getJackPot());
                monthlyInfo.setSlotWin(monthlyInfoTaxes.getSlotWin());
                monthlyInfo.setTableWin(monthlyInfoTaxes.getTableWin());
                monthlyInfo.setTotalWin(monthlyInfoTaxes.getTotalWin());
                monthlyInfo.setMonth(monthlyInfoTaxes.getMonth());
                monthlyInfoTaxResponse.add(monthlyInfo);
                customerTaxBySite.setMonthlyInfoTaxes(monthlyInfoTaxResponse);

            });
            customerTaxBySite.setCoinIn(siteTotals.getCoinIn());
            customerTaxBySite.setCoinOut(siteTotals.getCoinOut());
            customerTaxBySite.setJackPot(siteTotals.getJackPot());
            customerTaxBySite.setSlotWin(siteTotals.getSlotWin());
            customerTaxBySite.setTableWin(siteTotals.getTableWin());
            customerTaxBySite.setTotalWin(siteTotals.getTotalWin());
            customerTaxBySite.setSiteId(siteTotals.getSiteId());
            siteDetails = registry.cache(SiteMetaData.class).get(siteTotals.getSiteId());
            if (siteDetails != null) {
                customerTaxBySite.setSiteName(siteDetails.getName());
            } else {
                customerTaxBySite.setSiteName("");
            }
            siteTotalsResponse.add(customerTaxBySite);
        }
        Collections.sort(siteTotalsResponse, new Comparator<CustomerTaxBySite>() {
            @Override
            public int compare(CustomerTaxBySite o1, CustomerTaxBySite o2) {
                return o1.getSiteId().compareTo(o2.getSiteId());
            }
        });
        customerTaxInfoFinal.setSiteTotals(siteTotalsResponse);
        customerTaxInfoFinal.setCustomerId(originalTaxInfo.getCustomerId());
        customerTaxInfoFinal.setTitle(originalTaxInfo.getTitle());
        customerTaxInfoFinal.setFirstName(originalTaxInfo.getFirstName());
        customerTaxInfoFinal.setLastName(originalTaxInfo.getLastName());
        Address address = new Address();
        if (originalTaxInfo.getAddress() != null) {
            address.setStreet1(originalTaxInfo.getAddress().getStreet1());
            address.setStreet2(originalTaxInfo.getAddress().getStreet2());
            address.setCity(originalTaxInfo.getAddress().getCity());
            address.setState(originalTaxInfo.getAddress().getState());
            address.setCountry(originalTaxInfo.getAddress().getCountry());
            address.setZipCode(originalTaxInfo.getAddress().getZipCode());
            customerTaxInfoFinal.setAddress(address);
        }
        customerTaxInfoFinal.setTotalCoinIn(originalTaxInfo.getTotalCoinIn());
        customerTaxInfoFinal.setTotalCoinOut(originalTaxInfo.getTotalCoinOut());
        customerTaxInfoFinal.setTotalJackPot(originalTaxInfo.getTotalJackPot());
        customerTaxInfoFinal.setTotalSlotWin(originalTaxInfo.getTotalSlotWin());
        customerTaxInfoFinal.setTotalTableWin(originalTaxInfo.getTotalTableWin());
        customerTaxInfoFinal.setTotalWin(originalTaxInfo.getTotalWin());

        return customerTaxInfoFinal;
    }

    private CustomerTaxInfo yearlyTaxInfo(final int mlifeNumber, final int year) throws AppException {
        final List<CustomerTaxInfo> customerTaxInfoList = new ArrayList<>();
        final List<List<CustomerTaxBySite>> allTaxInfos = new ArrayList<>();
        final List<CustomerTaxBySite> quarterlySiteInfo = new ArrayList<>();
        final Long startTime = Instant.now().toEpochMilli();
        logger.info("Yearly Tax informations DB execution starts here:: ", startTime);

        for (int quarter = 1; quarter <= 4; quarter++) {
            final CustomerTaxInfo customerTaxInfoCopy = new CustomerTaxInfo();
            final List<TaxInformation> patronResultList = customerTaxInfoDataAccess.readPatronTaxInfoCustomerResponse(mlifeNumber, year, quarter);
            if (Utils.isEmpty(patronResultList)) {
                continue;
            }
            final ResultSet resultSet = JaxbContext.getInstance().unmarshal(patronResultList.get(0).getResult(), ResultSet.class);
            final CustomerTaxInfo customerTaxInfo = customerTaxInfoTransformer.toLeft(resultSet);
            final Set<Entry<String, List<CustomerTaxBySite>>> entrySet = customerTaxInfo.getSiteTotals().stream().collect(Collectors.groupingBy(data -> data.getSiteId()))
                    .entrySet();
            // Site total sum
            final List<CustomerTaxBySite> siteIdTaxes = entrySet.stream().map((entry) -> {
                final CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
                final String key = entry.getKey();
                final List<CustomerTaxBySite> value = entry.getValue();
                customerTaxBySite.setCoinIn(value.stream().mapToDouble(CustomerTaxBySite::getCoinIn).sum());
                customerTaxBySite.setCoinOut(value.stream().mapToDouble(CustomerTaxBySite::getCoinOut).sum());
                customerTaxBySite.setJackPot(value.stream().mapToDouble(CustomerTaxBySite::getJackPot).sum());
                customerTaxBySite.setSlotWin(value.stream().mapToDouble(CustomerTaxBySite::getSlotWin).sum());
                customerTaxBySite.setTableWin(value.stream().mapToDouble(CustomerTaxBySite::getTableWin).sum());
                customerTaxBySite.setTotalWin(value.stream().mapToDouble(CustomerTaxBySite::getTotalWin).sum());
                customerTaxBySite.setSiteId(key);
                return customerTaxBySite;
            }).collect(Collectors.toList());
            quarterlySiteInfo.addAll(siteIdTaxes);

            allTaxInfos.add(customerTaxInfo.getSiteTotals());

            // Added customer name ,address and other ......
            customerTaxInfoCopy.setCustomerId(customerTaxInfo.getCustomerId());
            customerTaxInfoCopy.setTitle(customerTaxInfo.getTitle());
            customerTaxInfoCopy.setFirstName(customerTaxInfo.getFirstName());
            customerTaxInfoCopy.setLastName(customerTaxInfo.getLastName());
            Address address = new Address();
            if (customerTaxInfo.getAddress() != null) {
                address.setStreet1(customerTaxInfo.getAddress().getStreet1());
                address.setStreet2(customerTaxInfo.getAddress().getStreet2());
                address.setCity(customerTaxInfo.getAddress().getCity());
                address.setState(customerTaxInfo.getAddress().getState());
                address.setCountry(customerTaxInfo.getAddress().getCountry());
                address.setZipCode(customerTaxInfo.getAddress().getZipCode());
                customerTaxInfoCopy.setAddress(address);
            }
            customerTaxInfoCopy.setTotalCoinIn(customerTaxInfo.getTotalCoinIn());
            customerTaxInfoCopy.setTotalCoinOut(customerTaxInfo.getTotalCoinOut());
            customerTaxInfoCopy.setTotalJackPot(customerTaxInfo.getTotalJackPot());
            customerTaxInfoCopy.setTotalSlotWin(customerTaxInfo.getTotalSlotWin());
            customerTaxInfoCopy.setTotalTableWin(customerTaxInfo.getTotalTableWin());
            customerTaxInfoCopy.setTotalWin(customerTaxInfo.getTotalWin());

            customerTaxInfoList.add(customerTaxInfoCopy);
        }

        Long endTime = Instant.now().toEpochMilli();
        Long elapsedTime = endTime - startTime;

        logger.info("Yearly Tax informations DB execution end here:: ", elapsedTime);

        final CustomerTaxInfo annualCustomerTaxInfo = getAnnualTaxInfo(customerTaxInfoList, quarterlySiteInfo, allTaxInfos);

        return annualCustomerTaxInfo;
    }

    // Yearly tax calculations..
    private CustomerTaxInfo getAnnualTaxInfo(List<CustomerTaxInfo> customerTaxInfoList, List<CustomerTaxBySite> quarterlySiteInfo, List<List<CustomerTaxBySite>> allTaxInfos)
            throws AppException {
        final CustomerTaxInfo first = Utils.first(customerTaxInfoList);
        final CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();
        customerTaxInfo.setTotalCoinIn(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalCoinIn).sum());
        customerTaxInfo.setTotalCoinOut(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalCoinOut).sum());
        customerTaxInfo.setTotalJackPot(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalJackPot).sum());
        customerTaxInfo.setTotalSlotWin(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalSlotWin).sum());
        customerTaxInfo.setTotalTableWin(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalTableWin).sum());
        customerTaxInfo.setTotalWin(customerTaxInfoList.stream().mapToDouble(CustomerTaxInfo::getTotalWin).sum());
        customerTaxInfo.setCustomerId(first.getCustomerId());
        customerTaxInfo.setTitle(first.getTitle());
        customerTaxInfo.setFirstName(first.getFirstName());
        customerTaxInfo.setLastName(first.getLastName());

        if (first.getAddress() != null) {
            final Address address = new Address();
            address.setStreet1(first.getAddress().getStreet1());
            address.setStreet2(first.getAddress().getStreet2());
            address.setCity(first.getAddress().getCity());
            address.setState(first.getAddress().getState());
            address.setCountry(first.getAddress().getCountry());
            address.setZipCode(first.getAddress().getZipCode());
            customerTaxInfo.setAddress(address);
        }
        // Sum of site totals
        final List<CustomerTaxBySite> siteIdTaxes = quarterlySiteInfo.stream().collect(Collectors.groupingBy(data -> data.getSiteId())).entrySet().stream().map((entry) -> {
            final CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
            final String key = entry.getKey();
            final List<CustomerTaxBySite> value = entry.getValue();
            customerTaxBySite.setCoinIn(value.stream().mapToDouble(CustomerTaxBySite::getCoinIn).sum());
            customerTaxBySite.setCoinOut(value.stream().mapToDouble(CustomerTaxBySite::getCoinOut).sum());
            customerTaxBySite.setJackPot(value.stream().mapToDouble(CustomerTaxBySite::getJackPot).sum());
            customerTaxBySite.setSlotWin(value.stream().mapToDouble(CustomerTaxBySite::getSlotWin).sum());
            customerTaxBySite.setTableWin(value.stream().mapToDouble(CustomerTaxBySite::getTableWin).sum());
            customerTaxBySite.setTotalWin(value.stream().mapToDouble(CustomerTaxBySite::getTotalWin).sum());
            customerTaxBySite.setSiteId(key);

            return customerTaxBySite;
        }).collect(Collectors.toList());

        final CustomerTaxInfo taxInfoFinal = new CustomerTaxInfo();
        final Address adds = new Address();
        taxInfoFinal.setCustomerId(customerTaxInfo.getCustomerId());
        taxInfoFinal.setTitle(customerTaxInfo.getTitle());
        taxInfoFinal.setFirstName(customerTaxInfo.getFirstName());
        taxInfoFinal.setLastName(customerTaxInfo.getLastName());
        if (customerTaxInfo.getAddress() != null) {
            adds.setStreet1(customerTaxInfo.getAddress().getStreet1());
            adds.setStreet2(customerTaxInfo.getAddress().getStreet2());
            adds.setCity(customerTaxInfo.getAddress().getCity());
            adds.setState(customerTaxInfo.getAddress().getState());
            adds.setCountry(customerTaxInfo.getAddress().getCountry());
            adds.setZipCode(customerTaxInfo.getAddress().getZipCode());
            taxInfoFinal.setAddress(adds);
        }
        taxInfoFinal.setTotalCoinIn(customerTaxInfo.getTotalCoinIn());
        taxInfoFinal.setTotalCoinOut(customerTaxInfo.getTotalCoinOut());
        taxInfoFinal.setTotalJackPot(customerTaxInfo.getTotalJackPot());
        taxInfoFinal.setTotalSlotWin(customerTaxInfo.getTotalSlotWin());
        taxInfoFinal.setTotalTableWin(customerTaxInfo.getTotalTableWin());
        taxInfoFinal.setTotalWin(customerTaxInfo.getTotalWin());
        return calculateYearlyMonthlyTax(siteIdTaxes, allTaxInfos, taxInfoFinal);
    }

    private CustomerTaxInfo calculateYearlyMonthlyTax(List<CustomerTaxBySite> siteIdTaxes, List<List<CustomerTaxBySite>> allTaxInfos, CustomerTaxInfo taxInfoFinal)
            throws AppException {

        final Map<String, List<CustomerTaxBySite>> siteIdVsTaxes = allTaxInfos.stream().flatMap(s -> s.stream()).collect(Collectors.groupingBy(data -> data.getSiteId()));

        final CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();

        final List<CustomerTaxBySite> siteTotals = new ArrayList<>();

        for (CustomerTaxBySite siteTotal : siteIdTaxes) {
            final List<MonthlyInfoTaxes> monthlyInfoTaxes = new ArrayList<>();
            final List<CustomerTaxBySite> taxes = siteIdVsTaxes.get(siteTotal.getSiteId());
            final CustomerTaxBySite siteTotalObject = new CustomerTaxBySite();
            for (CustomerTaxBySite tax : taxes) {
                MonthlyInfoTaxes monthlyInfoTax = new MonthlyInfoTaxes();
                monthlyInfoTax.setCoinIn(tax.getCoinIn());
                monthlyInfoTax.setCoinOut(tax.getCoinOut());
                monthlyInfoTax.setJackPot(tax.getJackPot());
                monthlyInfoTax.setSlotWin(tax.getSlotWin());
                monthlyInfoTax.setTableWin(tax.getTableWin());
                monthlyInfoTax.setTotalWin(tax.getTotalWin());
                monthlyInfoTax.setMonth(tax.getMonth());
                final String numericQuarter = tax.getMonth();
                if (Utils.isNumeric(numericQuarter)) {
                    final String month = dateFormatSymbols.getMonths()[Integer.parseInt(numericQuarter) - 1];
                    monthlyInfoTax.setMonth(month);
                }
                monthlyInfoTaxes.add(monthlyInfoTax);
            }
            siteTotalObject.setSiteId(siteTotal.getSiteId());
            siteDetails = registry.cache(SiteMetaData.class).get(siteTotal.getSiteId());
            if (siteDetails != null) {
                siteTotalObject.setSiteName(siteDetails.getName());
            } else {
                siteTotalObject.setSiteName("");
            }
            siteTotalObject.setCoinIn(siteTotal.getCoinIn());
            siteTotalObject.setCoinOut(siteTotal.getCoinOut());
            siteTotalObject.setJackPot(siteTotal.getJackPot());
            siteTotalObject.setSlotWin(siteTotal.getSlotWin());
            siteTotalObject.setTableWin(siteTotal.getTableWin());
            siteTotalObject.setTotalWin(siteTotal.getTotalWin());
            siteTotalObject.setMonthlyInfoTaxes(monthlyInfoTaxes);
            siteTotals.add(siteTotalObject);
            customerTaxInfo.setSiteTotals(siteTotals);
        }
        customerTaxInfo.setCustomerId(taxInfoFinal.getCustomerId());
        customerTaxInfo.setTitle(taxInfoFinal.getTitle());
        customerTaxInfo.setFirstName(taxInfoFinal.getFirstName());
        customerTaxInfo.setLastName(taxInfoFinal.getLastName());
        final Address address = new Address();
        if (customerTaxInfo.getAddress() != null) {
            address.setStreet1(customerTaxInfo.getAddress().getStreet1());
            address.setStreet2(customerTaxInfo.getAddress().getStreet2());
            address.setCity(customerTaxInfo.getAddress().getCity());
            address.setState(customerTaxInfo.getAddress().getState());
            address.setCountry(customerTaxInfo.getAddress().getCountry());
            address.setZipCode(customerTaxInfo.getAddress().getZipCode());
            taxInfoFinal.setAddress(address);
        }
        customerTaxInfo.setTotalCoinIn(taxInfoFinal.getTotalCoinIn());
        customerTaxInfo.setTotalCoinOut(taxInfoFinal.getTotalCoinOut());
        customerTaxInfo.setTotalJackPot(taxInfoFinal.getTotalJackPot());
        customerTaxInfo.setTotalSlotWin(taxInfoFinal.getTotalSlotWin());
        customerTaxInfo.setTotalTableWin(taxInfoFinal.getTotalTableWin());
        customerTaxInfo.setTotalWin(taxInfoFinal.getTotalWin());
        return customerTaxInfo;
    }
}