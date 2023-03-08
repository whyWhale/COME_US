package com.platform.order.product.controller.dto.response.product;

public record CreateProductImagesResponseDto(
	String fileName,
	String originName,
	String fileExtension,
	String path,
	Long size,
	Long arrangement) {
}