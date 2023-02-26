package com.platform.order.common.exception.model;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

public enum ErrorCode implements ErrorModel {
	// COMMON
	NOT_FOUND_RESOURCES("C001", "not found resources", NOT_FOUND),
	FATAL_ERROR("C002", "[fatal] Internal Server error", INTERNAL_SERVER_ERROR),
	NOT_AUTHENTICATE("C003", "not authenticate", UNAUTHORIZED),
	//VALIDATION
	BINDING_ERROR("V001", "Biding error", BAD_REQUEST),
	CONSTRAINT_VIOLATION("V002", "Validation error", BAD_REQUEST),
	DATA_INTEGRITY_VIOLATION("V003", "Data integrity violation", BAD_REQUEST),

	// BUSINESS
	NOT_OWNER("B001", "not owner", BAD_REQUEST),
	EntityConstraint("B002", "entity constraint", BAD_REQUEST),
	OUT_OF_QUANTITY("B003", "out of quantity", PRECONDITION_FAILED),
	// IO
	FILE_IO("I0004", "File I/O fail", BAD_REQUEST);
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
