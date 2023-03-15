package com.platform.order.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.domain.review.ReviewRepository;
import com.platform.order.review.service.ReviewService;
import com.platform.order.testenv.IntegrationTest;

public class ReviewIntegrationTest extends IntegrationTest {
	@Autowired
	ReviewService reviewService;

	@Autowired
	ReviewRepository reviewRepository;

	@Autowired
	OrderProductRepository orderProductRepository;

	@Autowired
	ProductRepository productRepository;

	OrderProductEntity orderProduct;

	@BeforeEach
	public void setUp() {

		ProductEntity product = productRepository.save(ProductEntity.builder()
			.name("test 상품")
			.quantity(10L)
			.price(1000L)
			.isDisplay(true)
			.build());
		orderProduct = orderProductRepository.save(
			OrderProductEntity.create(product, 3L)
		);
	}

	@AfterEach
	public void setDown() {
		reviewRepository.deleteAllInBatch();
		orderProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("리뷰를 생성하다")
	void testCreate() {
		//given
		CreateReviewRequestDto requestDto = new CreateReviewRequestDto(orderProduct.getId(), 3, "test");
		//when
		CreateReviewResponseDto createReviewResponseDto = reviewService.create(1L, requestDto, List.of());
		//then
		assertThat(createReviewResponseDto.id()).isNotNull();
		assertThat(createReviewResponseDto.reviewImageResponses()).isEmpty();
		assertThat(createReviewResponseDto.userId()).isEqualTo(1L);
		assertThat(createReviewResponseDto.orderProductId()).isEqualTo(requestDto.orderProductId());
		assertThat(createReviewResponseDto.score()).isEqualTo(requestDto.score());
	}
}
