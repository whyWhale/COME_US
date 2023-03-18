package com.platform.order.review.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.platform.order.review.domain.review.entity.ReviewEntity;
import com.platform.order.review.domain.review.repository.CustomReviewRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long>, CustomReviewRepository {

	@Query(value = "select r from ReviewEntity r "
		+ "left join fetch r.images "
		+ "where r.id =:reviewId and r.userId =:authId")
	Optional<ReviewEntity> findByIdWithImage(@Param("reviewId") Long reviewId, @Param("authId") Long authId);

}
