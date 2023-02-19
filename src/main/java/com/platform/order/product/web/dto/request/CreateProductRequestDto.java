package com.platform.order.product.web.dto.request;

public record CreateProductRequestDto(
	String name,
	Long quantity,
	Long price,
	String categoryCode
) {
}
