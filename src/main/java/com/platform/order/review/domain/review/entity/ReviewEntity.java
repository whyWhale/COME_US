package com.platform.order.review.domain.review.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Where;

import com.platform.order.common.superentity.BaseEntity;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.review.domain.reviewimage.ReviewImageEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted=false")
@Table(name = "review")
@Entity
public class ReviewEntity extends BaseEntity {
	@NotNull
	private int score;

	@NotNull
	private Long userId;
	@Lob
	private String content;

	@OneToOne(fetch = FetchType.LAZY)
	private OrderProductEntity orderProduct;

	@Builder.Default
	@OneToMany(mappedBy = "review", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<ReviewImageEntity> images = new ArrayList<>();

	public void addReviewImage(List<ReviewImageEntity> reviewImages) {
		reviewImages.forEach(reviewImageEntity -> reviewImageEntity.addReview(this));
		images.addAll(reviewImages);
	}

	public void update(int score, String contents) {
		this.score = score;
		this.content = contents;
	}

	public boolean isDifferentFromBefore(int score) {
		return this.score != score;
	}

	public List<ReviewImageEntity> removeImages() {
		List<ReviewImageEntity> removes = this.images;
		this.images.clear();

		return removes;
	}
}
