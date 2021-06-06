package com.mynt.test.infra.voucher;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
import java.time.LocalDate;

public class VoucherResponse {
	private final String code;
	private final LocalDate expiry;
	private final BigDecimal discount;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public VoucherResponse(String code, BigDecimal discount, LocalDate expiry) {
		this.code = code;
		this.expiry = expiry;
		this.discount = discount;
	}

	public String getCode() {
		return code;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public BigDecimal getDiscount() {
		return discount;
	}
}
