package com.mgmresorts.loyalty.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.mgmresorts.loyalty.dto.patron.customerpromo.CRMAcresMessage;


public class JaxbContextTest {
    @Test
    void testUnmarshal() throws Exception {
        final String string = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
                "<Resultsets>\r\n" + 
                "    <ResultSet1>\r\n" + 
                "        <Record1>\r\n" + 
                "            <XMLString>\r\n" + 
                "                <CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRM.xsd\">\r\n" + 
                "                    <Header>\r\n" + 
                "                        <OriginalMessageID>0</OriginalMessageID>\r\n" + 
                "                        <TimeStamp>2020-03-26T14:15:22.087</TimeStamp>\r\n" + 
                "                        <Operation Data=\"PlayerPromos\" Operand=\"Information\"/>\r\n" + 
                "                    </Header>\r\n" + 
                "                    <PlayerID>58286388</PlayerID>\r\n" + 
                "                    <Body>\r\n" + 
                "                        <PlayerPromos>\r\n" + 
                "                            <PromoID>368440</PromoID>\r\n" + 
                "                            <PromoName>BCH 2020-01 NG</PromoName>\r\n" + 
                "                            <PublicDescription/>\r\n" + 
                "                            <BeginDate>2020-01-02T12:06:00</BeginDate>\r\n" + 
                "                            <EndDate>2020-07-31T12:06:00</EndDate>\r\n" + 
                "                            <Status>Mailer Sent</Status>\r\n" + 
                "                            <SiteID>6</SiteID>\r\n" + 
                "                            <SiteDescription>Bellagio</SiteDescription>\r\n" + 
                "                        </PlayerPromos>\r\n" + 
                "                        <PlayerPromos>\r\n" + 
                "                            <PromoID>372700</PromoID>\r\n" + 
                "                            <PromoName>CORP 2020-03 NG – V3</PromoName>\r\n" + 
                "                            <PublicDescription>NULL</PublicDescription>\r\n" + 
                "                            <BeginDate>2020-03-02T15:53:00</BeginDate>\r\n" + 
                "                            <EndDate>2020-09-30T15:53:00</EndDate>\r\n" + 
                "                            <Status>Mailer Sent</Status>\r\n" + 
                "                            <SiteID>1</SiteID>\r\n" + 
                "                            <SiteDescription>Mirage</SiteDescription>\r\n" + 
                "                        </PlayerPromos>\r\n" + 
                "                    </Body>\r\n" + 
                "                </CRMAcresMessage>\r\n" + 
                "            </XMLString>\r\n" + 
                "        </Record1>\r\n" + 
                "    </ResultSet1>\r\n" + 
                "</Resultsets>";
        final CRMAcresMessage unmarshal = JaxbContext.getInstance().unmarshal(string, CRMAcresMessage.class);
        assertNotNull(unmarshal);

    }

}
