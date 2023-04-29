package com.platform.order.product.controller.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateThumbnailResponseDto(
	@Schema(description = "상품 섬네일 이름")
	String fileName,

	@Schema(description = "상품 섬네일 원본 이름")
	String originName,

	@Schema(description = "상품 섬네일 확장자")
	String fileExtension,

	@Schema(description = "상품 섬네일 저장 경로")
	String path,

	@Schema(description = "상품 섬네일 사이즈")
	Long size
) {
}
