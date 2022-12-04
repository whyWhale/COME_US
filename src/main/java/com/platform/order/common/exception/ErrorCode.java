package com.platform.order.common.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorModel {
	// COMMON
	NOT_FOUND_RESOURCES("C001", "not found resources", NOT_FOUND),
	FATAL_ERROR("C002", "[fatal] Internal Server error", INTERNAL_SERVER_ERROR),

	//VALIDATION
	BINDING_ERROR("V001", "Biding error", BAD_REQUEST),
	CONSTRAINT_VIOLATION("V002", "Validation error", BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V003", "Data integrity violation", BAD_REQUEST),

	// BUSINESS,
	ALREADY_ISSUE_COUPON("B001", "already issue coupon", BAD_REQUEST),
	ALREADY_USE_COUPON("B002", "already use coupon", BAD_REQUEST),
	OUT_OF_STOCK("B003", "out of quantity", BAD_REQUEST),
	NOT_AUTHENTICATE("B004", "not authenticate", UNAUTHORIZED);
	private final String code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(String code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public HttpStatus getStatus() {
		return httpStatus;
	}
}
