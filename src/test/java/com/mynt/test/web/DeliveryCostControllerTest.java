package com.mynt.test.web;

import com.mynt.test.core.costcalculator.CostDetails;
import com.mynt.test.core.costcalculator.DeliveryCostCalculatorService;
import com.mynt.test.core.parcel.InvalidParcelException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class DeliveryCostControllerTest {
	@MockBean
	private DeliveryCostCalculatorService costCalculatorService;

	@Autowired
	private MockMvc mockMvc;

	private static final String path = "/delivery/cost";

	@Test
	public void getDeliveryCost_success() throws Exception{
		CostDetails costDetails = CostDetails.builder()
			.calculatedCost(BigDecimal.TEN)
			.build();
		when(costCalculatorService.calculateCost(any(), any())).thenReturn(costDetails);

		mockMvc.perform(
			get(path)
				.queryParam("weight", "5")
				.queryParam("height", "10")
				.queryParam("length", "10")
				.queryParam("width", "10")
		)
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(content().json("{\"calculatedCost\":10,\"totalCost\":10,\"voucher\":null}"));
	}

	@Test
	public void getDeliveryCost_invalidParcel_error() throws Exception{
		when(costCalculatorService.calculateCost(any(), any())).thenThrow(new InvalidParcelException("Invalid parcel"));

		mockMvc.perform(
			get(path)
				.queryParam("weight", "5")
				.queryParam("height", "10")
				.queryParam("length", "10")
				.queryParam("width", "10")
		)
		.andExpect(status().isBadRequest())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(content().json("{\"error\":\"Invalid parcel\"}"));
	}
}
