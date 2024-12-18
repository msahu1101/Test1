package com.mgmresorts.loyalty.service.impl;

import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.http.HttpFailureException;
import com.mgmresorts.common.http.HttpService;
import com.mgmresorts.loyalty.data.impl.StopCodeAccess;
import com.mgmresorts.loyalty.data.to.StopCodeWrapper;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketRequest;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketResponse;
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;
import com.mgmresorts.loyalty.service.IService;

class PromoEventTicketIssuanceServiceTest {

    
    @Mock
    private StopCodeAccess dataAccessMock;

    @InjectMocks
    private PromoEventTicketIssuanceService promoEventTicketService;

    @Mock
    Thread threadMock;

    @Mock
    private ICache<StopCodeWrapper> mockCache;

    @Mock
    StopCodeResponse stopCodeResponseMock;

    @Mock
    IService iServiceMock;

    @Mock
    HttpService httpService;
    
    
    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    void testIssueEventPromo() throws AppException, HttpFailureException {
        String xmlResponse = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\"><Header><OriginalMessageID>0</OriginalMessageID><TimeStamp>2020-10-08T06:59:46</TimeStamp><Operation Data=\"General\" Operand=\"Success\"/></Header></CRMAcresMessage>";
        when(this.httpService.post(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(xmlResponse);
        IssuePromoEventTicketResponse issueEventPromo = promoEventTicketService.issueEventPromo(issuePromeRequest());
        Assertions.assertEquals("Success", issueEventPromo.getResult().getStatus());
    }
    
    
    @Test
    void testIssueEventPromoNotFound() throws AppException, HttpFailureException {
        String xmlResponse = "<CRMAcresMessage xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://localhost/CRMAcres.xsd\">\r\n" + 
                "    <Header>\r\n" + 
                "        <TimeStamp>2020-12-13T15:50:42.497</TimeStamp>\r\n" + 
                "        <Operation Data=\"Error\" Operand=\"Error\"/>\r\n" + 
                "    </Header>\r\n" + 
                "    <Body>\r\n" + 
                "        <Error>\r\n" + 
                "            <ErrorCode>INVALIDTICKETCNT</ErrorCode>\r\n" + 
                "            <ErrorDescription>Proc_XML_CRM_PromoEventTicketIssuance: There is a maximum of 2 tickets available for the block you have selected. Please select an alternate block or adjust the number of tickets.</ErrorDescription>\r\n" + 
                "        </Error>\r\n" + 
                "    </Body>\r\n" + 
                "</CRMAcresMessage>";
        when(this.httpService.post(Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(xmlResponse);
        Assertions.assertThrows(AppException.class, () -> promoEventTicketService.issueEventPromo(issuePromeRequest()));
    }

    private IssuePromoEventTicketRequest issuePromeRequest() {
        IssuePromoEventTicketRequest updateRequest = new IssuePromoEventTicketRequest();
        updateRequest.setBlockId(43);
        updateRequest.setPickedUp("test");
        updateRequest.setPlayerId(34);
        updateRequest.setPromoId(67);
        updateRequest.setTicketCount(34);
        return updateRequest;
    }
}
