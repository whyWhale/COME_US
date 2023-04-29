package com.platform.order.product.controller.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateProductImagesResponseDto(
	@Schema(description = "상품 이미지 이름")
	String fileName,

	@Schema(description = "상품 이미지 원본 이름")
	String originName,

	@Schema(description = "상품 이미지 확장자")
	String fileExtension,

	@Schema(description = "상품 이미지 저장 경로")
	String path,

	@Schema(description = "상품 이미지 사이즈")
	Long size,

	@Schema(description = "상품 이미지 순서")
	Long arrangement
) {
}