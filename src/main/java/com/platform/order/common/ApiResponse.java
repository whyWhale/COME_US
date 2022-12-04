package com.platform.order.common;

public class ApiResponse<T> {
	private T data;

	private ApiResponse() {
	}

	public ApiResponse(T data) {
		this.data = data;
	}

	public T getData() { return data;}
}
