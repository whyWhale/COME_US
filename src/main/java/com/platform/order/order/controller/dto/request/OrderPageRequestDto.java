package com.platform.order.order.controller.dto.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.PositiveOrZero;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.platform.order.common.dto.cursor.CursorPageRequestDto;
import com.platform.order.common.validation.KeywordSearch;
import com.platform.order.order.domain.order.entity.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class OrderPageRequestDto extends CursorPageRequestDto {
	@Schema(description = "최소 상품 가격")
	@PositiveOrZero
	private Long MinimumPrice;

	@Schema(description = "최대 상품 가격")
	@PositiveOrZero
	private Long MaximumPrice;

	@Schema(description = "상품 이름 (최소 2자 이상 입력해야 함)")
	@KeywordSearch
	private String productName;

	@Schema(description = "최소 주문 날짜")
	private LocalDate upperCreatedAt;

	@Schema(description = "최대 주문 날짜")
	private LocalDate lowerCreatedAt;

	@Schema(description = "주문 상태")
	private OrderStatus orderStatus;

	@Schema(description = "정렬 기준")
	private List<OrderProductSort> sorts;

	public OrderPageRequestDto(
		Long lastId,
		Integer size,
		Long minimumPrice,
		Long maximumPrice,
		LocalDate upperCreatedAt,
		LocalDate lowerCreatedAt,
		String productName,
		OrderStatus orderStatus,
		List<OrderProductSort> sorts
	) {
		super(lastId, size);
		this.MinimumPrice = minimumPrice;
		this.MaximumPrice = maximumPrice;
		this.upperCreatedAt = upperCreatedAt;
		this.lowerCreatedAt = lowerCreatedAt;
		this.productName = productName;
		this.orderStatus = orderStatus;
		this.sorts = sorts;
	}

	public Sort toSort() {
		Order defaultOrder = OrderProductSort.getDefault();

		if (this.sorts == null) {
			return Sort.by(defaultOrder);
		}

		List<Order> requestOrders = sorts.stream()
			.map(OrderProductSort::getPageableOrder)
			.toList();
		List<Order> orders = new ArrayList<>(requestOrders);
		orders.add(defaultOrder);

		return Sort.by(orders);
	}

	@Getter
	@AllArgsConstructor
	public enum OrderProductSort {
		CREATED_ASC("createdAt", "asc"),
		CREATED_DESC("createdAt", "desc"),
		PRICE_ASC("price", "asc"),
		PRICE_DESC("price", "desc");

		String property;
		String direction;

		public Order getPageableOrder() {
			return this.direction.equals("desc") ?
				Order.desc(this.getProperty()) : Order.asc(this.getProperty());
		}

		public static Order getDefault() {
			return Order.desc("id");
		}
	}
}
