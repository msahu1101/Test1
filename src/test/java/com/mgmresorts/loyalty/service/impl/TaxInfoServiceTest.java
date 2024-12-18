package com.mgmresorts.loyalty.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.entity.TaxInformation;
import com.mgmresorts.loyalty.data.impl.TaxInfoAccess;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxBySite;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxInfo;
import com.mgmresorts.loyalty.dto.customer.MonthlyInfoTaxes;
import com.mgmresorts.loyalty.dto.patron.taxinformation.ResultSet;
import com.mgmresorts.loyalty.dto.services.TaxInfoResponse;
import com.mgmresorts.loyalty.transformer.TaxInfoTransformer;

public class TaxInfoServiceTest {

    @InjectMocks
    private TaxInfoService customerTaxInfoService;

    @Mock
    public TaxInfoAccess customerTaxInfoDataAccessMock;

    @Spy
    private TaxInfoTransformer customerTaxInfoTransformer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReadTaxInfo() throws IOException, JAXBException, AppException {
        String mlifeNumber = "12345678";
        String year = "2019";
        String quarter = "4";
        CustomerTaxInfo entityResponse = buildCustomerTaxInfo();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("data/cust_tax_info_patron_resultset.xml").getFile());
        String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        final ResultSet unmarshal = JaxbContext.getInstance().unmarshal(result, ResultSet.class);

        TaxInformation information = new TaxInformation();
        information.setResult(result);
        List<TaxInformation> informations = new ArrayList<TaxInformation>();
        informations.add(information);
        when(customerTaxInfoDataAccessMock.readPatronTaxInfoCustomerResponse(anyInt(), anyInt(), anyInt())).thenReturn(informations);
        when(customerTaxInfoTransformer.toLeft(unmarshal)).thenReturn(buildCustomerTaxInfo());
        TaxInfoResponse taxInfoResponse = customerTaxInfoService.readTaxInfoCustomerResponse(mlifeNumber, year, quarter);
        assertNotNull(taxInfoResponse);
        assertEquals(entityResponse.getTotalCoinIn(), taxInfoResponse.getCustomerTaxInfo().getTotalCoinIn());
        assertEquals(entityResponse.getTotalCoinOut(), taxInfoResponse.getCustomerTaxInfo().getTotalCoinOut());
        assertEquals(entityResponse.getTotalJackPot(), taxInfoResponse.getCustomerTaxInfo().getTotalJackPot());
        assertEquals(entityResponse.getTotalSlotWin(), taxInfoResponse.getCustomerTaxInfo().getTotalSlotWin());
        assertEquals(entityResponse.getTotalTableWin(), taxInfoResponse.getCustomerTaxInfo().getTotalTableWin());
        assertEquals(entityResponse.getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getTotalWin());

        taxInfoResponse.getCustomerTaxInfo().getSiteTotals().forEach(site -> {
            assertNotNull(site.getCoinIn());
            assertNotNull(site.getCoinOut());
            assertNotNull(site.getJackPot());
            assertNotNull(site.getSiteId());
            assertNotNull(site.getSlotWin());
            assertNotNull(site.getTableWin());
            assertNotNull(site.getTotalWin());
            assertNotNull(site.getMonthlyInfoTaxes());
        });

        assertEquals(entityResponse.getSiteTotals().get(0).getCoinIn(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinIn());

        assertEquals(entityResponse.getSiteTotals().get(0).getCoinOut(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinOut());

        assertEquals(entityResponse.getSiteTotals().get(0).getJackPot(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getJackPot());
        assertEquals(entityResponse.getSiteTotals().get(0).getSlotWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getSlotWin());
        assertEquals(entityResponse.getSiteTotals().get(0).getTableWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTableWin());
        assertEquals(entityResponse.getSiteTotals().get(0).getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTotalWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getCoinIn(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinIn());
        assertEquals(entityResponse.getSiteTotals().get(1).getCoinOut(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinOut());
        assertEquals(entityResponse.getSiteTotals().get(1).getJackPot(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getJackPot());
        assertEquals(entityResponse.getSiteTotals().get(1).getSlotWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getSlotWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getTableWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTableWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTotalWin());
    }
   
    @Test
    public void testYearlyTaxInfo() throws IOException, JAXBException, AppException {
        String mlifeNumber = "12345678";
        String year = "2019";
        
        CustomerTaxInfo entityResponse = buildCustomerTaxInfo();
        ClassLoader classLoader = getClass().getClassLoader();

        File file = new File(classLoader.getResource("data/cust_tax_info_patron_resultset.xml").getFile());
        String result = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
        final ResultSet unmarshal = JaxbContext.getInstance().unmarshal(result, ResultSet.class);

        TaxInformation information = new TaxInformation();
        information.setResult(result);
        List<TaxInformation> informations = new ArrayList<TaxInformation>();
        informations.add(information);
        when(customerTaxInfoDataAccessMock.readPatronTaxInfoCustomerResponse(anyInt(), anyInt(), anyInt())).thenReturn(informations);
        when(customerTaxInfoTransformer.toLeft(unmarshal)).thenReturn(buildCustomerTaxInfo());
        TaxInfoResponse taxInfoResponse = customerTaxInfoService.readTaxInfoCustomerResponse(mlifeNumber, year, null);
        assertNotNull(taxInfoResponse);
        assertEquals(entityResponse.getTotalCoinIn(), taxInfoResponse.getCustomerTaxInfo().getTotalCoinIn());
        assertEquals(entityResponse.getTotalCoinOut(), taxInfoResponse.getCustomerTaxInfo().getTotalCoinOut());
        assertEquals(entityResponse.getTotalJackPot(), taxInfoResponse.getCustomerTaxInfo().getTotalJackPot());
        assertEquals(entityResponse.getTotalSlotWin(), taxInfoResponse.getCustomerTaxInfo().getTotalSlotWin());
        assertEquals(entityResponse.getTotalTableWin(), taxInfoResponse.getCustomerTaxInfo().getTotalTableWin());
        assertEquals(entityResponse.getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getTotalWin());

        taxInfoResponse.getCustomerTaxInfo().getSiteTotals().forEach(site -> {
            assertNotNull(site.getCoinIn());
            assertNotNull(site.getCoinOut());
            assertNotNull(site.getJackPot());
            assertNotNull(site.getSiteId());
            assertNotNull(site.getSlotWin());
            assertNotNull(site.getTableWin());
            assertNotNull(site.getTotalWin());
            assertNotNull(site.getMonthlyInfoTaxes());
        });

        assertEquals(entityResponse.getSiteTotals().get(0).getCoinIn(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinIn());

        assertEquals(entityResponse.getSiteTotals().get(0).getCoinOut(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getCoinOut());

        assertEquals(entityResponse.getSiteTotals().get(0).getJackPot(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getJackPot());
        assertEquals(entityResponse.getSiteTotals().get(0).getSlotWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getSlotWin());
        assertEquals(entityResponse.getSiteTotals().get(0).getTableWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTableWin());
        assertEquals(entityResponse.getSiteTotals().get(0).getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(0).getTotalWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getCoinIn(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinIn());
        assertEquals(entityResponse.getSiteTotals().get(1).getCoinOut(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getCoinOut());
        assertEquals(entityResponse.getSiteTotals().get(1).getJackPot(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getJackPot());
        assertEquals(entityResponse.getSiteTotals().get(1).getSlotWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getSlotWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getTableWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTableWin());
        assertEquals(entityResponse.getSiteTotals().get(1).getTotalWin(), taxInfoResponse.getCustomerTaxInfo().getSiteTotals().get(1).getTotalWin());
    }

    @Test
    public void testCustReadTaxWithStringMlife() throws AppException {
        String mlifeNumber = "playerId";
        String year = "2019";
        String quarter = "4";
        Assertions.assertThrows(AppException.class, () -> {
            customerTaxInfoService.readTaxInfoCustomerResponse(mlifeNumber, year, quarter);
        });
    }

    @Test
    public void testCustReadTaxWithStringYear() throws AppException {
        String mlifeNumber = "12345678";
        String year = "year";
        String quarter = "4";
        Assertions.assertThrows(AppException.class, () -> {
            customerTaxInfoService.readTaxInfoCustomerResponse(mlifeNumber, year, quarter);
        });
    }

    private CustomerTaxInfo buildCustomerTaxInfo() {
        CustomerTaxInfo customerTaxInfo = new CustomerTaxInfo();
        customerTaxInfo.setCustomerId("12345678");
        customerTaxInfo.setTotalCoinIn(0.0);
        customerTaxInfo.setTotalCoinOut(0.0);
        customerTaxInfo.setTotalJackPot(0.0);
        customerTaxInfo.setTotalSlotWin(0.0);
        customerTaxInfo.setTotalTableWin(0.0);
        customerTaxInfo.setTotalWin(0.0);
        customerTaxInfo.setSiteTotals(buildSiteDetails());
        return customerTaxInfo;
    }

    private List<CustomerTaxBySite> buildSiteDetails() {
        Set<CustomerTaxBySite> customerTaxBySiteSet = new LinkedHashSet<>();
        CustomerTaxBySite customerTaxBySite = new CustomerTaxBySite();
        customerTaxBySite.setSiteId("1");
        customerTaxBySite.setCoinIn(0.0);
        customerTaxBySite.setCoinOut(0.0);
        customerTaxBySite.setJackPot(0.0);
        customerTaxBySite.setSlotWin(0.0);
        customerTaxBySite.setTableWin(0.0);
        customerTaxBySite.setTotalWin(0.0);
        customerTaxBySite.setMonthlyInfoTaxes(buildMonthSiteObject());
        customerTaxBySiteSet.add(customerTaxBySite);
        CustomerTaxBySite customerTaxBySiteOne = new CustomerTaxBySite();
        customerTaxBySiteOne.setSiteId("6");
        customerTaxBySiteOne.setCoinIn(0.0);
        customerTaxBySiteOne.setCoinOut(0.0);
        customerTaxBySiteOne.setJackPot(0.0);
        customerTaxBySiteOne.setSlotWin(0.0);
        customerTaxBySiteOne.setTableWin(0.0);
        customerTaxBySiteOne.setTotalWin(0.0);
        customerTaxBySiteOne.setMonthlyInfoTaxes(buildMonthSiteObject());
        customerTaxBySiteSet.add(customerTaxBySiteOne);
        return customerTaxBySiteSet.stream().collect(Collectors.toList());
    }

    private List<MonthlyInfoTaxes> buildMonthSiteObject() {
        List<MonthlyInfoTaxes> monthlyInfoTaxesList = new ArrayList<>();
        MonthlyInfoTaxes monthlyInfoTaxes = new MonthlyInfoTaxes();
        monthlyInfoTaxes.setMonth("2");
        monthlyInfoTaxes.setCoinIn(0.0);
        monthlyInfoTaxes.setCoinOut(0.0);
        monthlyInfoTaxes.setJackPot(0.0);
        monthlyInfoTaxes.setSlotWin(0.0);
        monthlyInfoTaxes.setTableWin(0.0);
        monthlyInfoTaxes.setTotalWin(0.0);
        monthlyInfoTaxesList.add(monthlyInfoTaxes);

        return monthlyInfoTaxesList;
    }
}
