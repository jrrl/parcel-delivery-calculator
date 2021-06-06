package com.mynt.test.core.rules.impl;

import com.mynt.test.core.parcel.Parcel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class SmallParcelRule extends AbstractRule {
	private final BigDecimal volumeLimit;
	private final BigDecimal price;

	SmallParcelRule(@Value("${rule.smallParcel.enabled:true}") boolean enabled,
	                @Value("${rule.smallParcel.priority:1}") int priority,
	                @Value("${rule.smallParcel.limit:1000}") double volumeLimit,
	                @Value("${rule.smallParcel.cost:1}") double price) {
		super(enabled, priority);
		this.volumeLimit = BigDecimal.valueOf(volumeLimit);
		this.price = BigDecimal.valueOf(price);
	}

	@Override
	BigDecimal calculate(Parcel parcel) {
		return parcel.getVolume().multiply(price);
	}

	@Override
	public boolean isApplicable(Parcel parcel) {
		return parcel.getVolume().compareTo(volumeLimit) < 0;
	}
}
