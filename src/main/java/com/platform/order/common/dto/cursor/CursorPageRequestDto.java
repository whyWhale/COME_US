package com.platform.order.common.dto.cursor;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CursorPageRequestDto {
	protected Long lastId;

	@NotNull
	@Range(min = 5, max = 15)
	protected Integer size;

}
