package com.platform.order.common.protocal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;

import lombok.Getter;

@Getter
public abstract class PageRequestDto {
	@NotNull
	@Positive
	protected Integer page;
	@NotNull
	@Range(min = 5, max = 15)
	protected Integer size;

	public PageRequestDto(Integer page, Integer size) {
		this.page = page;
		this.size = size;
	}
}
