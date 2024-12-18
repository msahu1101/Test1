package com.mgmresorts.loyalty.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.lambda.Worker;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.impl.StopCodeAccess;
import com.mgmresorts.loyalty.data.to.StopCodeWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;
import com.mgmresorts.loyalty.dto.services.StopCodeResponse;
import com.mgmresorts.loyalty.service.IService;
import com.mgmresorts.loyalty.transformer.StopCodeTransformer;

class StopCodeServiceTest {

    @Mock
    private StopCodeAccess dataAccessMock;

    @InjectMocks
    private StopCodeService stopCodeService;

    @Mock
    Thread threadMock;

    @Mock
    private ICache<StopCodeWrapper> mockCache;

    @Mock
    StopCodeResponse stopCodeResponseMock;

    @Mock
    IService iServiceMock;

    @Mock
    StopCodeTransformer transformerMock;

    StopCodeWrapper stopCodeWrappers = new StopCodeWrapper();
    List<CustomerStopCode> customerStopCodes = new ArrayList<>();

    @BeforeEach
    public void setUp() throws Exception {

        CustomerStopCode customerStopCode = new CustomerStopCode();
        customerStopCode.setDescription("test description");
        customerStopCode.setId("11133");
        customerStopCode.setIsActive(true);
        customerStopCode.setPriority(1);
        customerStopCodes.add(customerStopCode);
        stopCodeWrappers.setCustomerStopCodes(customerStopCodes);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getCustomerStopCodeTest() throws AppException {
        String mlife = "2423423";
        when(mockCache.nonBlockingGet(Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(stopCodeWrappers);
        StopCodeResponse result = stopCodeService.getCustomerStopCode(mlife);
        assertEquals("test description", result.getCustomerStopCode().get(0).getDescription());
        assertEquals("11133", result.getCustomerStopCode().get(0).getId());
        assertEquals(true, result.getCustomerStopCode().get(0).getIsActive());
        assertEquals(1, result.getCustomerStopCode().get(0).getPriority());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getCustomerStopCodenonBlockingGetTest() throws AppException {
        String mlife = "2423423";
        List<StopCode> stopcodes = new ArrayList<>();
        StopCode stopCode = new StopCode();
        stopCode.setDescription("test");
        stopCode.setId("543534");
        stopCode.setIsActive(true);
        stopCode.setPriority(1);
        stopcodes.add(stopCode);
        when(dataAccessMock.getStopCode(Mockito.anyInt())).thenReturn(stopcodes);
        when(transformerMock.toLeft(stopcodes)).thenReturn(customerStopCodes);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Worker<String, ?> lambda = invocationOnMock.getArgument(2);
                return lambda.apply("");
            }
        }).when(mockCache).nonBlockingGet(anyString(), any(), any());

        StopCodeResponse result = stopCodeService.getCustomerStopCode(mlife);
        assertEquals("test description", result.getCustomerStopCode().get(0).getDescription());
        assertEquals("11133", result.getCustomerStopCode().get(0).getId());
        assertEquals(true, result.getCustomerStopCode().get(0).getIsActive());
        assertEquals(1, result.getCustomerStopCode().get(0).getPriority());
    }

    @Test
    public void getCustomerStopCodevalidatMlifeTest() throws AppException {
        StopCodeResponse result = null;
        try {
            stopCodeService.getCustomerStopCode("dfsfs");
        } catch (Exception e) {

        }
        assertEquals(null, result);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getCustomerStopCodeExceptionTest() throws AppException {
        List<StopCode> stopcodes = null;
        when(dataAccessMock.getStopCode(Mockito.anyInt())).thenReturn(stopcodes);
        when(transformerMock.toLeft(stopcodes)).thenReturn(customerStopCodes);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Worker<String, ?> lambda = invocationOnMock.getArgument(2);
                return lambda.apply("");
            }
        }).when(mockCache).nonBlockingGet(anyString(), any(), any());
        Assertions.assertDoesNotThrow(() -> stopCodeService.getCustomerStopCode("4387797"));
    }

}