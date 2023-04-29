package com.platform.order.product.controller.dto.request.product;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateProductRequestDto(
	@Schema(description = "이름", required = true)
	@NotBlank
	String name,

	@Schema(description = "재고량", required = true)
	@NotNull
	@PositiveOrZero
	Long quantity,

	@Schema(description = "가격", required = true)
	@NotNull
	@Positive
	Long price,

	@Schema(description = "상품 유형", required = true)
	@NotBlank
	String categoryCode
) {

}
