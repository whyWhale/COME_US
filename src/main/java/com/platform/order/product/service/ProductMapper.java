package com.platform.order.product.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.domain.entity.ProductImageEntity;
import com.platform.order.product.domain.entity.ProductThumbnailEntity;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;
import com.platform.order.product.web.dto.response.ProductFileResponseDto;

@Component
public class ProductMapper {
	public CreateProductResponseDto toCreateProductResponseDto(ProductEntity product) {
		return new CreateProductResponseDto(product.getName(),
			product.getQuantity(),
			product.getPrice(),
			product.getCategory().getCode());
	}

	public ProductFileResponseDto productFileResponseDto(ProductThumbnailEntity thumbnail,
		List<ProductImageEntity> images) {
		var thumbnailResponseDto = new ProductFileResponseDto.thumbnailResponseDto(
			thumbnail.getName(), thumbnail.getOriginName(), thumbnail.getExtension(),
			thumbnail.getPath(), thumbnail.getSize());

		var imageResponseDtos = images.stream().map(
			productImage ->
				new ProductFileResponseDto.ImageResponseDto(
					productImage.getName(),
					productImage.getOriginName(),
					productImage.getExtension(),
					productImage.getPath(),
					productImage.getSize(),
					productImage.getArrangement()
				)
		).toList();

		return new ProductFileResponseDto(thumbnailResponseDto, imageResponseDtos);
	}
}
