package com.platform.order.review.controller.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateReviewResponseDto(
	@Schema(description = "리뷰 아이디")
	Long id,

	@Schema(description = "주문 상품 아이디")
	Long orderProductId,

	@Schema(description = "작성자 아이디")
	Long userId,

	@Schema(description = "작성자 별점")
	Integer score,

	@Schema(description = "리뷰 이미지")
	List<CreateReviewImageResponseDto> reviewImageResponses
) {
	public record CreateReviewImageResponseDto(
		@Schema(description = "리뷰 이미지 아이디")
		Long id,

		@Schema(description = "리뷰 이미지 경로")
		String path
	) {

	}
}
