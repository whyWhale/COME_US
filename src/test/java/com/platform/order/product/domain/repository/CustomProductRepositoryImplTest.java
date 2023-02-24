package com.platform.order.product.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.platform.order.env.RepositoryTest;
import com.platform.order.product.controller.dto.request.ProductPageRequestDto;
import com.platform.order.product.domain.entity.CategoryEntity;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.entity.ProductThumbnailEntity;

class CustomProductRepositoryImplTest extends RepositoryTest {

	@Autowired
	ProductRepository productRepository;
	@Autowired
	CategoryRepository categoryRepository;

	List<ProductEntity> products;

	CategoryEntity category;

	@BeforeEach
	public void setUp() {
		category = categoryRepository.save(CategoryEntity.builder()
			.code("C0323")
			.name("testCategory")
			.build());

		products = LongStream.rangeClosed(1, 130).mapToObj(value -> {
				ProductEntity product = ProductEntity.builder()
					.name("test상품" + value)
					.price(value)
					.quantity(value)
					.isDisplay(true)
					.category(category)
					.build();

				product.addThumbnail(ProductThumbnailEntity.builder()
					.name(UUID.randomUUID().toString())
					.extension("png")
					.size(100023L)
					.path("storage path")
					.originName("모의 섬네일")
					.build());

				return product;
			}
		).toList();

		productRepository.saveAll(products);
	}

	@AfterEach
	public void setDown() {
		productRepository.deleteAllInBatch();
	}

	@Test
	@DisplayName("상품목록 20개를 가져온다.")
	void testFindAll() {
		// given
		int page = 1;
		int pageSize = 20;
		int expectedTotalPage = (int)Math.ceil((double)products.size() / pageSize);
		var productPageRequestDto = new ProductPageRequestDto(page, pageSize, null, null, null,
			List.of(ProductPageRequestDto.ProductCondition.CREATED_DESC,
				ProductPageRequestDto.ProductCondition.CREATED_ASC));
		// when
		Page<ProductEntity> productPage = productRepository.findAllWithConditions(productPageRequestDto);
		Pageable pageable = productPage.getPageable();
		// then
		assertThat(productPage.getTotalElements()).isEqualTo(products.size());
		assertThat(productPage.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(pageable.getPageSize()).isEqualTo(pageSize);
	}

	@Test
	@DisplayName("상품목록을 10개씩, name이 가리온으로 시작하는 상품들을 가져온다.")
	void testFindAllWithNameConditions() {
		// given
		int page = 1;
		int pageSize = 10;
		var productPageRequestDto = new ProductPageRequestDto(page, pageSize, "가리온", null, null, null);
		List<ProductEntity> plusProducts = LongStream.rangeClosed(1, 30).mapToObj(value -> {
			ProductEntity product = ProductEntity.builder()
				.name("가리온상품" + value)
				.price(value)
				.quantity(value)
				.isDisplay(true)
				.category(category)
				.build();

			product.addThumbnail(ProductThumbnailEntity.builder()
				.name(UUID.randomUUID().toString())
				.extension("png")
				.size(100023L)
				.path("storage path")
				.originName("모의 섬네일")
				.build());

			return product;
		}).toList();
		int expectedTotalPage = (int)Math.ceil((double)plusProducts.size() / pageSize);

		productRepository.saveAll(plusProducts);

		// when
		Page<ProductEntity> productPage = productRepository.findAllWithConditions(productPageRequestDto);
		Pageable pageable = productPage.getPageable();
		// then
		assertThat(productPage.getTotalElements()).isEqualTo(plusProducts.size());
		assertThat(productPage.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(pageable.getPageSize()).isEqualTo(pageSize);
	}

	@Test
	@DisplayName("100원 이상 130원 이하인 상품을 10개씩 가져온다.")
	void testFindAllWithLessThanPriceConditions() {
		//given
		int page = 1;
		int pageSize = 10;
		long minimumPrice = 100L;
		long maximumPrice = 130L;
		long expectedProductSize = maximumPrice - minimumPrice + 1;
		var productPageRequestDto = new ProductPageRequestDto(page, pageSize, null, maximumPrice, minimumPrice,
			null);
		int expectedTotalPage = (int)Math.ceil(expectedProductSize / (double)pageSize);
		//when
		Page<ProductEntity> productPage = productRepository.findAllWithConditions(productPageRequestDto);
		Pageable pageable = productPage.getPageable();
		// then
		assertThat(productPage.getTotalElements()).isEqualTo(expectedProductSize);
		assertThat(productPage.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(pageable.getPageSize()).isEqualTo(pageSize);
	}
}