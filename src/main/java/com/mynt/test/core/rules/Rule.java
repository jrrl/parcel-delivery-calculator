package com.mynt.test.core.rules;

import com.mynt.test.Parcel;

import java.math.BigDecimal;

public interface Rule {
	/**
	 * States whether this rule leads to a success or reject state
	 * @return true if it leads to a success state, false for reject states
	 */
	boolean isSuccess();

	/**
	 * Check if this rule is applicable to the given parcel
	 * @param parcel - cannot be null
	 * @return true if this rule can be applied to the parcel, otherwise false
	 */
	boolean isApplicable(Parcel parcel);

	/**
	 * Get the priority of this rule. A lower number means a higher priority
	 * @return priority of this rule
	 */
	int getPriority();

	/**
	 * Check if this rule is enabled. Disable rules cannot calculate cost.
	 * @return true if this rule is enabled
	 */
	boolean isEnabled();

	/**
	 * Calculates the total cost of the parcel using this rule. Will throw an exception when used for an inapplicable or disabled rule
	 * @param parcel
	 * @return total cost of the parcel
	 * @throws InapplicableRuleException - thrown when the given rule cannot be applied to the parcel
	 */
	 BigDecimal calculateCost(Parcel parcel) throws InapplicableRuleException;
}
