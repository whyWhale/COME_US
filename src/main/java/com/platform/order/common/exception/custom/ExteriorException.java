package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.model.ErrorModel;

public class ExteriorException extends RuntimeException {
	private final ErrorModel errorModel;

	public ExteriorException(String message, ErrorModel errorModel) {
		super(message);
		this.errorModel = errorModel;
	}

	public ErrorModel errorModel() {
		return errorModel;
	}

	@Override
	public String toString() {
		return "[BusinessException] " + errorModel;
	}
}
