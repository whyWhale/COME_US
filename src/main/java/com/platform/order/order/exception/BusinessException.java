package com.platform.order.order.exception;

public class BusinessException extends RuntimeException {
	protected BusinessException() {
	}

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(String message, Throwable cause) {
		super(message, cause);
	}
}
