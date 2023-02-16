package com.platform.order.order.service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.exception.custom.ErrorCode;
import com.platform.order.common.exception.custom.NotFoundResource;
import com.platform.order.order.domain.entity.OrderEntity;
import com.platform.order.order.domain.entity.OrderProductEntity;
import com.platform.order.order.domain.repository.OrderProductRepository;
import com.platform.order.order.domain.repository.OrderRepository;
import com.platform.order.order.web.dto.request.CreatOrderRequestDto;
import com.platform.order.order.web.dto.response.CreateOrderResponseDto;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.respository.ProductRepository;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class OrderService {
	private static final int SUCCESS_STOCK_DECREASE = 1;
	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final OrderProductRepository orderProductRepository;
	private final ProductRepository productRepository;
	private final OrderConverter orderConverter;

	/**
	 * todo: 다량 상품 주문 처리시 예외 생각해야함.
	 * @param userId
	 * @param creatOrderRequest
	 * @return
	 */
	public CreateOrderResponseDto placeOrder(Long userId, CreatOrderRequestDto creatOrderRequest) {
		UserEntity buyer = userRepository.findById(userId)
			.orElseThrow(() -> new NotFoundResource(
				MessageFormat.format("user id:{0} is not found.", userId),
				ErrorCode.NOT_FOUND_RESOURCES)
			);

		boolean isAvailable = creatOrderRequest.orderProducts().stream()
			.map(orderProductRequestDto -> productRepository.updateQuantity(orderProductRequestDto.productId(),
				orderProductRequestDto.orderQuantity())).allMatch(affectedRow -> affectedRow == 1);

		if (!isAvailable) {
			throw new BusinessException(
				MessageFormat.format("products : {0} is out of stock. Or not valid product id list",
					creatOrderRequest.orderProducts().stream()
						.map(CreatOrderRequestDto.OrderProductRequestDto::productId)
						.toList()),
				ErrorCode.NOT_FOUND_RESOURCES
			);
		}

		OrderEntity order = orderRepository.save(
			OrderEntity.create(buyer, creatOrderRequest.address(), creatOrderRequest.zipCode())
		);

		Map<Long, Long> pickedProducts = creatOrderRequest.orderProducts()
			.stream()
			.collect(Collectors.toMap(CreatOrderRequestDto.OrderProductRequestDto::productId,
				CreatOrderRequestDto.OrderProductRequestDto::orderQuantity));
		List<ProductEntity> productEntities = productRepository.findByIdIn(pickedProducts.keySet());
		List<OrderProductEntity> orderProducts = productEntities.stream()
			.map(productEntity -> OrderProductEntity.builder()
				.order(order)
				.product(productEntity)
				.orderQuantity(pickedProducts.get(productEntity.getId()))
				.price(productEntity.getPrice())
				.build()
			)
			.toList();

		List<OrderProductEntity> savedOrderProduct = orderProductRepository.saveAllInBulk(orderProducts);

		return orderConverter.toCreateOrderResponseDto(order, pickedProducts, savedOrderProduct);
	}

}
