package com.platform.order.common.protocal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Range;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageDto {
	public record PageRequestDto(
		@NotNull
		@Positive
		Integer page,
		@NotNull
		@Range(min = 5, max = 10, message = "목록 단위는 5 ~ 10까지 가능합니다.")
		Integer size
	) {
		public Pageable of() {
			return PageRequest.of(page - 1, size, Sort.by("createdAt"));
		}

		public Pageable of(Sort sort) {
			return PageRequest.of(page - 1, size, sort);
		}
	}
}
