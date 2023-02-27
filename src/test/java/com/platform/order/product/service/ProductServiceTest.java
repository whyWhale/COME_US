package com.platform.order.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.storage.StorageService;
import com.platform.order.env.ServiceTest;
import com.platform.order.product.controller.dto.request.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.UpdateProductRequestDto;
import com.platform.order.product.domain.entity.CategoryEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.repository.CategoryRepository;
import com.platform.order.product.domain.repository.ProductImageRepository;
import com.platform.order.product.domain.repository.ProductRepository;
import com.platform.order.product.domain.repository.UserProductRepository;
import com.platform.order.product.service.redis.ProductRedisService;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class ProductServiceTest extends ServiceTest {
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
	UserProductRepository userProductRepository;

	@Mock
	ProductRedisService productRedisService;

	@Mock
	ProductMapper productMapper;

	@Mock
	StorageService storageService;

	final Long userId = 1L;
	final Long productId = 1L;
	UserEntity user;
	CategoryEntity category;
	ProductEntity product;

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

		product = ProductEntity.builder()
			.name("test 상품")
			.quantity(10L)
			.price(1000L)
			.owner(user)
			.isDisplay(true)
			.category(category)
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

	@Test
	@DisplayName("상품을 삭제한다.")
	void testDelete() {
		//given
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(productRepository.findByIdWithCategory(any())).willReturn(Optional.of(product));
		//when
		productService.delete(productId, userId);
		//then
		verify(userRepository, times(1)).findById(any());
		verify(productRepository, times(1)).findByIdWithCategory(any());
	}

	@DisplayName("상품 상태를 변경할 때, 상품을 만든 생산자가 아니면 비즈니스 예외가 발생한다. ")
	@Nested
	class NotOwner {
		Long notAuthId = 2L;
		UserEntity notProductCreator = UserEntity.builder()
			.email("anonymous@cocoa.com")
			.username("alooaddod")
			.password("1")
			.nickName("sofattack")
			.role(Role.OWNER)
			.build();

		@DisplayName("상품 수정시, 비즈니스 예외가 발생한다.")
		@Test
		void testUpdateFail() {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("변경될 이름", 10L, 1000L, category.getCode());
			given(userRepository.findById(any())).willReturn(Optional.of(notProductCreator));
			given(productRepository.findById(any())).willReturn(Optional.of(product));
			//when
			//then
			assertThatThrownBy(() -> {
				productService.update(notAuthId, any(), requestDto);
			}).isInstanceOf(BusinessException.class);
		}

		@DisplayName("상품 삭제시, 비즈니스 예외가 발행한다.")
		@Test
		void testDeleteFail() {
			//given
			given(userRepository.findById(2L)).willReturn(Optional.of(notProductCreator));
			given(productRepository.findByIdWithCategory(any())).willReturn(Optional.of(product));
			//when
			//then
			assertThatThrownBy(() -> {
				productService.delete(productId, notAuthId);
			}).isInstanceOf(BusinessException.class);
		}
	}

	@Test
	@DisplayName("상품 상세를 확인한다.")
	void testRead() {
		//given
		given(productRepository.findByIdWithCategoryAndThumbnail(productId)).willReturn(Optional.of(product));
		//whenΩ
		productService.read(productId);
		//then
		verify(productRepository, times(1)).findByIdWithCategoryAndThumbnail(productId);
		verify(imageRepository, times(1)).findByProduct(any());
	}

	@Test
	@DisplayName("상품목록을 조회한다.")
	void testReadAll() {
		//given
		//when
		productService.readAll(any());
		//then
		verify(productRepository, times(1)).findAllWithConditions(any());
	}

	@Test
	@DisplayName("상품을 장바구니에 담다.")
	void testWish() {
		//given
		given(userProductRepository.existsByProductIdAndWisherId(productId, userId)).willReturn(false);
		given(userRepository.findById(userId)).willReturn(Optional.of(user));
		given(productRepository.findByIdWithCategoryAndThumbnail(productId)).willReturn(Optional.of(product));
		doNothing().when(productRedisService).increaseWishCount(productId);
		//when
		productService.wish(productId, userId);
		//then
		verify(userRepository, times(1)).findById(userId);
		verify(userProductRepository, times(1)).existsByProductIdAndWisherId(productId, userId);
		verify(productRepository, times(1)).findByIdWithCategoryAndThumbnail(productId);
		verify(productRedisService, times(1)).increaseWishCount(productId);
	}

	@Test
	@DisplayName("이미 장바구니에 담겨져 있는 상품이라면 비즈니스 예외가 발생한다.")
	void failWishWithAlreadyHaving() {
		//given
		given(userProductRepository.existsByProductIdAndWisherId(productId, userId)).willReturn(false);
		//when
		//then
		assertThatThrownBy(() -> {
			productService.wish(productId, userId);
		}).isInstanceOf(BusinessException.class);
	}
}