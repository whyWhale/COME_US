package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.model.ErrorModel;

public class BusinessException extends RuntimeException {
	private final ErrorModel errorModel;

	public BusinessException(String message, ErrorModel errorModel) {
		super(message);
		this.errorModel = errorModel;
	}
}
