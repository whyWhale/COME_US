package com.platform.order.common.dto.offset;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OffsetPageResponseDto<T> {
	private int totalPage;
	private int page;
	private int size;
	private List<T> contents;
}
