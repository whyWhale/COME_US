package com.platform.order.product.web.dto.response;

public record ReadAllProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String thumbnailPath,
	String category) {
}
