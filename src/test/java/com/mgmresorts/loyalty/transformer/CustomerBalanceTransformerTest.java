package com.mgmresorts.loyalty.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.xml.sax.SAXException;

import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.ErrorManager;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.dto.customer.SiteTypeCommon;
import com.mgmresorts.loyalty.dto.customer.XtraCreditBalancesLocal;
import com.mgmresorts.loyalty.errors.Errors;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CustomerBalanceTransformerTest {
    @Mock
    private final JaxbContext jaxbContext = Mockito.mock(JaxbContext.class);

    @InjectMocks
    private final BalanceTransformer customerBalanceTransformer = new BalanceTransformer();

    private static String crmXml;

    private static Balance positiveCrm = new Balance();
    private static Balance invalidCrm = new Balance();
    private static Balance otherCrm = new Balance();
    private static Balance xtraCredit = new Balance();
    private static Balance theoValues = new Balance();

    private static final BalanceWrapper correctWrapper = new BalanceWrapper();

    @BeforeAll
    public static void setUp() {
        Runtime.get().refresh();

        String invalidMlifeXml = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\"><Header><OriginalMessageID>0</OriginalMessageID><TimeStamp>2019-09-16T03:04:32.590</TimeStamp><Operation Data=\"Error\" Operand=\"Error\"/></Header><Body><Error><ErrorCode>INVALIDPLAYERID</ErrorCode><ErrorDescription>Proc_XML_CRM_PlayerBalances: PlayerID / CardID is Invalid or not active</ErrorDescription></Error></Body></CRMAcresMessage>";

        crmXml = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRM.xsd\"><Header><OriginalMessageID>0</OriginalMessageID><TimeStamp>2019-09-16T01:05:49.440</TimeStamp><Operation Data=\"PlayerBalances\" Operand=\"Information\"/></Header><PlayerID>1075711</PlayerID><Site><SiteID>1</SiteID><SiteDescription>Mirage</SiteDescription></Site><Body><PlayerBalances><RewardBalance>0</RewardBalance><CompBalance>-335.7858</CompBalance><SecondCompBalance>0.00000000</SecondCompBalance><GiftPoints>0.00000000</GiftPoints><PointBalance>0</PointBalance><CompBalanceLinked>-335.7858</CompBalanceLinked><SecondCompBalanceLinked>0.00000000</SecondCompBalanceLinked><GiftPointsLinked>0.00000000</GiftPointsLinked><PointBalanceLinked>0</PointBalanceLinked></PlayerBalances></Body></CRMAcresMessage>";

        String xtraCreditXml = "<PlayerXtraCreditBalances><GlobalBalance>0.0000</GlobalBalance><LocalBalances><LocalBalance><SiteID>1</SiteID><Balance>0.0000</Balance></LocalBalance><LocalBalance><SiteID>3</SiteID><Balance>0.0000</Balance></LocalBalance></LocalBalances></PlayerXtraCreditBalances>";

        String theoXml = "<TheoValues><ID>5</ID><SiteID>6</SiteID><TotalTheoWin>0.0000</TotalTheoWin><RoomNights>0</RoomNights><LastStayDate>1900-01-01T00:00:00</LastStayDate><SitemasterID>6</SitemasterID><SiteName>Bellagio</SiteName><SiteIDPM>6</SiteIDPM><ConfigHotelSystemID>1</ConfigHotelSystemID><CorpSortOrder>1</CorpSortOrder></TheoValues><TheoValues><ID>12</ID><SiteID>16</SiteID><TotalTheoWin>0.0000</TotalTheoWin><RoomNights>0</RoomNights><LastStayDate>1900-01-01T00:00:00</LastStayDate><SitemasterID>16</SitemasterID><SiteName>ARIA</SiteName><SiteIDPM>16</SiteIDPM><ConfigHotelSystemID>1</ConfigHotelSystemID><CorpSortOrder>2</CorpSortOrder></TheoValues>";

        String otherErrorXml = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\"><Header><OriginalMessageID>0</OriginalMessageID><TimeStamp>2019-09-16T03:04:32.590</TimeStamp><Operation Data=\"Error\" Operand=\"Error\"/></Header><Body><Error><ErrorCode>OTHER</ErrorCode><ErrorDescription>Proc_XML_CRM_PlayerBalances: PlayerID / CardID is Invalid or not active</ErrorDescription></Error></Body></CRMAcresMessage>";

        positiveCrm.setXml(crmXml);
        invalidCrm.setXml(invalidMlifeXml);
        otherCrm.setXml(otherErrorXml);
        theoValues.setXml(theoXml);
        xtraCredit.setXml(xtraCreditXml);

        correctWrapper.getBalance().setRewardBalance(0);
        correctWrapper.getBalance().setCompBalance(-335.7858);
        correctWrapper.getBalance().setSecondCompBalance(0.0);
        correctWrapper.getBalance().setGiftPoints(0.0);
        correctWrapper.getBalance().setXtraCreditBalanceLocal(0.0);
        correctWrapper.getBalance().setXtraCreditBalanceGlobal(0.0);
        correctWrapper.getBalance().setPointBalance(0);
        correctWrapper.getBalance().setCompBalanceLinked(-335.7858);
        correctWrapper.getBalance().setSecondCompBalanceLinked(0.0);
        correctWrapper.getBalance().setPointBalanceLinked(0);

        XtraCreditBalancesLocal xtraCreditBalancesLocal = new XtraCreditBalancesLocal();
        xtraCreditBalancesLocal.setSiteId("1");
        xtraCreditBalancesLocal.setXtraCreditBalanceLocal(0.0);
        correctWrapper.getBalance().getXtraCreditBalancesLocal().add(xtraCreditBalancesLocal);
        xtraCreditBalancesLocal = new XtraCreditBalancesLocal();
        xtraCreditBalancesLocal.setSiteId("3");
        xtraCreditBalancesLocal.setXtraCreditBalanceLocal(0.0);
        correctWrapper.getBalance().getXtraCreditBalancesLocal().add(xtraCreditBalancesLocal);

        SiteTypeCommon siteType = new SiteTypeCommon();
        siteType.setConfigHotelSystemId(1);
        siteType.setCorpSortOrder(1);
        siteType.setId(5);
        siteType.setRoomNights(0);
        siteType.setSiteId("6");
        siteType.setSiteIdPm(6);
        siteType.setSiteName("Bellagio");
        siteType.setSitemasterId(6);
        siteType.setTotalTheoWin(0.0);
        correctWrapper.getBalance().getSites().getSiteInfo().add(siteType);
        siteType = new SiteTypeCommon();
        siteType.setConfigHotelSystemId(1);
        siteType.setCorpSortOrder(2);
        siteType.setId(12);
        siteType.setRoomNights(0);
        siteType.setSiteId("16");
        siteType.setSiteIdPm(16);
        siteType.setSiteName("ARIA");
        siteType.setSitemasterId(16);
        siteType.setTotalTheoWin(0.0);
        correctWrapper.getBalance().getSites().getSiteInfo().add(siteType);
    }

    @BeforeEach
    public void cleanErrors() {
        ErrorManager.clean();
        new Errors();

        MockitoAnnotations.initMocks(this);
    }

    public void customerBalanceTransformerCommonTester(List entities) throws AppException {
        BalanceWrapper wrapper = customerBalanceTransformer.toLeft(entities);
        assertEquals(correctWrapper.getBalance().getRewardBalance(), wrapper.getBalance().getRewardBalance());
        assertEquals(correctWrapper.getBalance().getCompBalance(), wrapper.getBalance().getCompBalance());
        assertEquals(correctWrapper.getBalance().getSecondCompBalance(), wrapper.getBalance().getSecondCompBalance());
        assertEquals(correctWrapper.getBalance().getGiftPoints(), wrapper.getBalance().getGiftPoints());
        assertEquals(correctWrapper.getBalance().getXtraCreditBalanceLocal(), wrapper.getBalance().getXtraCreditBalanceLocal());
        assertEquals(correctWrapper.getBalance().getXtraCreditBalanceGlobal(), wrapper.getBalance().getXtraCreditBalanceGlobal());
        assertEquals(correctWrapper.getBalance().getPointBalance(), wrapper.getBalance().getPointBalance());
        assertEquals(correctWrapper.getBalance().getCompBalanceLinked(), wrapper.getBalance().getCompBalanceLinked());
        assertEquals(correctWrapper.getBalance().getSecondCompBalanceLinked(), wrapper.getBalance().getSecondCompBalanceLinked());
        assertEquals(correctWrapper.getBalance().getPointBalanceLinked(), wrapper.getBalance().getPointBalanceLinked());

        for (int i = 0; i < 2; i++) {
            XtraCreditBalancesLocal correctXtraCreditBalancesLocal = correctWrapper.getBalance().getXtraCreditBalancesLocal().get(i);
            XtraCreditBalancesLocal xtraCreditBalancesLocal = wrapper.getBalance().getXtraCreditBalancesLocal().get(i);
            assertEquals(correctXtraCreditBalancesLocal.getSiteId(), xtraCreditBalancesLocal.getSiteId());
            assertEquals(correctXtraCreditBalancesLocal.getXtraCreditBalanceLocal(), xtraCreditBalancesLocal.getXtraCreditBalanceLocal());
        }

        for (int i = 0; i < 2; i++) {
            SiteTypeCommon correctSiteType = correctWrapper.getBalance().getSites().getSiteInfo().get(i);
            SiteTypeCommon siteType = wrapper.getBalance().getSites().getSiteInfo().get(i);
            assertEquals(correctSiteType.getConfigHotelSystemId(), siteType.getConfigHotelSystemId());
            assertEquals(correctSiteType.getCorpSortOrder(), siteType.getCorpSortOrder());
            assertEquals(correctSiteType.getId(), siteType.getId());
            assertEquals(correctSiteType.getId(), siteType.getId());
            assertEquals(correctSiteType.getRoomNights(), siteType.getRoomNights());
            assertEquals(correctSiteType.getSiteId(), siteType.getSiteId());
            assertEquals(correctSiteType.getSiteIdPm(), siteType.getSiteIdPm());
            assertEquals(correctSiteType.getSiteName(), siteType.getSiteName());
            assertEquals(correctSiteType.getSitemasterId(), siteType.getSitemasterId());
            assertEquals(correctSiteType.getTotalTheoWin(), siteType.getTotalTheoWin());
        }
    }
    @Test
    public void toLeftInvalidMlifeTest() throws AppException {
        List<Balance> entities;
        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(JaxbContext.getInstance().getBalanceUnmarshaller());

        entities = new ArrayList<Balance>();
        entities.add(invalidCrm);
        entities.add(theoValues);
        entities.add(xtraCredit);
        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.INVALID_PATRON_ID, e.getCode());
            return;
        }
        assertEquals("App Exception", "No Exception");
    }

    @Test
    public void toLeftOtherCrmExceptionTest() throws AppException {
        List<Balance> entities;
        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(JaxbContext.getInstance().getBalanceUnmarshaller());

        entities = new ArrayList<Balance>();
        entities.add(otherCrm);
        entities.add(theoValues);
        entities.add(xtraCredit);
        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.UNEXPECTED_SYSTEM, e.getCode());
            return;
        }
        assertEquals("App Exception", "No Exception");
    }

    @Test
    public void toLeftMissingCrmExceptionTest() throws AppException {
        List<Balance> entities;
        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(JaxbContext.getInstance().getBalanceUnmarshaller());

        entities = new ArrayList<Balance>();
        entities.add(theoValues);
        entities.add(xtraCredit);
        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.INVALID_DATA, e.getCode());
            return;
        }
        assertEquals("App Exception", "No Exception");
    }
    @Test
    public void toLeftUnmarshallExceptionTest() throws Exception {
        Unmarshaller unmarshaller = Mockito.mock(Unmarshaller.class);
        Schema schema = Mockito.mock(Schema.class);
        Validator validator = Mockito.mock(Validator.class);
        List<Balance> entities;

        entities = new ArrayList<Balance>();
        entities.add(invalidCrm);
        entities.add(theoValues);
        entities.add(xtraCredit);

        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.getSchema()).thenReturn(schema);
        when(schema.newValidator()).thenReturn(validator);
        when(unmarshaller.unmarshal(any(StringReader.class))).thenThrow(JAXBException.class);

        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.INVALID_DATA, e.getCode());
            assertEquals(JAXBException.class, e.getCause().getClass());
            return;
        }
        assertEquals("App Exception", "No Exception");
    }

    @Test
    public void toLeftNullListTest() throws AppException {
        BalanceWrapper wrapper = customerBalanceTransformer.toLeft(null);
        assertEquals(null, wrapper.getBalance().getRewardBalance());
        assertEquals(null, wrapper.getBalance().getCompBalance());
        assertEquals(null, wrapper.getBalance().getSecondCompBalance());
        assertEquals(null, wrapper.getBalance().getGiftPoints());
        assertEquals(null, wrapper.getBalance().getXtraCreditBalanceLocal());
        assertEquals(null, wrapper.getBalance().getXtraCreditBalanceGlobal());
        assertEquals(null, wrapper.getBalance().getPointBalance());
        assertEquals(null, wrapper.getBalance().getCompBalanceLinked());
        assertEquals(null, wrapper.getBalance().getSecondCompBalanceLinked());
        assertEquals(null, wrapper.getBalance().getPointBalanceLinked());
    }

    @Test
    public void toLeftEmptyListTest() throws AppException {
        BalanceWrapper wrapper = customerBalanceTransformer.toLeft(new ArrayList<>());
        assertEquals(null, wrapper.getBalance().getRewardBalance());
        assertEquals(null, wrapper.getBalance().getCompBalance());
        assertEquals(null, wrapper.getBalance().getSecondCompBalance());
        assertEquals(null, wrapper.getBalance().getGiftPoints());
        assertEquals(null, wrapper.getBalance().getXtraCreditBalanceLocal());
        assertEquals(null, wrapper.getBalance().getXtraCreditBalanceGlobal());
        assertEquals(null, wrapper.getBalance().getPointBalance());
        assertEquals(null, wrapper.getBalance().getCompBalanceLinked());
        assertEquals(null, wrapper.getBalance().getSecondCompBalanceLinked());
        assertEquals(null, wrapper.getBalance().getPointBalanceLinked());
    }

    @Test
    public void validationSaxExceptionTest() throws Exception {
        Runtime.get().getConfigurations().put("customer.balance.xml.validation", "true");
        assertEquals("true", Runtime.get().getConfiguration("customer.balance.xml.validation"));

        Unmarshaller unmarshaller = Mockito.spy(Unmarshaller.class);
        Schema schema = Mockito.mock(Schema.class);
        Validator validator = Mockito.mock(Validator.class);

        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(unmarshaller);
        doNothing().when(unmarshaller).setSchema(any());
        when(unmarshaller.getSchema()).thenReturn(schema);
        when(schema.newValidator()).thenReturn(validator);
        doThrow(SAXException.class).when(validator).validate(any());
        List<Balance> entities;
        entities = new ArrayList<Balance>();
        entities.add(positiveCrm);
        entities.add(theoValues);
        entities.add(xtraCredit);

        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.INVALID_DATA, e.getCode());
            assertEquals(SAXException.class, e.getCause().getClass());
            return;
        }
        assertEquals("App Exception", "No exception");
    }

    @Test
    public void validationIoExceptionTest() throws Exception {
        Runtime.get().getConfigurations().put("customer.balance.xml.validation", "true");
        assertEquals("true", Runtime.get().getConfiguration("customer.balance.xml.validation"));

        Unmarshaller unmarshaller = Mockito.spy(Unmarshaller.class);
        Schema schema = Mockito.mock(Schema.class);
        Validator validator = Mockito.mock(Validator.class);

        when(jaxbContext.getBalanceUnmarshaller()).thenReturn(unmarshaller);
        doNothing().when(unmarshaller).setSchema(any());
        when(unmarshaller.getSchema()).thenReturn(schema);
        when(schema.newValidator()).thenReturn(validator);
        doThrow(IOException.class).when(validator).validate(any());
        List<Balance> entities;
        entities = new ArrayList<Balance>();
        entities.add(positiveCrm);
        entities.add(theoValues);
        entities.add(xtraCredit);

        try {
            customerBalanceTransformerCommonTester(entities);
        } catch (AppException e) {
            assertEquals(Errors.UNEXPECTED_SYSTEM, e.getCode());
            assertEquals(IOException.class, e.getCause().getClass());
            return;
        }
        assertEquals("App Exception", "No exception");
    }
    @Test
    public void getLeftTypeTest() {
        assertEquals(BalanceWrapper.class, customerBalanceTransformer.getLeftType());
    }

    @Test
    public void toRightTest() {
        assertEquals(null, customerBalanceTransformer.toRight(null));
    }

}