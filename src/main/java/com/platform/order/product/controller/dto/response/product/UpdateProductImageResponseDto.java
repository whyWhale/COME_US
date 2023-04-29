package com.platform.order.product.controller.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateProductImageResponseDto(
	@Schema(description = "이미지 파일 이름")
	String fileName,

	@Schema(description = "원본 파일 이름")
	String originName,

	@Schema(description = "파일 확장자")
	String fileExtension,

	@Schema(description = "파일 경로")
	String path,

	@Schema(description = "파일 크기")
	Long size,

	@Schema(description = "배치 순서")
	Long arrangement
) {

}
