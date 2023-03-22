package com.platform.order.product.service;

import static com.platform.order.product.controller.dto.response.product.RankingRegionOrderProductResponseDto.*;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.dto.offset.PageResponseDto;
import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.controller.dto.response.product.CreateProductImagesResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingRegionOrderProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingWishProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductImageResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.ReadAllUserProductResponseDto;
import com.platform.order.product.domain.category.entity.CategoryEntity;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.productimage.entity.ProductImageEntity;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;
import com.platform.order.user.domain.entity.UserEntity;

@Component
public class ProductMapper {
	public CreateProductResponseDto toCreateProductResponseDto(ProductEntity product) {
		return new CreateProductResponseDto(
			product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode());
	}

	public CreateThumbnailResponseDto toCreateThumbnailResponse(ProductThumbnailEntity thumbnail) {
		return new CreateThumbnailResponseDto(thumbnail.getFileName(), thumbnail.getOriginName(),
			thumbnail.getExtension(),
			thumbnail.getPath(), thumbnail.getSize());
	}

	public List<CreateProductImagesResponseDto> toCreateProductImagesResponses(
		List<ProductImageEntity> productImages) {
		return productImages.stream()
			.map(productImageEntity -> new CreateProductImagesResponseDto(
				productImageEntity.getFileName(),
				productImageEntity.getOriginName(),
				productImageEntity.getExtension(),
				productImageEntity.getPath(),
				productImageEntity.getSize(),
				productImageEntity.getArrangement()
			))
			.toList();
	}

	public List<UpdateProductImageResponseDto> toUpdateProductImageResponseDtos(
		List<ProductImageEntity> productImageEntities) {
		return productImageEntities.stream()
			.map(productImageEntity -> new UpdateProductImageResponseDto(
				productImageEntity.getFileName(),
				productImageEntity.getOriginName(),
				productImageEntity.getExtension(),
				productImageEntity.getPath(),
				productImageEntity.getSize(),
				productImageEntity.getArrangement()
			))
			.toList();
	}

	public UpdateProductThumbnailResponseDto toUpdateProductThumbnailResponse(ProductThumbnailEntity newThumbnail) {
		return new UpdateProductThumbnailResponseDto(
			newThumbnail.getFileName(),
			newThumbnail.getOriginName(),
			newThumbnail.getExtension(),
			newThumbnail.getPath(),
			newThumbnail.getSize()
		);
	}

	public UpdateProductResponseDto toUpdateProductResponseDto(ProductEntity product) {
		return new UpdateProductResponseDto(
			product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode());
	}

	public ReadProductResponseDto toReadProductResponseDto(
		ProductEntity foundProduct,
		List<ProductImageEntity> images,
		long wishCount) {

		List<String> imagePaths = images.stream()
			.map(ProductImageEntity::getPath)
			.toList();

		return new ReadProductResponseDto(
			foundProduct.getName(),
			foundProduct.getQuantity(),
			foundProduct.getPrice(),
			foundProduct.getProductThumbnail().getPath(),
			foundProduct.getCategory().getName(),
			imagePaths,
			wishCount);
	}

	public PageResponseDto<ReadAllProductResponseDto> toContentPageResponseDto(
		Page<ProductEntity> productsPage,
		Map<Long, Long> wishCounts) {
		Pageable pageable = productsPage.getPageable();
		List<ProductEntity> products = productsPage.getContent();

		var productResponses = products.stream()
			.map(product -> new ReadAllProductResponseDto(
				product.getName(),
				product.getQuantity(),
				product.getPrice(),
				product.getProductThumbnail().getPath(),
				product.getCategory().getName(),
				wishCounts.get(product.getId())))
			.toList();

		return new PageResponseDto<>(
			productsPage.getTotalPages(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			productResponses);
	}

	public PageResponseDto<ReadAllProductResponseDto> toNoContentPageResponseDto(Page<ProductEntity> productsPage) {
		return new PageResponseDto<>(
			productsPage.getTotalPages(),
			productsPage.getPageable().getPageNumber(),
			productsPage.getPageable().getPageSize(),
			List.of());
	}

	public PageResponseDto<ReadAllUserProductResponseDto> toPageResponse(Page<UserProductEntity> pageUserProduct) {
		Pageable pageable = pageUserProduct.getPageable();

		var readUserProductResponses = pageUserProduct.getContent().stream()
			.map(userProduct -> new ReadAllUserProductResponseDto(userProduct.getId(),
				userProduct.getProduct().getCategory().getName(),
				userProduct.getProduct().getCategory().getName(),
				userProduct.getProduct().getId(),
				userProduct.getProduct().getName(),
				userProduct.getProduct().getPrice(),
				userProduct.getProduct().getProductThumbnail().getPath()))
			.toList();

		return new PageResponseDto<>(pageUserProduct.getTotalPages(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			readUserProductResponses);
	}

	public ProductEntity toProduct(CreateProductRequestDto createProductRequest, UserEntity auth,
		CategoryEntity category) {
		return ProductEntity.builder()
			.name(createProductRequest.name())
			.quantity(createProductRequest.quantity())
			.price(createProductRequest.price())
			.category(category)
			.owner(auth)
			.build();
	}

	public ProductEntity toProduct(UpdateProductRequestDto updateProductRequest, CategoryEntity foundCategory) {
		return ProductEntity.builder()
			.name(updateProductRequest.name())
			.category(foundCategory)
			.price(updateProductRequest.price())
			.quantity(updateProductRequest.quantity())
			.build();
	}

	public UserProductEntity toUserProduct(UserEntity auth, ProductEntity foundProduct) {
		return UserProductEntity.builder()
			.wisher(auth)
			.product(foundProduct)
			.build();
	}

	public List<RankingWishProductResponseDto> toRankingWishProductResponses(List<ProductEntity> rankingProducts) {
		return rankingProducts.stream()
			.map(product -> new RankingWishProductResponseDto(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<RankingReadProductResponseDto> toRankingReadProductResponses(List<ProductEntity> rankingProducts) {
		return rankingProducts.stream()
			.map(product -> new RankingReadProductResponseDto(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public RankingRegionOrderProductResponseDto toRankingRegionOrderProductResponses(
		List<ProductEntity> maximumOrderProductsByCity,
		List<ProductEntity> maximumOrderProductsByCountry,
		List<ProductEntity> maximumOrderProductsByDistrict
	) {

		List<ProductByCity> productsByCity = toProductByCity(maximumOrderProductsByCity);
		List<ProductByCountry> producstByCountry = toProductByCountry(maximumOrderProductsByCountry);
		List<ProductByDistrict> productsByDistrict = toProductByDistrict(maximumOrderProductsByDistrict);

		return new RankingRegionOrderProductResponseDto(
			productsByCity,
			producstByCountry,
			productsByDistrict
		);
	}

	public List<ProductByCity> toProductByCity(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new ProductByCity(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<ProductByCountry> toProductByCountry(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new ProductByCountry(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}

	public List<ProductByDistrict> toProductByDistrict(
		List<ProductEntity> maximumOrderProductsByCity) {
		return maximumOrderProductsByCity.stream()
			.map(product -> new ProductByDistrict(
				product.getId(),
				product.getName(),
				product.getPrice(),
				product.getCategory().getCode(),
				product.getProductThumbnail().getPath()
			)).toList();
	}
}
