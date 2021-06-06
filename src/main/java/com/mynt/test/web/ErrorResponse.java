package com.mynt.test.web;

public class ErrorResponse {
	private final String error;

	public ErrorResponse(String error) {
		this.error = error;
	}

	public String getError() {
		return error;
	}
}
