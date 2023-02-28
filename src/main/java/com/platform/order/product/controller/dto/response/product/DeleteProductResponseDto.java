package com.platform.order.product.controller.dto.response.product;

public record DeleteProductResponseDto(
	Long id,
	String name,
	Long quantity,
	Long price,
	boolean isDisplay,
	String categoryName,
	String categoryCode) {
}
