package com.platform.order.common.exception;

public class NotFoundResource extends BusinessException {

	public NotFoundResource(String errorMessage, ErrorModel errorModel) {
		super(errorMessage, errorModel);
	}

}
