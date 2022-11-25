package com.platform.order.order.exception;

public class NotFoundResource extends RuntimeException {
	protected NotFoundResource() {
	}

	public NotFoundResource(String message) {
		super(message);
	}

	public NotFoundResource(String message, Throwable cause) {
		super(message, cause);
	}
}
