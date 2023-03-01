package com.platform.order.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.DeleteProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.WishUserProductResponseDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.product.domain.userproduct.repository.UserProductRepository;
import com.platform.order.product.service.ProductService;
import com.platform.order.testenv.IntegrationTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

public class ProductIntegrationTest extends IntegrationTest {

	@Autowired
	ProductService productService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	UserProductRepository userProductRepository;

	UserEntity user;
	CategoryEntity category;
	ProductEntity product;

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

		ProductEntity productEntity = ProductEntity.builder()
			.name("test 상품")
			.category(category)
			.owner(user)
			.quantity(10L)
			.price(1000L)
			.isDisplay(true)
			.build();

		productEntity.addThumbnail(ProductThumbnailEntity.builder()
			.name(UUID.randomUUID().toString())
			.extension("png")
			.size(100023L)
			.path("storage path")
			.originName("모의 섬네일")
			.build());

		product = productRepository.save(productEntity);
	}

	@AfterEach
	public void finalSetUp() {
		userProductRepository.deleteAllInBatch();
		productRepository.deleteAllInBatch();
		categoryRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("상품을 생성한다.")
	void testCreate() {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, category.getCode());
		//when
		CreateProductResponseDto productResponseDto = productService.create(user.getId(), requestDto);
		//then
		assertThat(productResponseDto.categoryCode()).isEqualTo(requestDto.categoryCode());
		assertThat(productResponseDto.name()).isEqualTo(requestDto.name());
		assertThat(productResponseDto.price()).isEqualTo(requestDto.price());
		assertThat(productResponseDto.quantity()).isEqualTo(requestDto.quantity());
	}

	@Test
	@DisplayName("상품을 수정한다.")
	void testUpdate() {
		//given
		UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 이름 변경", 10000L, 500L,
			category.getCode());
		//when
		UpdateProductResponseDto productResponseDto = productService.update(user.getId(), product.getId(), requestDto);
		//then
		assertThat(productResponseDto.categoryCode()).isEqualTo(requestDto.categoryCode());
		assertThat(productResponseDto.name()).isEqualTo(requestDto.name());
		assertThat(productResponseDto.price()).isEqualTo(requestDto.price());
		assertThat(productResponseDto.quantity()).isEqualTo(requestDto.quantity());
	}

	@Test
	@DisplayName("상품을 삭제한다.")
	void testDelete() {
		//given
		//when
		DeleteProductResponseDto deleteProductResponseDto = productService.delete(product.getId(), user.getId());
		//then
		assertThat(deleteProductResponseDto.name()).isEqualTo(product.getName());
		assertThat(deleteProductResponseDto.quantity()).isEqualTo(product.getQuantity());
		assertThat(deleteProductResponseDto.price()).isEqualTo(product.getPrice());
		assertThat(deleteProductResponseDto.isDisplay()).isFalse();
		assertThat(deleteProductResponseDto.categoryCode()).isEqualTo(category.getCode());
		assertThat(deleteProductResponseDto.categoryName()).isEqualTo(category.getName());
	}

	@Test
	@DisplayName("상세 상품을 확인한다.")
	void testRead() {
		//given
		//when
		var readProductResponseDto = productService.read(product.getId(), UUID.randomUUID().toString());
		//then
		assertThat(readProductResponseDto.name()).isEqualTo(product.getName());
		assertThat(readProductResponseDto.quantity()).isEqualTo(product.getQuantity());
		assertThat(readProductResponseDto.price()).isEqualTo(product.getPrice());
	}

	@Test
	@DisplayName("상품을 장바구니에 담다.")
	void testWish() {
		//given
		//when
		WishUserProductResponseDto wishProductResponse = productService.wish(product.getId(), user.getId());
		//then
		assertThat(wishProductResponse.categoryCode()).isEqualTo(category.getCode());
		assertThat(wishProductResponse.categoryName()).isEqualTo(category.getName());
		assertThat(wishProductResponse.productId()).isEqualTo(product.getId());
		assertThat(wishProductResponse.productName()).isEqualTo(product.getName());
		assertThat(wishProductResponse.price()).isEqualTo(product.getPrice());
		assertThat(wishProductResponse.thumbnailPath()).isEqualTo(product.getProductThumbnail().getPath());
		assertThat(wishProductResponse.isDisplay()).isEqualTo(product.isDisplay());
	}

	@Test
	@DisplayName("장바구니 목록 중 하나를 삭제한다.")
	void testDeleteWishProduct() {
		//given
		UserProductEntity wishProduct = userProductRepository.save(
			UserProductEntity.builder()
				.wisher(user)
				.product(product)
				.build());
		//when
		Long deleteId = productService.deleteWishProduct(user.getId(), wishProduct.getId());
		//then
		assertThat(deleteId).isEqualTo(wishProduct.getId());
	}
}
