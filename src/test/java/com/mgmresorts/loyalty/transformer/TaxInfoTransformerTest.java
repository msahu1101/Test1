package com.mgmresorts.loyalty.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxBySite;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxInfo;
import com.mgmresorts.loyalty.dto.customer.MonthlyInfoTaxes;
import com.mgmresorts.loyalty.dto.patron.taxinformation.ResultSet;
import com.mgmresorts.loyalty.dto.services.TaxInfoResponse;

public class TaxInfoTransformerTest {

    @InjectMocks
    private TaxInfoTransformer customerTaxInfoTransformer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testCustReadTaxInfo() throws IOException, JAXBException, AppException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/cust_tax_info_patron_resultset.xml").getFile());
        String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        final ResultSet unmarshal = JaxbContext.getInstance().unmarshal(result, ResultSet.class);
        TaxInfoResponse entityResponse = buildCustomerTaxInfo();
        final CustomerTaxInfo taxInfoResponse = customerTaxInfoTransformer.toLeft(unmarshal);
        assertNotNull(taxInfoResponse);
        assertEquals(entityResponse.getCustomerTaxInfo().getCustomerId(), taxInfoResponse.getCustomerId());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalCoinIn(), taxInfoResponse.getTotalCoinIn());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalCoinOut(), taxInfoResponse.getTotalCoinOut());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalJackPot(), taxInfoResponse.getTotalJackPot());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalSlotWin(), taxInfoResponse.getTotalSlotWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalTableWin(), taxInfoResponse.getTotalTableWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getTotalWin(), taxInfoResponse.getTotalWin());

        taxInfoResponse.getSiteTotals().forEach(site -> {
            assertNotNull(site.getCoinIn());
            assertNotNull(site.getCoinOut());
            assertNotNull(site.getJackPot());
            assertNotNull(site.getSiteId());
            assertNotNull(site.getSlotWin());
            assertNotNull(site.getTableWin());
            assertNotNull(site.getTotalWin());
            assertNotNull(site.getMonthlyInfoTaxes());
        });

        taxInfoResponse.getSiteTotals().get(0).getMonthlyInfoTaxes().forEach(site -> {
            assertNotNull(site.getCoinIn());
            assertNotNull(site.getCoinOut());
            assertNotNull(site.getJackPot());
            assertNotNull(site.getMonth());
            assertNotNull(site.getSlotWin());
            assertNotNull(site.getTableWin());
            assertNotNull(site.getTotalWin());
        });

        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinIn(), taxInfoResponse.getSiteTotals().get(0).getCoinIn());

        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinOut(), taxInfoResponse.getSiteTotals().get(0).getCoinOut());

        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getJackPot(), taxInfoResponse.getSiteTotals().get(0).getJackPot());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getSlotWin(), taxInfoResponse.getSiteTotals().get(0).getSlotWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTableWin(), taxInfoResponse.getSiteTotals().get(0).getTableWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTotalWin(), taxInfoResponse.getSiteTotals().get(0).getTotalWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinIn(), taxInfoResponse.getSiteTotals().get(1).getCoinIn());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinOut(), taxInfoResponse.getSiteTotals().get(1).getCoinOut());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getJackPot(), taxInfoResponse.getSiteTotals().get(1).getJackPot());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getSlotWin(), taxInfoResponse.getSiteTotals().get(1).getSlotWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTableWin(), taxInfoResponse.getSiteTotals().get(1).getTableWin());
        assertEquals(entityResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTotalWin(), taxInfoResponse.getSiteTotals().get(1).getTotalWin());
    }

    @Test
    public void testCustReadTaxInfoWithNoPlayerId() throws IOException, JAXBException, AppException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("data/cust_tax_info_patron_resultset_no_playerid.xml").getFile());
        String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        final ResultSet unmarshal = JaxbContext.getInstance().unmarshal(result, ResultSet.class);
        Assertions.assertThrows(AppException.class, () -> {
            customerTaxInfoTransformer.toLeft(unmarshal);
        });
    }

    @Test
    public void testCustReadTaxInfotoRight() throws AppException {
        Assertions.assertThrows(AppException.class, () -> {
            customerTaxInfoTransformer.toRight(new CustomerTaxInfo());
        });
    }

    private TaxInfoResponse buildCustomerTaxInfo() {
        TaxInfoResponse taxInfoResponse = new TaxInfoResponse();
        CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();
        customerTaxInfo.setCustomerId("12345678");
        customerTaxInfo.setTotalCoinIn(0.0000);
        customerTaxInfo.setTotalCoinOut(0.0000);
        customerTaxInfo.setTotalJackPot(0.0000);
        customerTaxInfo.setTotalSlotWin(0.0000);
        customerTaxInfo.setTotalTableWin(0.0000);
        customerTaxInfo.setTotalWin(0.0000);
        customerTaxInfo.setSiteTotals(buildSiteDetails());
        taxInfoResponse.setCustomerTaxInfo(customerTaxInfo);
        return taxInfoResponse;
    }

    private List<CustomerTaxBySite> buildSiteDetails() {
        Set<CustomerTaxBySite> customerTaxBySiteSet = new LinkedHashSet<>();
        CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
        customerTaxBySite.setSiteId("1");
        customerTaxBySite.setCoinIn(0.0000);
        customerTaxBySite.setCoinOut(0.0000);
        customerTaxBySite.setJackPot(0.0000);
        customerTaxBySite.setSlotWin(0.0000);
        customerTaxBySite.setTableWin(0.0000);
        customerTaxBySite.setTotalWin(0.0000);
        customerTaxBySite.setMonthlyInfoTaxes(buildMonthSiteObject());
        customerTaxBySiteSet.add(customerTaxBySite);
        CustomerTaxBySite customerTaxBySiteOne = new CustomerTaxBySite();
        customerTaxBySiteOne.setSiteId("6");
        customerTaxBySiteOne.setCoinIn(0.0000);
        customerTaxBySiteOne.setCoinOut(0.0000);
        customerTaxBySiteOne.setJackPot(0.0000);
        customerTaxBySiteOne.setSlotWin(0.0000);
        customerTaxBySiteOne.setTableWin(0.0000);
        customerTaxBySiteOne.setTotalWin(0.0000);
        customerTaxBySiteOne.setMonthlyInfoTaxes(buildMonthSiteObject());
        customerTaxBySiteSet.add(customerTaxBySiteOne);
        return customerTaxBySiteSet.stream().collect(Collectors.toList());
    }

    private List<MonthlyInfoTaxes> buildMonthSiteObject() {
        List<MonthlyInfoTaxes> monthlyInfoTaxesList = new ArrayList<>();
        MonthlyInfoTaxes monthlyInfoTaxes = new MonthlyInfoTaxes();
        monthlyInfoTaxes.setMonth("2");
        monthlyInfoTaxes.setCoinIn(0.0000);
        monthlyInfoTaxes.setCoinOut(0.0000);
        monthlyInfoTaxes.setJackPot(0.0000);
        monthlyInfoTaxes.setSlotWin(0.0000);
        monthlyInfoTaxes.setTableWin(0.0000);
        monthlyInfoTaxes.setTotalWin(0.0000);
        monthlyInfoTaxesList.add(monthlyInfoTaxes);
        return monthlyInfoTaxesList;
    }

    @Test
    public void testToRight() throws AppException {
        CustomerTaxInfo left = null;
        ResultSet result = null;
        try {
            result = customerTaxInfoTransformer.toRight(left);
        } catch (Exception e) {

        }
        assertNull(result);
    }

}
