package com.mgmresorts.loyalty.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;

class StopCodeTransformerTest {

	@InjectMocks
	private StopCodeTransformer stopCodeTransformer;

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testToLeft() {
		List<StopCode> stopCodes = new ArrayList<>();
		StopCode stopCode = new StopCode();
		stopCode.setDescription("test");
		stopCode.setInformation("test");
		stopCode.setId("234234");
		stopCode.setIsActive(true);
		stopCode.setPriority(1);
		stopCodes.add(stopCode);
		List<CustomerStopCode> custStopCodes = stopCodeTransformer.toLeft(stopCodes);
		assertEquals("test"+". "+"test", custStopCodes.get(0).getDescription());
		assertEquals("234234", custStopCodes.get(0).getId());
		assertEquals(true, custStopCodes.get(0).getIsActive());
		assertEquals(1, custStopCodes.get(0).getPriority());

	}

	@Test
	void testToRight() {
		List<CustomerStopCode> left = new ArrayList<>();
		List<StopCode> result = null;
		try {
			result = stopCodeTransformer.toRight(left);
		} catch (Exception exp) {

		}
		assertNull(result);
	}
}
