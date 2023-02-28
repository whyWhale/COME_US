package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.model.ErrorModel;

public class NotFoundResourceException extends BusinessException {

	public NotFoundResourceException(String errorMessage, ErrorModel errorModel) {
		super(errorMessage, errorModel);
	}
}
