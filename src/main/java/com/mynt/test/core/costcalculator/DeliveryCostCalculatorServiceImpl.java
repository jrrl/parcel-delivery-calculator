package com.mynt.test.core.costcalculator;

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
import java.util.stream.Collectors;

@Component
public class DeliveryCostCalculatorServiceImpl implements DeliveryCostCalculatorService {
	private static final Logger logger = LoggerFactory.getLogger(DeliveryCostCalculatorServiceImpl.class);

	private final List<Rule> rules;
	private final VoucherRepo voucherRepo;
	private final Validator validator;

	public DeliveryCostCalculatorServiceImpl(List<Rule> rules, VoucherRepo voucherRepo, Validator validator) {
		this.rules = rules;
		this.voucherRepo = voucherRepo;
		this.validator = validator;
	}

	public CostDetails calculateCost(final Parcel parcel, final String voucherCode)
		throws InvalidParcelException, InapplicableRuleException {
		if(!validator.validate(parcel).isEmpty()) {
			throw new InvalidParcelException("Invalid parcel");
		}
		Rule applicableRule = findApplicableRule(parcel);
		if(!applicableRule.isSuccess()) {
			throw new InvalidParcelException("Invalid parcel");
		}
		BigDecimal calculatedCost = applicableRule.calculateCost(parcel);
		logger.info("Calculated Cost: {}", calculatedCost);
		CostDetails.Builder costBuilder = CostDetails.builder();
		costBuilder.calculatedCost(calculatedCost);
		costBuilder.voucher(findVoucher(voucherCode));
		return costBuilder.build();
	}

	Rule findApplicableRule(Parcel parcel) throws InvalidParcelException {
		return rules.stream()
		     .filter(rule -> rule.isEnabled() && rule.isApplicable(parcel))
		     .min(Rule::compareTo)
		     .orElseThrow(() -> new InvalidParcelException("No applicable rules found"));
	}

	Voucher findVoucher(String voucherCode) {
		if(voucherCode == null || voucherCode.isBlank()) {
			return null;
		}
		Voucher voucher = voucherRepo.getVoucherDetails(voucherCode);
		Set<ConstraintViolation<Voucher>> errors = validator.validate(voucher);
		if (!errors.isEmpty()) {
			String errorMessage =
				errors.stream()
				      .map(ConstraintViolation::getMessage)
				      .collect(Collectors.joining(" "));
			voucher = voucher.withErrorMessage(errorMessage);
		}
		return voucher;
	}
}
