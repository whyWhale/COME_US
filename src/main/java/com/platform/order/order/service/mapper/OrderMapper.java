package com.platform.order.order.service.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.platform.order.common.dto.cursor.CursorPageResponseDto;
import com.platform.order.order.controller.dto.response.CreateOrderResponseDto;
import com.platform.order.order.controller.dto.response.ReadMyOrderResponseDto;
import com.platform.order.order.domain.order.entity.OrderEntity;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;

@Component
public class OrderMapper {
	public CreateOrderResponseDto toCreateOrderResponseDto(OrderEntity order) {
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

	public CursorPageResponseDto<ReadMyOrderResponseDto> toCursorPageResponse(List<OrderProductEntity> orderProducts) {
		var myOrderResponses = orderProducts.stream()
			.map(this::toReadMyOrderResponseDto)
			.toList();
		OrderProductEntity tailOrderProduct = orderProducts.get(orderProducts.size() - 1);

		return new CursorPageResponseDto<>(
			tailOrderProduct.getId(),
			orderProducts.size(),
			myOrderResponses);
	}

	public ReadMyOrderResponseDto toReadMyOrderResponseDto(OrderProductEntity orderProduct) {
		return new ReadMyOrderResponseDto(
			orderProduct.getId(),
			orderProduct.getOrderQuantity(),
			orderProduct.getToTalPrice(),
			orderProduct.getProduct().getId(),
			orderProduct.getProduct().getName(),
			orderProduct.getProduct().getProductThumbnail().getPath(),
			orderProduct.getProduct().getPrice(),
			orderProduct.isUseCoupon(),
			orderProduct.isUseCoupon() ? orderProduct.getUserCoupon().getCoupon().getType() : null,
			orderProduct.isUseCoupon() ? orderProduct.getUserCoupon().getCoupon().getAmount() : null
		);
	}
}
