package com.platform.order.product.service;

import org.springframework.stereotype.Component;

import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.web.dto.response.CreateProductResponseDto;

@Component
public class ProductMapper {
	public CreateProductResponseDto toCreateProductResponseDto(ProductEntity savedProductEntity) {
		return new CreateProductResponseDto(savedProductEntity.getName(),
			savedProductEntity.getQuantity(),
			savedProductEntity.getPrice(),
			savedProductEntity.getCategory().getCode());
	}
}
