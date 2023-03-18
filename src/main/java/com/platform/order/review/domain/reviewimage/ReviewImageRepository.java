package com.platform.order.review.domain.reviewimage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.platform.order.review.domain.review.entity.ReviewEntity;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity, Long> {
	List<ReviewImageEntity> findByReviewIn(List<ReviewEntity> reviewEntities);
}
