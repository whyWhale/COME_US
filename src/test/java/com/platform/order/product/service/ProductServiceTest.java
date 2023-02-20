package com.platform.order.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.platform.order.common.storage.StorageService;
import com.platform.order.product.domain.entity.CategoryEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.respository.CategoryRepository;
import com.platform.order.product.domain.respository.ProductImageRepository;
import com.platform.order.product.domain.respository.ProductRepository;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.request.UpdateProductRequestDto;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
	@InjectMocks
	ProductService productService;
	@Mock
	UserRepository userRepository;
	@Mock
	ProductRepository productRepository;
	@Mock
	CategoryRepository categoryRepository;
	@Mock
	ProductImageRepository imageRepository;
	@Mock
	ProductMapper productMapper;
	@Mock
	StorageService storageService;

	final Long userId = 1L;
	UserEntity user;
	CategoryEntity category;

	@BeforeEach
	public void setUp() {
		user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.OWNER)
			.build();

		category = CategoryEntity.builder()
			.name("아우터")
			.code("C032")
			.build();

	}

	@Test
	@DisplayName("상품을 생성한다.")
	void testCreate() {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto(
			"test 상품", 10L, 100L, category.getCode()
		);

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(categoryRepository.findByCode(any())).willReturn(Optional.of(category));
		//when
		productService.create(userId, requestDto);
		//then
		verify(userRepository, times(1)).findById(any());
		verify(categoryRepository, times(1)).findByCode(any());
		verify(productRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("상품을 수정한다.")
	void testUpdate() {
		//given
		long price = 1000L;
		long quantity = 10L;
		UpdateProductRequestDto requestDto = new UpdateProductRequestDto("변경될 이름", quantity + 5, price,
			category.getCode());
		ProductEntity savedProduct = ProductEntity.builder()
			.name("test 상품")
			.quantity(quantity)
			.price(price)
			.owner(user)
			.isDisplay(true)
			.category(category)
			.build();

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(productRepository.findById(any())).willReturn(Optional.of(savedProduct));
		given(categoryRepository.findByCode(any())).willReturn(Optional.of(category));

		//when
		productService.update(userId, any(), requestDto);
		//then

		assertThat(savedProduct.getName()).isEqualTo(requestDto.name());
		assertThat(savedProduct.getPrice()).isEqualTo(requestDto.price());
		assertThat(savedProduct.getQuantity()).isEqualTo(requestDto.quantity());
		assertThat(savedProduct.getCategory().getCode()).isEqualTo(requestDto.categoryCode());
	}

}