package com.platform.order.common.exception.custom;

import com.platform.order.common.exception.model.ErrorCode;
import com.platform.order.common.exception.model.ErrorModel;

public class CustomFileIoException extends RuntimeException {
	private final ErrorModel errorModel;

	public CustomFileIoException(String message) {
		super(message);
		this.errorModel = ErrorCode.FILE_IO;
	}

	@Override
	public String toString() {
		return "[FileIOException] " + errorModel;
	}
}
