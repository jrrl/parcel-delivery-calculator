package com.mynt.test.core.voucher;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Voucher {
	@Future(message = "Voucher has already expired")
	private final LocalDate expiry;
	@PositiveOrZero
	private final BigDecimal discount;
	private final Boolean isApplied;
	private final String errorMessage;

	public Voucher(LocalDate expiry, BigDecimal discount, String errorMessage) {
		this.expiry = expiry;
		this.discount = discount;
		this.errorMessage = errorMessage;
		this.isApplied = errorMessage == null || errorMessage.isBlank();
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

	public Boolean isApplied() {
		return isApplied;
	}

	public Voucher withErrorMessage(String errorMessage) {
		return new Voucher(this.expiry, this.discount, errorMessage);
	}
}
