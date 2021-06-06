package com.mynt.test.core.rules.impl;

import com.mynt.test.core.parcel.Parcel;
import com.mynt.test.core.rules.InapplicableRuleException;
import com.mynt.test.core.rules.Rule;

import java.math.BigDecimal;

abstract class AbstractRule implements Rule {
	private final int priority;
	private final boolean enabled;

	protected AbstractRule(boolean enabled, int priority) {
		this.priority = priority;
		this.enabled = enabled;
	}

	abstract BigDecimal calculate(Parcel parcel);

	@Override
	public boolean isSuccess() {
		return true;
	}

	@Override
	public int getPriority() {
		return  priority;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public BigDecimal calculateCost(Parcel parcel) throws InapplicableRuleException {
		if(!isApplicable(parcel)) {
			throw new InapplicableRuleException();
		}
		return calculate(parcel);
	}

	@Override
	public int compareTo(Rule rule) {
		return this.priority - rule.getPriority();
	}
}
