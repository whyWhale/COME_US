package com.platform.order.review;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.controller.dto.request.UpdateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.domain.review.entity.ReviewEntity;
import com.platform.order.review.domain.review.repository.ReviewRepository;
import com.platform.order.review.domain.reviewimage.ReviewImageRepository;
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

	@Autowired
	ReviewImageRepository reviewImageRepository;

	ResourceLoader resourceLoader = new DefaultResourceLoader();
	OrderProductEntity orderProduct;
	MultipartFile mockFile;
	ProductEntity product;

	@BeforeEach
	public void setUp() throws IOException {
		String fileName = "test.png";
		Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
		mockFile = new MockMultipartFile("file", fileName, "image/png", resource.getInputStream());

		product = productRepository.save(ProductEntity.builder()
			.name("test 상품")
			.quantity(1000L)
			.price(1000L)
			.isDisplay(true)
			.build());
		orderProduct = orderProductRepository.save(
			OrderProductEntity.create(product, 3L)
		);
	}

	@AfterEach
	public void setDown() {
		reviewImageRepository.deleteAllInBatch();
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

	@Test
	@DisplayName("리뷰를 수정한다")
	void testUpdate() {
		//given
		long authId = 3L;
		ReviewEntity savedReview = reviewRepository.save(
			ReviewEntity.builder()
				.userId(authId)
				.orderProduct(orderProduct)
				.content("test")
				.score(4)
				.build()
		);
		UpdateReviewRequestDto updateRequestDto = new UpdateReviewRequestDto(orderProduct.getId(), 1, "test update");
		List<MultipartFile> requestMultipartFiles = List.of(mockFile);
		//when
		var responseDto = reviewService.update(
			savedReview.getUserId(),
			savedReview.getId(),
			updateRequestDto,
			requestMultipartFiles
		);
		//then
		assertThat(responseDto.score()).isEqualTo(updateRequestDto.score());
		assertThat(responseDto.content()).isEqualTo(updateRequestDto.content());
		assertThat(responseDto.reviewImageResponses().size()).isEqualTo(1);
	}

	@Test
	@DisplayName("상품에 해당하는 리뷰를 조회한다")
	void testReadAll() {
		//given
		List<OrderProductEntity> orderProducts = orderProductRepository.saveAll(
			LongStream.rangeClosed(1, 20)
				.mapToObj(value -> OrderProductEntity.create(product, 2L))
				.toList()
		);
		List<ReviewEntity> reviewEntities = IntStream.range(0, 20)
			.mapToObj(val -> ReviewEntity.builder()
				.userId((long)val)
				.orderProduct(orderProducts.get(val))
				.content("test" + val)
				.score(val % 5)
				.build())
			.toList();
		reviewRepository.saveAll(reviewEntities);

		int requestPage = 1;
		int requestSize = 10;
		int expectedTotalPage = (int)Math.ceil((double)reviewEntities.size() / requestSize);
		ReviewPageRequestDto pageRequest = new ReviewPageRequestDto(requestPage, requestSize, null, null);
		//when
		var pageResponse = reviewService.readAll(product.getId(), pageRequest);
		//then
		assertThat(pageResponse.getTotalPage()).isEqualTo(expectedTotalPage);
		assertThat(pageResponse.getPage()).isEqualTo(requestPage);
		assertThat(pageResponse.getSize()).isEqualTo(requestSize);
	}
}
