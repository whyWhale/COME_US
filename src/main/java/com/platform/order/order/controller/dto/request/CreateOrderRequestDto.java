package com.platform.order.order.controller.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record CreateOrderRequestDto(
	@NotBlank
	String address,

	@NotBlank
	String zipCode,

	@Valid
	@Size(min = 1)
	List<OrderProductRequestDto> orderProductRequests,

	@Valid
	@NotNull
	Location location
) {
	public record OrderProductRequestDto(
		@NotNull
		Long productId,

		@NotNull
		Long orderQuantity,

		Long userCouponId
	) {
		public boolean hasCoupon() {
			return this.userCouponId != null;
		}
	}
}