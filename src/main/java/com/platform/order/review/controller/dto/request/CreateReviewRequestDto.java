package com.platform.order.review.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateReviewRequestDto(
	@Schema(description = "주문 상품 아이디")
	@NotNull
	Long orderProductId,

	@Schema(description = "리뷰 평점")
	@NotNull
	@Range(min = 1, max = 5)
	Integer score,

	@Schema(description = "리뷰 글")
	@NotBlank
	String contents
) {
}
