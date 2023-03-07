package com.platform.order.product.controller.dto.response.product;

public record UpdateProductThumbnailResponseDto(
	String fileName,
	String originName,
	String fileExtension,
	String path,
	Long size) {
}
