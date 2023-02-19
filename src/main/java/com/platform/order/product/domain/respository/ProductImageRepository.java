package com.platform.order.product.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.entity.ProductImageEntity;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity,Long>, CustomProductImageRepository {
}
