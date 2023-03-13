package com.platform.order.review.controller.dto.response;

import java.util.List;

public record CreateReviewResponseDto(
	Long id,
	Long orderProductId,
	Long userId,
	Integer score,
	List<CreateReviewImageResponseDto> reviewImageResponses
) {
	public record CreateReviewImageResponseDto(Long id, String path) {

	}
}
