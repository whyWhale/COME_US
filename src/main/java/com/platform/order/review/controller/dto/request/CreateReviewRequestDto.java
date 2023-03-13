package com.platform.order.review.controller.dto.request;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public record CreateReviewRequestDto(
	@NotNull
	Long orderProductId,

	@Range(min = 1, max = 5)
	Integer score,

	String contents
) {
}
