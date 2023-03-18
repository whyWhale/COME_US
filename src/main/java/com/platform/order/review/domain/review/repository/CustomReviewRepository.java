package com.platform.order.review.domain.review.repository;

import org.springframework.data.domain.Page;

import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.domain.review.entity.ReviewEntity;

public interface CustomReviewRepository {
	Page<ReviewEntity> findByAllWithSorts(ReviewPageRequestDto page, Long productId);
}
