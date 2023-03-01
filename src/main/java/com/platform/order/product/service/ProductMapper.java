package com.platform.order.product.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.DeleteProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.file.CreateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.product.file.UpdateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.ReadAllUserProductResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.WishUserProductResponseDto;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.productimage.entity.ProductImageEntity;
import com.platform.order.product.domain.productthumbnail.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.userproduct.entity.UserProductEntity;

@Component
public class ProductMapper {
	public CreateProductResponseDto toCreateProductResponseDto(ProductEntity product) {
		return new CreateProductResponseDto(
			product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode());
	}

	public CreateProductFileResponseDto productFileResponseDto(ProductThumbnailEntity thumbnail,
		List<ProductImageEntity> images) {
		var thumbnailResponseDto = new CreateProductFileResponseDto.thumbnailResponseDto(
			thumbnail.getName(), thumbnail.getOriginName(), thumbnail.getExtension(),
			thumbnail.getPath(), thumbnail.getSize());

		var imageResponseDtos = images.stream().map(
				productImage -> new CreateProductFileResponseDto.ImageResponseDto(
					productImage.getName(),
					productImage.getOriginName(),
					productImage.getExtension(),
					productImage.getPath(),
					productImage.getSize(),
					productImage.getArrangement()))
			.toList();

		return new CreateProductFileResponseDto(thumbnailResponseDto, imageResponseDtos);
	}

	public UpdateProductResponseDto toUpdateProductResponseDto(ProductEntity product) {
		return new UpdateProductResponseDto(
			product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode());
	}

	public UpdateProductFileResponseDto toUpdateProductFileResponseDto(ProductThumbnailEntity thumbnail,
		List<ProductImageEntity> productImages) {
		var thumbnailResponseDto = new UpdateProductFileResponseDto.thumbnailResponseDto(thumbnail.getName(),
			thumbnail.getOriginName(), thumbnail.getExtension(), thumbnail.getPath(), thumbnail.getSize());

		var imageResponseDtos = productImages.stream()
			.map(productImage -> new UpdateProductFileResponseDto.ImageResponseDto(
				productImage.getName(),
				productImage.getOriginName(),
				productImage.getExtension(),
				productImage.getPath(),
				productImage.getSize(),
				productImage.getArrangement()))
			.toList();

		return new UpdateProductFileResponseDto(thumbnailResponseDto, imageResponseDtos);
	}

	public DeleteProductResponseDto toDeleteProductResponseDto(ProductEntity foundProduct) {
		return new DeleteProductResponseDto(
			foundProduct.getId(),
			foundProduct.getName(),
			foundProduct.getQuantity(),
			foundProduct.getPrice(),
			foundProduct.isDisplay(),
			foundProduct.getCategory().getName(),
			foundProduct.getCategory().getCode());
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

	public PageResponseDto<ReadAllProductResponseDto> toNoContentPageResponseDto(
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

	public WishUserProductResponseDto toWishProductResponseDto(UserProductEntity userProduct) {
		return new WishUserProductResponseDto(userProduct.getId(),
			userProduct.getProduct().getCategory().getCode(),
			userProduct.getProduct().getCategory().getName(),
			userProduct.getProduct().getId(),
			userProduct.getProduct().getName(),
			userProduct.getProduct().getPrice(),
			userProduct.getProduct().getProductThumbnail().getPath(),
			userProduct.getProduct().isDisplay());
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
}
