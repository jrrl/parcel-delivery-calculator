package com.mynt.test.infra.voucher;

import com.fasterxml.jackson.annotation.JsonCreator;

public class VoucherErrorResponse {
	private final String error;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public VoucherErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
