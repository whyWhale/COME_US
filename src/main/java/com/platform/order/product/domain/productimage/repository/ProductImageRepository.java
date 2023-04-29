package com.platform.order.product.domain.productimage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.productimage.entity.ProductImageEntity;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long>, CustomProductImageRepository {
	List<ProductImageEntity> findByProduct(ProductEntity product);
}
