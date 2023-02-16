package com.platform.order.order.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
