package com.platform.order.common.protocal;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {
	private int totalPage;
	private int page;
	private int size;
	private List<T> contents;
}
