package com.platform.order.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.entity.ProductThumbnailEntity;

public interface ProductThumbnailRepository extends JpaRepository<ProductThumbnailEntity, Long> {
}
