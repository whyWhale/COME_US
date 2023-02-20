package com.platform.order.product.web.dto.response;

public record UpdateProductResponseDto(
	String name,
	Long quantity,
	Long price,
	String categoryCode
) {

}
