package com.platform.order.common.exception.model;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorModel {
	// COMMON
	NOT_FOUND_RESOURCES("C001", "not found resources", NOT_FOUND),
	FATAL_ERROR("C002", "[fatal] Internal Server error", INTERNAL_SERVER_ERROR),
	NOT_AUTHENTICATE("C003", "not authenticate", UNAUTHORIZED),
	NOT_AUTHORIZATION("C004", "not found", FORBIDDEN),
	FILE_IO("I0004", "File I/O fail", INTERNAL_SERVER_ERROR),

	//VALIDATION
	BINDING_ERROR("V001", "Biding error", BAD_REQUEST),

	CONSTRAINT_VIOLATION("V002", "Validation error", BAD_REQUEST),

	DATA_INTEGRITY_VIOLATION("V003", "Data integrity violation", BAD_REQUEST),

	// BUSINESS
	NOT_OWNER("B001", "not owner", BAD_REQUEST),
	EntityConstraint("B002", "entity constraint", BAD_REQUEST),
	OUT_OF_QUANTITY("B003", "out of quantity", PRECONDITION_FAILED),
	ALREADY_WISH("B004", "already wish product", PRECONDITION_FAILED),
	ALREADY_USE_COUPON("B006", "coupon is already use", BAD_REQUEST),
	NOT_VALID_CANCEL("B007", "delivery is not cancel", BAD_REQUEST);

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
