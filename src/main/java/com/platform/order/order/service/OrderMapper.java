package com.platform.order.order.service;

import org.springframework.stereotype.Component;

import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.domain.order.entity.OrderEntity;

@Component
public class OrderMapper {
	public CreateOrderResponseDto toMultiCreateOrderResponseDto(OrderEntity order) {
		var createOrderProductResponses = order.getOrderproducts()
			.stream()
			.map(orderProduct -> new CreateOrderResponseDto.CreateOrderProductResponse(
				orderProduct.getProduct().getId(),
				orderProduct.getOrderQuantity(),
				orderProduct.getToTalPrice(),
				orderProduct.getUserCoupon() != null,
				orderProduct.getUserCoupon() != null ? orderProduct.getUserCoupon().getCoupon().getType() : null))
			.toList();

		return new CreateOrderResponseDto(order.getId(), createOrderProductResponses);
	}
}
