package com.mynt.test.core.rules.impl;

import com.mynt.test.core.parcel.Parcel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class HeavyParcelRule extends AbstractRule {
	private final double weightLimit;
	private final BigDecimal cost;

	HeavyParcelRule(@Value("${rule.heavyParcel.enabled:true}") boolean enabled,
	                @Value("${rule.heavyParcel.priority:1}") int priority,
	                @Value("${rule.heavyParcel.limit:50}") double weightLimit,
	                @Value("${rule.heavyParcel.cost:1}") double cost) {
		super(enabled, priority);
		this.weightLimit = weightLimit;
		this.cost = BigDecimal.valueOf(cost);
	}

	@Override
	public boolean isApplicable(Parcel parcel) {
		return parcel.getWeight() > weightLimit;
	}

	@Override
	BigDecimal calculate(Parcel parcel) {
		return parcel.getVolume().multiply(cost);
	}
}
