package com.mynt.test.core;

import java.math.BigDecimal;

public class CostDetails {
	private final BigDecimal calculatedCost;
	private final BigDecimal totalCost;
	private final BigDecimal discount;

	public CostDetails(BigDecimal calculatedCost, BigDecimal discount) {
		this.calculatedCost = calculatedCost;
		this.discount = discount != null ? discount : BigDecimal.ZERO;
		this.totalCost = this.calculatedCost.subtract(this.discount);
	}

	public BigDecimal getCalculatedCost() {
		return calculatedCost;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private BigDecimal calculatedCost;
		private BigDecimal discount;

		Builder calculatedCost(BigDecimal calculatedCost) {
			this.calculatedCost = calculatedCost;
			return this;
		}

		public Builder discount(BigDecimal discount) {
			this.discount = discount;
			return this;
		}

		public CostDetails build() {
			return new CostDetails(calculatedCost, discount);
		}
	}
}
