package com.platform.order.common.exception.protocal;

import java.time.LocalDateTime;

import com.platform.order.common.exception.model.ErrorModel;

import lombok.Getter;

@Getter
public class ErrorResponse<T extends ErrorModel> {
	private final String code;
	private final String message;
	private final LocalDateTime dateTime = LocalDateTime.now();

	public ErrorResponse(T errorCode) {
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}
}
