package com.platform.order.product.controller.dto.response.product;

public record CreateProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String categoryCode) {
}
