package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.ErrorModel;

public class NotFoundResource extends BusinessException {

	public NotFoundResource(String errorMessage, ErrorModel errorModel) {
		super(errorMessage, errorModel);
	}

}
