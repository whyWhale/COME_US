package com.platform.order.product.web.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record UpdateProductRequestDto(
	@NotBlank
	String name,

	@NotNull
	@Positive
	Long quantity,

	@NotNull
	@Positive
	Long price,

	@NotBlank
	String categoryCode
) {

}
