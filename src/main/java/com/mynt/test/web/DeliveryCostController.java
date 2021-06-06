package com.mynt.test.web;

import com.mynt.test.core.costcalculator.CostDetails;
import com.mynt.test.core.costcalculator.DeliveryCostCalculator;
import com.mynt.test.core.parcel.InvalidParcelException;
import com.mynt.test.core.parcel.Parcel;
import com.mynt.test.core.rules.InapplicableRuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery")
public class DeliveryCostController {
	private final DeliveryCostCalculator costCalculator;

	public DeliveryCostController(DeliveryCostCalculator costCalculator) {
		this.costCalculator = costCalculator;
	}

	@GetMapping("/cost")
	@ResponseBody
	public CostDetails getDeliveryCost(@RequestParam double weight,
	                                   @RequestParam double height,
	                                   @RequestParam double length,
	                                   @RequestParam double width,
	                                   @RequestParam(required = false) String voucherCode) throws InvalidParcelException, InapplicableRuleException {
		Parcel parcel = new Parcel(weight, height, length, width);
		return costCalculator.calculateCost(parcel, voucherCode);
	}

	@ExceptionHandler
	public ResponseEntity<ErrorResponse> handleRejectError(InvalidParcelException invalidParcelException) {
		return ResponseEntity.badRequest().body(new ErrorResponse(invalidParcelException.getMessage()));
	}
}
