package com.platform.order.order.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.platform.order.order.domain.entity.OrderEntity;
import com.platform.order.order.domain.entity.OrderProductEntity;
import com.platform.order.order.web.dto.response.CreateOrderResponseDto;

import com.platform.order.product.domain.entity.ProductEntity;

@Component
public class OrderConverter {
	public CreateOrderResponseDto toCreateOrderResponseDto(OrderEntity order, Map<Long, Long> pickedProducts,
		List<OrderProductEntity> savedOrderProduct) {
		List<CreateOrderResponseDto.OrderProductResponseDto> orderProductResponseDtos = savedOrderProduct.stream().map(orderProduct -> {
			ProductEntity productEntity = orderProduct.getProduct();

			return new CreateOrderResponseDto.OrderProductResponseDto(
				productEntity.getId(),
				pickedProducts.get(productEntity.getId()),
				productEntity.getPrice()
			);
		}).collect(Collectors.toList());

		return new CreateOrderResponseDto(order.getId(),
			orderProductResponseDtos);
	}
}
