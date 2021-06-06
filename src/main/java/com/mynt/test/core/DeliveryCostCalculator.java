package com.mynt.test.core;

import com.mynt.test.core.parcel.InvalidParcelException;
import com.mynt.test.core.parcel.Parcel;
import com.mynt.test.core.rules.InapplicableRuleException;
import com.mynt.test.core.rules.Rule;
import com.mynt.test.core.voucher.Voucher;
import com.mynt.test.core.voucher.VoucherRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Component
public class DeliveryCostCalculator {
	private static final Logger logger = LoggerFactory.getLogger(DeliveryCostCalculator.class);

	private final List<Rule> rules;
	private final VoucherRepo voucherRepo;
	private final Validator validator;

	public DeliveryCostCalculator(List<Rule> rules, VoucherRepo voucherRepo, Validator validator) {
		this.rules = rules;
		this.voucherRepo = voucherRepo;
		this.validator = validator;
	}

	public CostDetails calculateCost(final Parcel parcel, final String voucherCode) throws InvalidParcelException, InapplicableRuleException {
		CostDetails.Builder costBuilder = CostDetails.builder();
		Rule applicableRule = rules.stream()
			.filter(rule -> rule.isEnabled() && rule.isApplicable(parcel))
			.sorted()
			.findFirst()
			.orElseThrow(() -> new InvalidParcelException(""));
		BigDecimal calculatedCost = applicableRule.calculateCost(parcel);
		logger.info("Calculated Cost: {}", calculatedCost);
		costBuilder.calculatedCost(calculatedCost);
		if(voucherCode != null) {
			Voucher voucher = voucherRepo.getVoucherDetails(voucherCode);
			Set<ConstraintViolation<Voucher>> errors = validator.validate(voucher);
			if(errors.isEmpty()) {
				costBuilder.discount(voucher.getDiscount());
			}
		}
		return costBuilder.build();
	}
}
