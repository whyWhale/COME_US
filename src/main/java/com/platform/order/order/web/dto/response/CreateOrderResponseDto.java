package com.platform.order.order.web.dto.response;

import java.util.List;

public record CreateOrderResponseDto(Long orderId, List<OrderProductResponseDto> orderProducts) {
	public record OrderProductResponseDto(Long productId, Long orderQuantity, Long price) {
	}
}
