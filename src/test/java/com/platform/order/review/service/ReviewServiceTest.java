package com.platform.order.review.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.storage.AwsStorageService;
import com.platform.order.common.storage.response.UploadFileResponseDto;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.domain.review.ReviewEntity;
import com.platform.order.review.domain.review.ReviewRepository;
import com.platform.order.testenv.ServiceTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;

class ReviewServiceTest extends ServiceTest {
	@InjectMocks
	ReviewService reviewService;

	@Mock
	ReviewRepository reviewRepository;

	@Mock
	OrderProductRepository orderProductRepository;

	@Mock
	AwsStorageService awsStorageService;

	@Spy
	ReviewMapper reviewMapper;

	ResourceLoader resourceLoader = new DefaultResourceLoader();

	MultipartFile mockFile;
	List<MultipartFile> mockFiles;
	OrderProductEntity orderProduct;

	@BeforeEach
	public void setUp() throws IOException {
		String fileName = "test.png";
		Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
		mockFile = new MockMultipartFile("file", fileName, null, resource.getInputStream());
		mockFiles = Stream.generate(() -> mockFile).limit(1).toList();

		UserEntity user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.OWNER)
			.build();

		ProductEntity product = ProductEntity.builder()
			.name("test 상품")
			.quantity(10L)
			.price(1000L)
			.owner(user)
			.isDisplay(true)
			.build();

		orderProduct = OrderProductEntity.create(product, 3L);

	}

	@Test
	@DisplayName("리뷰를 생성한다.")
	void testCreate() {
		//given
		List<UploadFileResponseDto> uploadFileResponses = List.of(
			new UploadFileResponseDto(
				mockFile.getOriginalFilename(),
				"testPath",
				"png",
				mockFile
			));
		ReviewEntity expectedReview = ReviewEntity.builder()
			.userId(1L)
			.score(3)
			.orderProduct(orderProduct)
			.content("test")
			.build();
		CreateReviewRequestDto createReviewRequest = new CreateReviewRequestDto(1L, 3, "review 글 작성");
		given(orderProductRepository.findById(any())).willReturn(Optional.of(orderProduct));
		given(awsStorageService.upload(any(), any())).willReturn(uploadFileResponses);
		given(reviewRepository.save(any())).willReturn(expectedReview);
		//when
		reviewService.create(any(), createReviewRequest, List.of(mockFile));
		//then
		verify(orderProductRepository, times(1)).findById(any());
		verify(awsStorageService, times(1)).upload(any(), any());
		verify(reviewRepository, times(1)).save(any());
	}
}