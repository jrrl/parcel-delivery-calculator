package com.mynt.test.core.costcalculator;

import com.mynt.test.core.voucher.Voucher;

import java.math.BigDecimal;

public class CostDetails {
	private final BigDecimal calculatedCost;
	private final BigDecimal totalCost;
//	private final BigDecimal discount;
	private final Voucher voucher;

	private CostDetails(BigDecimal calculatedCost, Voucher voucher) {
		this.calculatedCost = calculatedCost;
		this.voucher = voucher;
		BigDecimal discount = voucher != null && voucher.isApplied() ? voucher.getDiscount() : BigDecimal.ZERO;
		this.totalCost = this.calculatedCost.subtract(discount);
	}

	public BigDecimal getCalculatedCost() {
		return calculatedCost;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private BigDecimal calculatedCost;
		private Voucher voucher;

		Builder calculatedCost(BigDecimal calculatedCost) {
			this.calculatedCost = calculatedCost;
			return this;
		}

		public Builder voucher(Voucher voucher) {
			this.voucher = voucher;
			return this;
		}

		public CostDetails build() {
			return new CostDetails(calculatedCost, voucher);
		}
	}
}
