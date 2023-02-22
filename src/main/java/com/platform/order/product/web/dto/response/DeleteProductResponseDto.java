package com.platform.order.product.web.dto.response;

public record DeleteProductResponseDto(
	Long id,
	String name,
	Long quantity,
	Long price,
	boolean isDisplay,
	String categoryName,
	String categoryCode
) {
}
