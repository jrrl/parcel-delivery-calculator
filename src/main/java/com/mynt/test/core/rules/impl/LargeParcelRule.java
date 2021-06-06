package com.mynt.test.core.rules.impl;

import com.mynt.test.core.parcel.Parcel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class LargeParcelRule extends AbstractRule {
	private final BigDecimal price;

	LargeParcelRule(@Value("${rule.largeParcel.enabled:true}") boolean enabled,
	                @Value("${rule.largeParcel.priority:1}") int priority,
	                @Value("${rule.largeParcel.cost:1}") double price) {
		super(enabled, priority);
		this.price = BigDecimal.valueOf(price);
	}

	@Override
	BigDecimal calculate(Parcel parcel) {
		return parcel.getVolume().multiply(price);
	}

	@Override
	public boolean isApplicable(Parcel parcel) {
		return true;
	}
}
