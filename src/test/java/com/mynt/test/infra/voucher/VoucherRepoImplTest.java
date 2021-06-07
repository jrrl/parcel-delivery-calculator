package com.mynt.test.infra.voucher;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mynt.test.core.DependencyErrorException;
import com.mynt.test.core.costcalculator.DeliveryCostCalculatorServiceImpl;
import com.mynt.test.core.voucher.Voucher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ContextConfiguration(initializers = WireMockConfig.class)
@Import({VoucherRepoImpl.class, DeliveryCostCalculatorServiceImpl.class})
public class VoucherRepoImplTest {
	private final WireMockServer wireMock;
	private final VoucherRepoImpl voucherRepo;
	private final VoucherProperties properties;

	public VoucherRepoImplTest(@Autowired WireMockServer wireMock,
	                           @Autowired VoucherRepoImpl voucherRepo,
	                           @Autowired VoucherProperties properties) {
		this.wireMock = wireMock;
		this.voucherRepo = voucherRepo;
		this.properties = properties;
	}

	@Test
	void getVoucherDetails_success() {
		wireMock.stubFor(
			get(urlPathEqualTo(properties.getBasePath() + "/testvoucher"))
				.withQueryParam("key", equalTo(properties.getApikey()))
				.willReturn(aResponse()
					.withStatus(200)
					.withHeader("Content-Type", "application/json")
					.withBody("{\"code\":\"MYNT\",\"discount\":12.25,\"expiry\":\"2020-09-16\"}")
				)
		);

		Voucher voucher = voucherRepo.getVoucherDetails("testvoucher");
		assertEquals(BigDecimal.valueOf(12.25), voucher.getDiscount());
		assertEquals("2020-09-16", voucher.getExpiry().toString());
		assertNull(voucher.getErrorMessage());
	}

	@Test
	void getVoucherDetails_voucherNotFound() {
		wireMock.stubFor(
			get(urlPathEqualTo(properties.getBasePath() + "/testvoucher"))
				.withQueryParam("key", equalTo(properties.getApikey()))
				.willReturn(aResponse()
					.withStatus(400)
					.withHeader("Content-Type", "application/json")
					.withBody("{\"error\":\"Invalid code\"}")
				)
		);

		Voucher voucher = voucherRepo.getVoucherDetails("testvoucher");
		assertEquals(BigDecimal.ZERO, voucher.getDiscount());
		assertNull(voucher.getExpiry());
		assertEquals("Invalid code", voucher.getErrorMessage());
	}

	@Test
	void getVoucherDetails_connectionError() {
		wireMock.stubFor(
			get(urlPathEqualTo(properties.getBasePath() + "/testvoucher"))
				.withQueryParam("key", equalTo(properties.getApikey()))
				.willReturn(aResponse()
					.withStatus(500)
				)
		);

		assertThrows(DependencyErrorException.class, () -> voucherRepo.getVoucherDetails("testvoucher"));
	}
}
