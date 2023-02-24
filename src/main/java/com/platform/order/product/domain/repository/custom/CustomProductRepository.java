package com.platform.order.product.domain.repository.custom;

import org.springframework.data.domain.Page;

import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.controller.dto.request.ProductPageRequestDto;

public interface CustomProductRepository {
	Page<ProductEntity> findAllWithConditions(ProductPageRequestDto page);
}
