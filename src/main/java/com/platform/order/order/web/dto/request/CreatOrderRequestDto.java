package com.platform.order.order.web.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreatOrderRequestDto(
	@Size(min = 1)
	List<OrderProductRequestDto> orderProducts,
	@NotBlank
	String address,
	@NotBlank
	String zipCode
	) {

	public record OrderProductRequestDto(@NotNull Long productId,
										 @NotNull Long orderQuantity) {

	}
}
