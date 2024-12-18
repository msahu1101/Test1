package com.mgmresorts.loyalty.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.lambda.Worker;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.IPromotionsAccess;
import com.mgmresorts.loyalty.data.entity.Offers;
import com.mgmresorts.loyalty.dto.customer.CustomerPromotion;
import com.mgmresorts.loyalty.dto.patron.customerpromo.CRMAcresMessage;
import com.mgmresorts.loyalty.dto.services.PromotionsResponse;
import com.mgmresorts.loyalty.service.IService;
import com.mgmresorts.loyalty.transformer.ITransformer;

class PromotionsServiceTest {

    final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    @InjectMocks
    private PromotionsService promotionsService;

    @Mock
    private ICache<String> mockCache;

    @Mock
    private ITransformer<List<CustomerPromotion>, CRMAcresMessage> iTransformer;

    @Mock
    private IService iServiceMock;

    @Mock
    private IPromotionsAccess customerPromotionsAccess;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetCustomerOffers() throws AppException, IOException, ParseException {
        List<Offers> customerOffers = new ArrayList<>();
        Offers customerOffer = new Offers();

        List<CustomerPromotion> customerPromotions = new ArrayList<CustomerPromotion>();
        CustomerPromotion customerPromotion = new CustomerPromotion();
        customerPromotion.setPublicDescription("test");
        customerPromotion.setEndDate(ZonedDateTime.parse("2020-01-03T11:15:00.000-07:00", FORMAT));
        customerPromotion.setStartDate(ZonedDateTime.parse("2020-01-03T11:15:00.000-07:00", FORMAT));
        customerPromotion.setName("test");
        customerPromotion.setPromoId("2324");
        customerPromotions.add(customerPromotion);
        String custPromoResult = Utils.readFileFromClassPath("data/player_promotions.xml");

        customerOffer.setResult(custPromoResult);
        customerOffers.add(customerOffer);
        JaxbContext.getInstance().unmarshal(custPromoResult, CRMAcresMessage.class);

        when(customerPromotionsAccess.readPatronCustPromotions(Mockito.anyInt())).thenReturn(customerOffers);
        Mockito.doAnswer(invocation -> {
            final Worker<String, ?> lambda = invocation.getArgument(2);
            return lambda.apply("");

        }).when(mockCache).nonBlockingGet(anyString(), any(), any());

        when(iTransformer.toLeft(any())).thenReturn(customerPromotions);
        PromotionsResponse result = promotionsService.getCustomerOffers("544545");
        assertEquals(result.getCustomerPromotions().get(0).getPublicDescription(), "test");
        assertEquals(result.getCustomerPromotions().get(0).getEndDate(), ZonedDateTime.from(FORMAT.parse("2020-01-03T11:15:00.000-07:00")));
        assertEquals(result.getCustomerPromotions().get(0).getStartDate(), ZonedDateTime.from(FORMAT.parse("2020-01-03T11:15:00.000-07:00")));
        assertEquals(result.getCustomerPromotions().get(0).getName(), "test");
        assertEquals(result.getCustomerPromotions().get(0).getPromoId(), "2324");
    }

    @Test
    void testGetCustomerOffersValidateMlife() throws AppException, IOException {
        Assertions.assertThrows(AppException.class, () -> promotionsService.getCustomerOffers("sd"));
    }

    @Test
    void testGetCustomerOffersValidateTransformer() throws AppException, IOException {
        List<CustomerPromotion> customerPromotions = new ArrayList<CustomerPromotion>();
        String custPromoResult = Utils.readFileFromClassPath("data/player_promo_validate.xml");
        Mockito.doAnswer(invocation -> {
            return custPromoResult;
        }).when(mockCache).nonBlockingGet(anyString(), any(), any());
        JaxbContext.getInstance().unmarshal(custPromoResult, CRMAcresMessage.class);

        when(iTransformer.toLeft(any())).thenReturn(customerPromotions);

        Assertions.assertThrows(AppException.class, () -> promotionsService.getCustomerOffers("3231"));
    }

}
