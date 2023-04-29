package com.platform.order.ranking.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record RankingReadProductResponseDto(
	@Schema(description = "상품 아이디")
	Long productId,

	@Schema(description = "상품 이름")
	String name,

	@Schema(description = "상품 가격")
	Long price,

	@Schema(description = "상품 카테고리 코드")
	String categoryCode,

	@Schema(description = "상품 섬네일 이미지 경로")
	String thumbnailPath
) {

}

