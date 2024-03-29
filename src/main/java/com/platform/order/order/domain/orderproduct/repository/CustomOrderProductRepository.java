package com.platform.order.order.domain.orderproduct.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.platform.order.order.controller.dto.request.OrderPageRequestDto;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;

@Repository
public interface CustomOrderProductRepository {
	List<OrderProductEntity> findMyAllWithConditions(Long authId, OrderPageRequestDto pageRequest);
}