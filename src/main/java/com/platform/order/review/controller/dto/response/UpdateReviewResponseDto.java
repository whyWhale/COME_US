package com.platform.order.review.controller.dto.response;

import java.util.List;

public record UpdateReviewResponseDto(
	Long id,
	Integer score,
	String content,
	List<UpdateReviewImageResponseDto> reviewImageResponses
) {
	public record UpdateReviewImageResponseDto(Long id, String path) {

	}
}
