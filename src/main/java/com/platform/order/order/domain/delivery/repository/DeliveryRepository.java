package com.platform.order.order.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.order.domain.delivery.entity.DeliveryEntity;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, Long> {
}
