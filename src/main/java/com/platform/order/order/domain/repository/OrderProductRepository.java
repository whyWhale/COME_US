package com.platform.order.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.entity.OrderProductEntity;
import com.platform.order.order.domain.repository.custom.CustomOrderProductRepository;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long>, CustomOrderProductRepository {
}
