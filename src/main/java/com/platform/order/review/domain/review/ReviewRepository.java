package com.platform.order.review.domain.review;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

	@Query(value = "select r from ReviewEntity r "
		+ "left join fetch ReviewImageEntity ri "
		+ "where r.id =:reviewId and r.userId =:authId")
	Optional<ReviewEntity> findByIdWithImage(@Param("reviewId") Long reviewId, @Param("authId") Long authId);
}
