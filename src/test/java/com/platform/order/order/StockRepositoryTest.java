package com.platform.order.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class StockRepositoryTest {

	@Autowired
	StockRepository stockRepository;

	@Test
	@DisplayName("재고 감소 테스트")
	void testDecreaseStock() {
		//given
		Stock savedStock = stockRepository.save(new Stock(1L, 1L));
		//when
		Integer updateRecord = stockRepository.updateQuantity(savedStock.getProductId(), 1L);
		//then
		Assertions.assertThat(updateRecord).isEqualTo(1);
	}

	@Test
	@DisplayName("재고량 보다 더 많은 재고를 차감하려 할 때, 레코드가 변경되지 않는다.")
	void testFailDecreaseStock() {
		//given
		Stock savedStock = stockRepository.save(new Stock(1L, 1L));
		//when
		Integer updateRecord = stockRepository.updateQuantity(savedStock.getProductId(), 2L);
		//then
		Assertions.assertThat(updateRecord).isZero();
	}

	@Test
	@DisplayName("상품이 존재하지 않는 재고를 차감할 때, 실패한다.")
	void testFailNotExistProduct() {
		//given
		Stock savedStock = stockRepository.save(new Stock(1L, 1L));
		//when
		long absenceId = 30L;
		Integer updateRecord = stockRepository.updateQuantity(absenceId, 2L);
		//then
		Assertions.assertThat(updateRecord).isZero();
	}
}