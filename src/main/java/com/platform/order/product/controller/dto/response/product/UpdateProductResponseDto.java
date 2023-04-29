package com.platform.order.product.controller.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateProductResponseDto(
	@Schema(description = "상품 이름")
	String name,

	@Schema(description = "상품 수량")
	Long quantity,

	@Schema(description = "상품 가격")
	Long price,

	@Schema(description = "상품 카테고리 코드")
	String categoryCode
) {

}
