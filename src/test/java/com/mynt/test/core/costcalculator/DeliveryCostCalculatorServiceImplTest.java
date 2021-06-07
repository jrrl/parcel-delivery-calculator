package com.mynt.test.core.costcalculator;

import com.mynt.test.core.parcel.InvalidParcelException;
import com.mynt.test.core.parcel.Parcel;
import com.mynt.test.core.rules.InapplicableRuleException;
import com.mynt.test.core.rules.Rule;
import com.mynt.test.core.voucher.Voucher;
import com.mynt.test.core.voucher.VoucherRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.validation.Validation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DeliveryCostCalculatorServiceImplTest {
	private List<Rule> rules = List.of(new TestRejectRule(), new TestSuccessRule(), new TestDisabledRule());
	private VoucherRepo voucherRepo;
	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	private DeliveryCostCalculatorServiceImpl costCalculator;

	@BeforeEach
	public void setUp() {
		voucherRepo = Mockito.mock(VoucherRepo.class);
		costCalculator = new DeliveryCostCalculatorServiceImpl(
			rules, voucherRepo, validator
		);
	}

	@Test
	public void findApplicableRule() throws Exception {
		Parcel rejectParcel = new Parcel(
			15, 10, 10, 10
		);
		Parcel successParcel = new Parcel(
			5, 10, 10, 10
		);
		Parcel invalidParcel = new Parcel(
			5, 1000, 10, 10
		);

		assertTrue(costCalculator.findApplicableRule(rejectParcel) instanceof TestRejectRule);
		assertTrue(costCalculator.findApplicableRule(successParcel) instanceof TestSuccessRule);
		Assertions.assertThrows(InvalidParcelException.class, () -> {
			costCalculator.findApplicableRule(invalidParcel);
		});
	}

	@Test
	public void findVoucher_nullOrEmpty() {
		assertNull(costCalculator.findVoucher(null));
		assertNull(costCalculator.findVoucher(""));
		assertNull(costCalculator.findVoucher("    "));
	}

	@Test
	public void findVoucher_notFound() {
		Voucher voucher = new Voucher(
			null,
			BigDecimal.ZERO,
			"Invalid code"
		);

		Mockito.when(voucherRepo.getVoucherDetails("nonExistentVoucher")).thenReturn(voucher);
		Voucher result = costCalculator.findVoucher("nonExistentVoucher");

		assertNull(result.getExpiry());
		assertEquals(BigDecimal.ZERO, result.getDiscount());
		assertEquals("Invalid code", result.getErrorMessage());
	}

	@Test
	public void findVoucher_success() {
		Voucher voucher = new Voucher(
			LocalDate.MAX,
			BigDecimal.TEN,
			null
		);

		Mockito.when(voucherRepo.getVoucherDetails("success")).thenReturn(voucher);
		Voucher result = costCalculator.findVoucher("success");

		assertEquals(LocalDate.MAX, voucher.getExpiry());
		assertEquals(BigDecimal.TEN, result.getDiscount());
		assertNull(voucher.getErrorMessage());
	}

	@Test
	public void findVoucher_expiredVoucher() {
		Voucher voucher = new Voucher(
			LocalDate.MIN,
			BigDecimal.TEN,
			null
		);

		Mockito.when(voucherRepo.getVoucherDetails("expired")).thenReturn(voucher);
		Voucher result = costCalculator.findVoucher("expired");

		assertEquals(LocalDate.MIN, voucher.getExpiry());
		assertEquals(BigDecimal.TEN, result.getDiscount());
		assertEquals("Voucher has already expired", result.getErrorMessage());
	}

	@Test
	public void calculatedCost_noVoucherCode_success() throws Exception {
		Parcel successParcel = new Parcel(
			5, 10, 10, 10
		);
		CostDetails result = costCalculator.calculateCost(successParcel, null);
		assertEquals(0, BigDecimal.TEN.compareTo(result.getCalculatedCost()));
		assertEquals(0, BigDecimal.TEN.compareTo(result.getTotalCost()));
		assertNull(result.getVoucher());
	}

	@Test
	public void calculatedCost_expiredVoucher_success() throws Exception {
		Parcel successParcel = new Parcel(
			5, 10, 10, 10
		);
		Voucher voucher = new Voucher(
			LocalDate.MIN,
			BigDecimal.TEN,
			null
		);

		Mockito.when(voucherRepo.getVoucherDetails("expired")).thenReturn(voucher);

		CostDetails result = costCalculator.calculateCost(successParcel, "expired");
		assertEquals(0, BigDecimal.TEN.compareTo(result.getCalculatedCost()));
		assertEquals(0, BigDecimal.TEN.compareTo(result.getTotalCost()));
		assertNotNull(result.getVoucher());
		assertEquals(0, BigDecimal.TEN.compareTo(result.getVoucher().getDiscount()));
		assertTrue(LocalDate.now().isAfter(result.getVoucher().getExpiry()));
		assertEquals("Voucher has already expired", result.getVoucher().getErrorMessage());
		assertFalse(result.getVoucher().isApplied());
	}

	@Test
	public void calculatedCost_nonExistentVoucher_success() throws Exception {
		Parcel successParcel = new Parcel(
			5, 10, 10, 10
		);
		Voucher voucher = new Voucher(
			null,
			BigDecimal.ZERO,
			"Invalid code"
		);

		Mockito.when(voucherRepo.getVoucherDetails("nonExistentVoucher")).thenReturn(voucher);

		CostDetails result = costCalculator.calculateCost(successParcel, "nonExistentVoucher");
		assertEquals(0, BigDecimal.TEN.compareTo(result.getCalculatedCost()));
		assertEquals(0, BigDecimal.TEN.compareTo(result.getTotalCost()));
		assertNotNull(result.getVoucher());
		assertEquals(0, BigDecimal.ZERO.compareTo(result.getVoucher().getDiscount()));
		assertEquals("Invalid code", result.getVoucher().getErrorMessage());
		assertFalse(result.getVoucher().isApplied());
	}

	@Test
	public void calculatedCost_validVoucher_success() throws Exception {
		Parcel successParcel = new Parcel(
			5, 10, 10, 10
		);
		Voucher voucher = new Voucher(
			LocalDate.MAX,
			BigDecimal.ONE,
			null
		);

		Mockito.when(voucherRepo.getVoucherDetails("valid")).thenReturn(voucher);

		CostDetails result = costCalculator.calculateCost(successParcel, "valid");
		assertEquals(0, BigDecimal.TEN.compareTo(result.getCalculatedCost()));
		assertEquals(0, BigDecimal.valueOf(9).compareTo(result.getTotalCost()));
		assertNotNull(result.getVoucher());
		assertEquals(0, BigDecimal.ONE.compareTo(result.getVoucher().getDiscount()));
		assertTrue(LocalDate.now().isBefore(result.getVoucher().getExpiry()));
		assertNull(result.getVoucher().getErrorMessage());
		assertTrue(result.getVoucher().isApplied());
	}

	@Test
	public void calculatedCost_invalidParcel_fail() throws Exception {
		Parcel successParcel = new Parcel(
			5, -10, 10, 10
		);

		assertThrows(InvalidParcelException.class, () -> costCalculator.calculateCost(successParcel, "valid"));
	}

	@Test
	public void calculatedCost_rejectedParcel_fail() throws Exception {
		Parcel successParcel = new Parcel(
			500, 10, 10, 10
		);

		assertThrows(InvalidParcelException.class, () -> costCalculator.calculateCost(successParcel, "valid"));
	}

	private static class TestRejectRule implements Rule {

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Override
		public boolean isApplicable(Parcel parcel) {
			return parcel.getWeight() > 10;
		}

		@Override
		public int getPriority() {
			return 1;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public BigDecimal calculateCost(Parcel parcel) throws InapplicableRuleException {
			return BigDecimal.ZERO;
		}

		@Override
		public int compareTo(Rule o) {
			return getPriority() - o.getPriority();
		}
	}

	private static class TestSuccessRule implements Rule {

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isApplicable(Parcel parcel) {
			return parcel.getVolume().compareTo(BigDecimal.valueOf(5000)) < 0;
		}

		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public BigDecimal calculateCost(Parcel parcel) throws InapplicableRuleException {
			return parcel.getVolume().multiply(BigDecimal.valueOf(0.01));
		}

		@Override
		public int compareTo(Rule o) {
			return getPriority() - o.getPriority();
		}
	}

	private static class TestDisabledRule implements Rule {

		@Override
		public boolean isSuccess() {
			return true;
		}

		@Override
		public boolean isApplicable(Parcel parcel) {
			return parcel.getVolume().compareTo(BigDecimal.valueOf(5000)) < 0;
		}

		@Override
		public int getPriority() {
			return 3;
		}

		@Override
		public boolean isEnabled() {
			return false;
		}

		@Override
		public BigDecimal calculateCost(Parcel parcel) throws InapplicableRuleException {
			return parcel.getVolume().multiply(BigDecimal.valueOf(0.01));
		}

		@Override
		public int compareTo(Rule o) {
			return getPriority() - o.getPriority();
		}
	}
}

