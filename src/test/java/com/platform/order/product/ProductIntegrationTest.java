package com.platform.order.product;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.platform.order.product.domain.entity.CategoryEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.respository.CategoryRepository;
import com.platform.order.product.domain.respository.ProductRepository;
import com.platform.order.product.service.ProductService;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.request.UpdateProductRequestDto;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;
import com.platform.order.product.web.dto.response.UpdateProductResponseDto;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

@SpringBootTest
public class ProductIntegrationTest {

	@Autowired
	ProductService productService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;

	UserEntity user;
	CategoryEntity category;

	@BeforeEach
	public void setUp() {
		user = userRepository.save(UserEntity
			.builder()
			.email("productInegration@google.com")
			.username("productIntegration")
			.nickName("pro")
			.role(Role.USER)
			.password("1")
			.build());

		category = categoryRepository.save(
			CategoryEntity.builder()
				.name("test 카테고리")
				.code("C032")
				.build()
		);
	}

	@AfterEach
	public void finalSetUp(){
		productRepository.deleteAll();
		categoryRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	@DisplayName("상품을 생성한다.")
	void testCreate() {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, category.getCode());
		//when
		CreateProductResponseDto productResponseDto = productService.create(user.getId(), requestDto);
		//then
		Assertions.assertThat(productResponseDto.categoryCode()).isEqualTo(requestDto.categoryCode());
		Assertions.assertThat(productResponseDto.name()).isEqualTo(requestDto.name());
		Assertions.assertThat(productResponseDto.price()).isEqualTo(requestDto.price());
		Assertions.assertThat(productResponseDto.quantity()).isEqualTo(requestDto.quantity());
	}

	@Test
	@DisplayName("상품을 수정한다.")
	void testUpdate() {
		//given
		ProductEntity savedProduct = productRepository.save(ProductEntity.builder()
			.name("test 상품")
			.category(category)
			.owner(user)
			.quantity(10L)
			.price(1000L)
			.isDisplay(true)
			.build());
		UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 이름 변경", 10000L, 500L,
			category.getCode());
		//when
		UpdateProductResponseDto productResponseDto = productService.update(user.getId(), savedProduct.getId(),requestDto);
		//then
		Assertions.assertThat(productResponseDto.categoryCode()).isEqualTo(requestDto.categoryCode());
		Assertions.assertThat(productResponseDto.name()).isEqualTo(requestDto.name());
		Assertions.assertThat(productResponseDto.price()).isEqualTo(requestDto.price());
		Assertions.assertThat(productResponseDto.quantity()).isEqualTo(requestDto.quantity());
	}
}
