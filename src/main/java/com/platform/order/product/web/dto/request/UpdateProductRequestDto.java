package com.platform.order.product.web.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record UpdateProductRequestDto(
	@NotBlank
	String name,

	@NotNull
	@PositiveOrZero
	Long quantity,

	@NotNull
	@Positive
	Long price,

	@NotBlank
	String categoryCode
) {

}
