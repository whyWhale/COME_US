package com.platform.order.product.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.product.domain.userproduct.repository.UserProductRepository;
import com.platform.order.testenv.RepositoryTest;
import com.platform.order.user.domain.entity.Role;
import com.platform.order.user.domain.entity.UserEntity;
import com.platform.order.user.domain.repository.UserRepository;

class CustomUserProductRepositoryTest extends RepositoryTest {

	@Autowired
	UserProductRepository userProductRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CategoryRepository categoryRepository;

	UserEntity user;
	List<ProductEntity> products;
	List<UserProductEntity> userProducts;

	@BeforeEach
	public void setUp() {
		user = userRepository.save(UserEntity.builder()
			.nickName("kaidoudou")
			.email("kaka@kanil.com")
			.username("kabonnnon")
			.password("1")
			.role(Role.USER)
			.build());

		CategoryEntity category = categoryRepository.save(CategoryEntity.builder()
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
					.path("storage path" + value)
					.originName("모의 섬네일")
					.build());

				return product;
			}
		).toList();

		productRepository.saveAll(products);

		List<ProductEntity> products = LongStream.rangeClosed(1, 25).mapToObj(value -> {
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
					.path("storage path" + value)
					.originName("모의 섬네일")
					.build());

				return product;
			}
		).toList();

		userProducts = this.products.stream().map(product -> UserProductEntity.builder()
				.wisher(user)
				.product(product)
				.build()).
			toList();

		userProductRepository.saveAll(userProducts);
	}

	@Test
	@DisplayName("130개의 상품이 담긴 장바구니 목록을 조회한다.")
	void testFindAllWithConditions() {
		//given
		int requestPage = 1;
		int requestSize = 10;
		var pageRequest = new WishUserProductPageRequestDto(requestPage, requestSize, null, null,
			List.of(WishUserProductPageRequestDto.UserProductOrder.CREATED_DESC));
		int expectedTotalPage = (int)Math.ceil(userProducts.size() / (double)requestSize);
		//when
		Page<UserProductEntity> page = userProductRepository.findAllWithCondtions(user.getId(), pageRequest);
		//then
		assertThat(page.getTotalPages()).isEqualTo(expectedTotalPage);
		assertThat(page.getPageable().getPageNumber()).isEqualTo(requestPage);
		assertThat(page.getPageable().getPageSize()).isEqualTo(requestSize);
		page.getContent().forEach(userProduct -> assertThat(userProduct.getWisher()).isEqualTo(user));
	}
}