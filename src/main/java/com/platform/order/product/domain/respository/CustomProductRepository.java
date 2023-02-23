package com.platform.order.product.domain.respository;

import org.springframework.data.domain.Page;

import com.platform.order.product.domain.entity.ProductEntity;
import com.platform.order.product.web.dto.request.ProductPageRequestRequestDto;

public interface CustomProductRepository {
	Page<ProductEntity> findAllWithConditions(ProductPageRequestRequestDto page);
}
