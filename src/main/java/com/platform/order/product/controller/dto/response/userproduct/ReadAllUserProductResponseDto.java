package com.platform.order.product.controller.dto.response.userproduct;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReadAllUserProductResponseDto(
	@Schema(description = "찜 아이디")
	Long id,

	@Schema(description = "찜 삼품 카테고리 코드")
	String categoryCode,

	@Schema(description = "찜 상품 카테고리 이름")
	String categoryName,

	@Schema(description = "찜 상품 아이디")
	Long productId,

	@Schema(description = "찜 상품 이름")
	String productName,

	@Schema(description = "찜 상품 가격")
	Long price,

	@Schema(description = "찜 상품 섬네일 경로")
	String thumbnailPath
) {
}
