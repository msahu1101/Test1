package com.mgmresorts.loyalty.service.impl;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.dto.OutHeader;
import com.mgmresorts.common.dto.Status;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.data.ISlotDollarBalanceAccess;
import com.mgmresorts.loyalty.dto.customer.GetSlotDollarBalanceResponse;
import com.mgmresorts.loyalty.dto.customer.SlotDollarBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class SlotDollarBalanceServiceTest {

    private final static String defaultPlayerId = "12345";
    private final static int defaultSlotDollars = 888;

    @InjectMocks
    @Spy
    private SlotDollarBalanceService slotDollarBalanceService;

    @Mock
    private ISlotDollarBalanceAccess slotDollarBalanceAccess;

    @Mock
    private ICache<com.mgmresorts.loyalty.data.entity.SlotDollarBalance> caches;


    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void getSlotDollarBalance_happyPath_returnsResponse() throws AppException {
        doReturn(getSlotDollarBalanceEntity()).when(slotDollarBalanceService).callSprocWithCircuitBreaker(anyString());
        GetSlotDollarBalanceResponse response = slotDollarBalanceService.getSlotDollarBalance(defaultPlayerId);

        verifyResponse(response, defaultPlayerId, defaultSlotDollars);
    }

    @Test
    void buildResponse_happyPath_returnsResponse() {
        com.mgmresorts.loyalty.data.entity.SlotDollarBalance entity = new com.mgmresorts.loyalty.data.entity.SlotDollarBalance()
                .withPlayerId("abcde")
                .withSlotDollars(-12345);
        GetSlotDollarBalanceResponse response = slotDollarBalanceService.buildResponse(entity);

        verifyResponse(response, "abcde", -12345);
    }

    protected void verifyResponse(GetSlotDollarBalanceResponse response, String expectedPlayerId, int expectedSlotDollars) {
        assertNotNull(response, "Response should not be null");

        SlotDollarBalance dto = response.getSlotDollarBalance();
        assertNotNull(dto, "SlotDollarBalance dto should not be null");
        assertEquals(expectedPlayerId, dto.getPlayerId(), "PlayerId's should match");
        assertEquals(expectedSlotDollars, dto.getSlotDollars(), "SlotDollar's should match");

        OutHeader header = response.getHeader();
        assertNotNull(header, "OutHeader should not be null");
        assertEquals(Status.Code.SUCCESS, header.getStatus().getCode(), "StatusCode should match");
        assertEquals("API", header.getOrigin(), "Origin should match");
        assertEquals(0, header.getStatus().getMessages().size(), "Status Messages should be empty");
    }

    private GetSlotDollarBalanceResponse getDefaultSlotDollarBalanceResponse() {
        return new GetSlotDollarBalanceResponse()
            .withSlotDollarBalance(getSlotDollarBalanceDto());
    }

    protected SlotDollarBalance getSlotDollarBalanceDto() {
        return getSlotDollarBalanceDto(defaultPlayerId, defaultSlotDollars);
    }

    protected SlotDollarBalance getSlotDollarBalanceDto(String playerId, int slotDollars) {
        return new SlotDollarBalance()
            .withPlayerId(playerId)
            .withSlotDollars(slotDollars);
    }

    protected com.mgmresorts.loyalty.data.entity.SlotDollarBalance getSlotDollarBalanceEntity() {
        return getSlotDollarBalanceEntity(defaultPlayerId, defaultSlotDollars);
    }

    protected com.mgmresorts.loyalty.data.entity.SlotDollarBalance getSlotDollarBalanceEntity(String playerId, int slotDollars) {
        return new com.mgmresorts.loyalty.data.entity.SlotDollarBalance()
            .withPlayerId(playerId)
            .withSlotDollars(slotDollars);
    }
}
