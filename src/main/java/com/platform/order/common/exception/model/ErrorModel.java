package com.platform.order.common.exception.model;

import org.springframework.http.HttpStatus;

public interface ErrorModel {
	String getCode();

	String getMessage();

	HttpStatus getStatus();
}

