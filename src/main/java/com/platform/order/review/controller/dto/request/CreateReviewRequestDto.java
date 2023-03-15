package com.platform.order.review.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

public record CreateReviewRequestDto(
	@NotNull
	Long orderProductId,

	@NotNull
	@Range(min = 1, max = 5)
	Integer score,

	@NotBlank
	String contents
) {
}
