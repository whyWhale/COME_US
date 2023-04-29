package com.platform.order.common.dto.offset;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public abstract class OffsetPageRequestDto {
	@Schema(description = "요청할 페이지", required = true)
	@NotNull
	@Positive
	protected Integer page;

	@Schema(description = "페이지 목록 개수", required = true)
	@NotNull
	@Range(min = 5, max = 15)
	protected Integer size;

	public OffsetPageRequestDto(Integer page, Integer size) {
		this.page = page;
		this.size = size;
	}
}
