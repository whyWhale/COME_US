package com.platform.order.product.domain.respository;

import java.util.List;

import com.platform.order.product.domain.entity.ProductImageEntity;

public interface CustomProductImageRepository {
	public List<ProductImageEntity> saveAllInBulk(List<ProductImageEntity> productImages);
}
