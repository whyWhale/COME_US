package com.platform.order.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.exception.custom.BusinessException;
import com.platform.order.common.storage.AwsStorageService;
import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.category.repository.CategoryRepository;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.domain.productimage.repository.ProductImageRepository;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.product.domain.userproduct.repository.UserProductRepository;
import com.platform.order.product.service.redis.ProductRedisService;
import com.platform.order.testenv.ServiceTest;
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

	@Spy
	ProductMapper productMapper;

	@Mock
	AwsStorageService awsStorageService;

	ResourceLoader resourceLoader = new DefaultResourceLoader();

	UserEntity user;
	UserEntity notOwner;
	CategoryEntity category;
	ProductEntity product;
	MultipartFile mockfile;
	List<MultipartFile> mockfiles;

	@BeforeEach
	public void setUp() throws IOException {
		user = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.OWNER)
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		notOwner = UserEntity.builder()
			.email("whywhale@cocoa.com")
			.username("whyWhale")
			.password("1")
			.nickName("whale")
			.role(Role.OWNER)
			.build();
		ReflectionTestUtils.setField(notOwner, "id", 2L);

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

		product.addThumbnail(ProductThumbnailEntity.builder()
			.fileName(UUID.randomUUID().toString())
			.originName("test")
			.extension("png")
			.path("path")
			.size(100233L)
			.build());

		ReflectionTestUtils.setField(product, "id", 1L);

		String fileName = "test.png";
		Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
		mockfile = new MockMultipartFile("file", fileName, null, resource.getInputStream());
		mockfiles = Stream.generate(() -> mockfile).limit(5).toList();
	}

	@Test
	@DisplayName("상품을 생성한다.")
	void testCreate() {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 100L, category.getCode());

		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(categoryRepository.findByCode(any())).willReturn(Optional.of(category));
		given(productRepository.save(any())).willReturn(product);
		//when
		productService.create(user.getId(), requestDto);
		//then
		verify(userRepository, times(1)).findById(any());
		verify(categoryRepository, times(1)).findByCode(any());
		verify(productRepository, times(1)).save(any());
	}

	@Test
	@DisplayName("상품 섬네일을 생성한다.")
	void testCreateThumbnail() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(awsStorageService.upload(any(), any(), any(), any())).willReturn("uploadPath");
		//when
		productService.createThumbnail(product.getId(), user.getId(), mockfile);
		//then
		assertThat(product.getProductThumbnail().getOriginName()).isEqualTo(mockfile.getOriginalFilename());
		verify(productRepository, times(1)).findById(any());
		verify(userRepository, times(1)).findById(any());
		verify(awsStorageService, times(1)).upload(any(), any(), any(), any());
	}

	@Test
	@DisplayName("상품 섬네일을 생성할 때, 상품을 만든 주인이 아니면 비즈니스 예외가 발생한다")
	void failCreateThumbnailNotSameOwner() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(notOwner));
		//when
		//then
		assertThatThrownBy(
			() -> productService.createThumbnail(product.getId(), notOwner.getId(), mockfile)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("상품 이미지을 생성한다.")
	void testCreateImages() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(awsStorageService.upload(any(), any(), any(), any())).willReturn("uploadPath");
		//when
		productService.createImages(product.getId(), user.getId(), mockfiles);
		//then
		verify(productRepository, times(1)).findById(any());
		verify(userRepository, times(1)).findById(any());
		verify(awsStorageService, times(mockfiles.size())).upload(any(), any(), any(), any());
	}

	@Test
	@DisplayName("상품 이미지를 생성할 때, 상품을 만든 주인이 아니면 비즈니스 예외가 발생한다")
	void failCreateImagesNotSameOwner() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(notOwner));
		//when
		//then
		assertThatThrownBy(
			() -> productService.createImages(product.getId(), notOwner.getId(), mockfiles)
		).isInstanceOf(BusinessException.class);
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
		productService.update(user.getId(), any(), requestDto);
		//then
		assertThat(savedProduct.getName()).isEqualTo(requestDto.name());
		assertThat(savedProduct.getPrice()).isEqualTo(requestDto.price());
		assertThat(savedProduct.getQuantity()).isEqualTo(requestDto.quantity());
		assertThat(savedProduct.getCategory().getCode()).isEqualTo(requestDto.categoryCode());
	}

	@Test
	@DisplayName("상품 섬네일을 수정한다.")
	void testUpdateThumbnail() {
		//given
		product.addThumbnail(ProductThumbnailEntity.builder().build());

		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(awsStorageService.delete(any(), any())).willReturn("deletePath");
		given(awsStorageService.upload(any(), any(), any(), any())).willReturn("uploadPath");
		//when
		productService.updateThumbnail(product.getId(), user.getId(), mockfile);
		//then
		assertThat(product.getProductThumbnail().getOriginName()).isEqualTo(mockfile.getOriginalFilename());
		verify(productRepository, times(1)).findById(any());
		verify(awsStorageService, times(1)).upload(any(), any(), any(), any());
		verify(awsStorageService, times(1)).delete(any(), any());
	}

	@Test
	@DisplayName("상품 섬네일을 수정할 때, 상품을 만든 주인이 아니면 비즈니스 예외가 발생한다")
	void failUpdateThumbnailNotSameOwner() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(notOwner));
		//when
		//then
		assertThatThrownBy(
			() -> productService.updateThumbnail(product.getId(), notOwner.getId(), mockfile)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("상품 이미지를 수정한다.")
	void testUpdateImages() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(awsStorageService.upload(any(), any(), any(), any())).willReturn("uploadPath");
		//when
		productService.updateImages(product.getId(), user.getId(), mockfiles);
		//then
		verify(productRepository, times(1)).findById(any());
		verify(userRepository, times(1)).findById(any());
		verify(awsStorageService, times(mockfiles.size())).upload(any(), any(), any(), any());
	}

	@Test
	@DisplayName("상품 이미지를 수정할 때, 상품을 만든 주인이 아니면 비즈니스 예외가 발생한다")
	void failUpdateImagesNotSameOwner() {
		//given
		given(productRepository.findById(any())).willReturn(Optional.of(product));
		given(userRepository.findById(any())).willReturn(Optional.of(notOwner));
		//when
		//then
		assertThatThrownBy(
			() -> productService.updateImages(product.getId(), notOwner.getId(), mockfiles)
		).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("상품을 삭제한다.")
	void testDelete() {
		//given
		given(userRepository.findById(any())).willReturn(Optional.of(user));
		given(productRepository.findByIdWithCategory(any())).willReturn(Optional.of(product));
		//when
		productService.delete(product.getId(), user.getId());
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
				productService.delete(product.getId(), notAuthId);
			}).isInstanceOf(BusinessException.class);
		}
	}

	@Test
	@DisplayName("상품 상세를 확인한다.")
	void testRead() {
		//given
		given(productRepository.findByIdWithCategoryAndThumbnail(product.getId())).willReturn(Optional.of(product));
		//whenΩ
		productService.read(product.getId(), any());
		//then
		verify(productRepository, times(1)).findByIdWithCategoryAndThumbnail(product.getId());
		verify(imageRepository, times(1)).findByProduct(any());
	}

	@Test
	@DisplayName("상품목록을 조회한다.")
	void testReadAll() {
		//given
		var pageRequestDto = new ProductPageRequestDto(1, 10, null, null, null, null);
		PageImpl<ProductEntity> page = new PageImpl<>(List.of(), pageRequestDto.toPageable(), 0);

		given(productRepository.findAllWithConditions(any())).willReturn(page);
		//when
		productService.readAll(pageRequestDto);
		//then
		verify(productRepository, times(1)).findAllWithConditions(any());
	}

	@Test
	@DisplayName("상품을 장바구니에 담다.")
	void testWish() {
		//given
		UserProductEntity expectedUserProduct = UserProductEntity.builder()
			.id(1L)
			.wisher(user)
			.product(product)
			.build();
		given(userProductRepository.existsByProductIdAndWisherId(product.getId(), user.getId())).willReturn(false);
		given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
		given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
		given(userProductRepository.save(any())).willReturn(expectedUserProduct);
		doNothing().when(productRedisService).increaseWishCount(product.getId());
		//when
		Long wishProductId = productService.wishProduct(product.getId(), user.getId());
		//then
		assertThat(wishProductId).isEqualTo(product.getId());
		verify(userProductRepository, times(1)).existsByProductIdAndWisherId(product.getId(), user.getId());
		verify(userRepository, times(1)).findById(user.getId());
		verify(productRepository, times(1)).findById(product.getId());
		verify(productRedisService, times(1)).increaseWishCount(product.getId());
	}

	@Test
	@DisplayName("이미 장바구니에 담겨져 있는 상품이라면 비즈니스 예외가 발생한다.")
	void failWishWithAlreadyHaving() {
		//given
		given(userProductRepository.existsByProductIdAndWisherId(product.getId(), user.getId())).willReturn(true);
		//when
		//then
		assertThatThrownBy(() -> {
			productService.wishProduct(product.getId(), user.getId());
		}).isInstanceOf(BusinessException.class);
	}

	@Test
	@DisplayName("장바구니에 있는 상품 목록을 조회한다.")
	void testReadAllWishProducts() {
		//given
		PageImpl<UserProductEntity> page = new PageImpl<>(List.of(), PageRequest.ofSize(10), 10);

		given(userProductRepository.findAllWithCondtions(any(), any())).willReturn(page);
		//when
		productService.readAllWishProducts(any(), any());
		//then
		verify(userProductRepository, times(1)).findAllWithCondtions(any(), any());
	}

	@Test
	@DisplayName("장바구니에 담긴 상품을 삭제한다.")
	void testDeleteWishProduct() {
		//given
		UserProductEntity wishUserProduct = UserProductEntity.builder()
			.id(1L)
			.wisher(user)
			.product(product)
			.build();
		given(userProductRepository.findByIdAndWisherId(wishUserProduct.getId(), user.getId())).willReturn(
			Optional.of(wishUserProduct));
		doNothing().when(userProductRepository).delete(wishUserProduct);
		doNothing().when(productRedisService).decreaseWishCount(wishUserProduct.getProduct().getId());
		//when
		productService.unWishProduct(user.getId(), wishUserProduct.getId());
		//then
		verify(userProductRepository, times(1)).findByIdAndWisherId(wishUserProduct.getId(), user.getId());
		verify(userProductRepository, times(1)).delete(wishUserProduct);
		verify(productRedisService, times(1)).decreaseWishCount(wishUserProduct.getProduct().getId());
	}

	@Test
	@DisplayName("찜이 가장 많은 상품 10개를 조회한다")
	void testGetMaximumWishProducts() {
		//given
		given(productRedisService.getMaximumWishProducts()).willReturn(List.of());
		//when
		productService.getMaximumWishProducts();
		//then
		verify(productRedisService,times(1)).getMaximumWishProducts();
	}

	@Test
	@DisplayName("조회수가 가장 많은 상품 10개를 조회한다")
	void testGetMaximumReadProducts() {
		//given
		given(productRedisService.getMaximumReadProducts()).willReturn(List.of());
		//when
		productService.getMaximumReadProducts();
		//then
		verify(productRedisService,times(1)).getMaximumReadProducts();
	}
}