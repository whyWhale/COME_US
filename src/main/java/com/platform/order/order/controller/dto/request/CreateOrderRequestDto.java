package com.platform.order.order.controller.dto.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateOrderRequestDto(
	@Schema(description = "배송지 주소", required = true)
	@NotBlank
	String address,

	@Schema(description = "건물명-호수", required = true)
	@NotBlank
	String zipCode,

	@Schema(description = "구매할 상품 및 수량", required = true)
	@Valid
	@Size(min = 1)
	List<OrderProductRequestDto> orderProductRequests,

	@Valid
	@NotNull
	Location location
) {
	public record OrderProductRequestDto(
		@Schema(description = "주문할 상품 아이디")
		@NotNull
		Long productId,

		@Schema(description = "주문 수량")
		@NotNull
		Long orderQuantity,

		@Schema(description = "적용할 쿠폰 아이디")
		Long userCouponId
	) {
		public boolean hasCoupon() {
			return this.userCouponId != null;
		}
	}
}