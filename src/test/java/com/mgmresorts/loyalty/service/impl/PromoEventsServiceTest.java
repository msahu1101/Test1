package com.mgmresorts.loyalty.service.impl;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.HttpService;

class PromoEventsServiceTest {

   

    @InjectMocks
    private PromoEventsService promoEventsService;
  
    

    @Mock
    HttpService httpService;
    
    
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void testGetPromoEventBlockInfo() throws AppException, HttpFailureException {
        String xmlResponse = "<?xml version=\"1.0\"?>\r\n" + 
                "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\">\r\n" + 
                "    <Header>\r\n" + 
                "        <TimeStamp>2020-12-04T16:33:51.950</TimeStamp>\r\n" + 
                "        <Operation Data=\"PromoEventsWithFilter\" Operand=\"Information\" WhereClause=\"PromoId='360323'\" Command=\"Request\" MaxRecords=\"25\" TotalRecords=\"1\" PageNumber=\"1\" MaxPages=\"1\"/>\r\n" + 
                "    </Header>\r\n" + 
                "    <Body>\r\n" + 
                "        <PromoEvent>\r\n" + 
                "            <EventID>31586</EventID>\r\n" + 
                "            <Description>BR377913-Shimmer&amp;Shine $450</Description>\r\n" + 
                "            <Date>2020-09-03T00:00:00</Date>\r\n" + 
                "            <Status>A</Status>\r\n" + 
                "            <SiteID>4</SiteID>\r\n" + 
                "            <PromoID>360323</PromoID>\r\n" + 
                "            <NumberOfBlocks>1</NumberOfBlocks>\r\n" + 
                "        </PromoEvent>\r\n" + 
                "    </Body>\r\n" + 
                "</CRMAcresMessage>";
        when(this.httpService.post(Mockito.anyString(), Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(xmlResponse);
        Assertions.assertThrows(AppException.class, () -> promoEventsService.getPromoEventBlockInfo("456464", "promo"));
    }

}
