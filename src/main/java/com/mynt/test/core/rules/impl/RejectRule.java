package com.mynt.test.core.rules.impl;

import com.mynt.test.core.parcel.Parcel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class RejectRule extends AbstractRule {
	private final double weightLimit;

	RejectRule(@Value("${rule.reject.enabled:true}") boolean enabled,
	           @Value("${rule.reject.priority:1}") int priority,
	           @Value("${rule.reject.limit:50}") double weightLimit) {
		super(enabled, priority);
		this.weightLimit = weightLimit;
	}

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public boolean isApplicable(Parcel parcel) {
		return parcel.getWeight() > weightLimit;
	}

	@Override
	BigDecimal calculate(Parcel parcel) {
		return BigDecimal.ZERO;
	}
}
