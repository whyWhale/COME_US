package com.platform.order.order.domain.orderproduct.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.testenv.RepositoryTest;

@Sql(scripts = "classpath:/load/order_product.sql")
class OrderProductRepositoryTest extends RepositoryTest {
	@Autowired
	OrderProductRepository orderProductRepository;

	@Test
	@DisplayName("나의 주문 목록을 가져온다.")
	void testFindAllWithConditions() {
		//given
		int requestSize = 10;
		OrderPageRequestDto orderPageRequestDto = new OrderPageRequestDto(
			null,
			requestSize,
			null,
			null,
			null,
			null,
			null,
			null,
			List.of(OrderPageRequestDto.OrderProductSort.CREATED_DESC)
		);
		//when
		var myOrderProducts = orderProductRepository.findMyAllWithConditions(1L, orderPageRequestDto);
		//then
		Assertions.assertThat(myOrderProducts).hasSize(requestSize);
	}

	@Test
	@DisplayName("나의 주문 목록 다음을 가져온다.")
	void testFindAll2PageWithConditions() {
		//given
		int requestSize = 10;
		OrderPageRequestDto orderPageRequestDto = new OrderPageRequestDto(
			19L,
			requestSize,
			null,
			null,
			null,
			null,
			null,
			null,
			List.of(OrderPageRequestDto.OrderProductSort.CREATED_DESC)
		);
		//when
		var myOrderProducts = orderProductRepository.findMyAllWithConditions(1L, orderPageRequestDto);
		//then
		Assertions.assertThat(myOrderProducts).hasSize(requestSize);
	}
}