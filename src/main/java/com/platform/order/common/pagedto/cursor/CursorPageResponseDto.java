package com.platform.order.common.pagedto.cursor;

import java.util.List;

public record CursorPageResponseDto<T>(
	Long lastId,
	int size,
	List<T> contents) {
}
