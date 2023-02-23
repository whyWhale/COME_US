package com.platform.order.product.controller.dto.response;

public record UpdateProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String categoryCode
) {

}
