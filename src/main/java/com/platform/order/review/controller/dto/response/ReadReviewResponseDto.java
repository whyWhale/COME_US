package com.platform.order.review.controller.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadReviewResponseDto(
	@Schema(description = "리뷰 작성자")
	String nickName,

	@Schema(description = "리뷰 평점")
	int score,

	@Schema(description = "리뷰 게시글")
	String content,

	@Schema(description = "리뷰 이미지 경로")
	List<String> imagePath
) {
}
