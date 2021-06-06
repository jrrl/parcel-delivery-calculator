package com.mynt.test.core.voucher;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Voucher {
	@Future
	private final LocalDate expiry;
	@PositiveOrZero
	private final BigDecimal discount;
	private final String errorMessage;

	public Voucher(LocalDate expiry, BigDecimal discount, String errorMessage) {
		this.expiry = expiry;
		this.discount = discount;
		this.errorMessage = errorMessage;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
