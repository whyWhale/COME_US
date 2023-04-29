package com.platform.order.review.controller.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateReviewResponseDto(

	@Schema(description = "리뷰 아이디")
	Long id,

	@Schema(description = "평점")
	Integer score,

	@Schema(description = "리뷰 게시글")
	String content,

	@Schema(description = "리뷰 이미지 경로")
	List<UpdateReviewImageResponseDto> reviewImageResponses
) {
	public record UpdateReviewImageResponseDto(
		@Schema(description = "리뷰 이미지 아이디")
		Long id,

		@Schema(description = "리뷰 이미지 경로")
		String path) {

	}
}
