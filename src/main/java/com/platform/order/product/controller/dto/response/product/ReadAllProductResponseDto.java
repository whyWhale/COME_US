package com.platform.order.product.controller.dto.response.product;

public record ReadAllProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String thumbnailPath,
	String category) {
}
