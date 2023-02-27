package com.platform.order.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.product.domain.entity.UserProductEntity;
import com.platform.order.product.domain.repository.custom.CustomUserProductRepository;

public interface UserProductRepository extends JpaRepository<UserProductEntity, Long>, CustomUserProductRepository {
}
