package com.mynt.test.core.costcalculator;

import com.mynt.test.core.parcel.InvalidParcelException;
import com.mynt.test.core.parcel.Parcel;
import com.mynt.test.core.rules.InapplicableRuleException;

public interface DeliveryCostCalculatorService {
	CostDetails calculateCost(Parcel parcel, String voucherCode) throws InvalidParcelException, InapplicableRuleException;
}
