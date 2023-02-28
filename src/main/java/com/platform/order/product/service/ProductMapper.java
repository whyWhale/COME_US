package com.platform.order.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.platform.order.common.protocal.PageResponseDto;
import com.platform.order.product.controller.dto.response.CreateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.DeleteProductResponseDto;
import com.platform.order.product.controller.dto.response.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.ReadUserProductResponseDto;
import com.platform.order.product.controller.dto.response.UpdateProductFileResponseDto;
import com.platform.order.product.controller.dto.response.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.WishUserProductResponseDto;
import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.entity.ProductImageEntity;
import com.platform.order.product.domain.entity.ProductThumbnailEntity;
import com.platform.order.product.domain.entity.UserProductEntity;

@Component
public class ProductMapper {
	public CreateProductResponseDto toCreateProductResponseDto(ProductEntity product) {
		return new CreateProductResponseDto(
			product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode()
		);
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
			.map(productImage ->
				new UpdateProductFileResponseDto.ImageResponseDto(
					productImage.getName(),
					productImage.getOriginName(),
					productImage.getExtension(),
					productImage.getPath(),
					productImage.getSize(),
					productImage.getArrangement()
				))
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
			foundProduct.getCategory().getCode()
		);
	}

	public ReadProductResponseDto toReadProductResponseDto(ProductEntity foundProduct,
		List<ProductImageEntity> images) {
		List<String> imagePaths = images.stream()
			.map(ProductImageEntity::getPath)
			.toList();

		return new ReadProductResponseDto(
			foundProduct.getName(),
			foundProduct.getQuantity(),
			foundProduct.getPrice(),
			foundProduct.getProductThumbnail().getPath(),
			foundProduct.getCategory().getName(),
			imagePaths
		);
	}

	public PageResponseDto<ReadAllProductResponseDto> toPageResponseDto(Page<ProductEntity> productsPage) {
		Pageable pageable = productsPage.getPageable();
		List<ProductEntity> products = productsPage.getContent();

		List<ReadAllProductResponseDto> productResponses = products.stream().map(product ->
			new ReadAllProductResponseDto(
				product.getName(),
				product.getQuantity(),
				product.getPrice(),
				product.getProductThumbnail().getPath(),
				product.getCategory().getName())
		).toList();

		return new PageResponseDto<>(
			productsPage.getTotalPages(),
			pageable.getPageNumber(),
			pageable.getPageSize(),
			productResponses
		);
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

	public PageResponseDto<ReadUserProductResponseDto> toPageResponse(Page<UserProductEntity> pageUserProduct) {
		Pageable pageable = pageUserProduct.getPageable();

		List<ReadUserProductResponseDto> readUserProductResponses = pageUserProduct.getContent().stream()
			.map(userProduct -> new ReadUserProductResponseDto(userProduct.getId(),
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
