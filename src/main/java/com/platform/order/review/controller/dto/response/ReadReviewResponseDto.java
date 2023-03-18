package com.platform.order.review.controller.dto.response;

import java.util.List;

public record ReadReviewResponseDto(
	String nickName,
	int score,
	String content,
	List<String> imagePath) {
}
