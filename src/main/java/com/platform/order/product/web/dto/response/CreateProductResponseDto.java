package com.platform.order.product.web.dto.response;

public record CreateProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String categoryCode
) {
}
