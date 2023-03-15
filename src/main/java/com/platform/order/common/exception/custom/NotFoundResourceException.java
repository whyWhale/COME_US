package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.exception.model.ErrorModel;

public class NotFoundResourceException extends RuntimeException{
	private final ErrorModel errorModel;

	public NotFoundResourceException(String message) {
		super(message);
		this.errorModel = ErrorCode.NOT_FOUND_RESOURCES;
	}

	@Override
	public String toString() {
		return "[NotFountResource] " + errorModel;
	}

}
