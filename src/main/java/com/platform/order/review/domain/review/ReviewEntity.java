package com.platform.order.review.domain.review;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.platform.order.common.supperentity.BaseEntity;
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
	Long userId;

	@OneToOne
	private OrderProductEntity orderProduct;

	private int score;

	@Lob
	private String content;

	@Builder.Default
	@OneToMany(mappedBy = "review", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private Set<ReviewImageEntity> images = new HashSet<>();

	public void addReviewImage(List<ReviewImageEntity> reviewImages) {
		reviewImages.forEach(reviewImageEntity -> reviewImageEntity.addReview(this));
		images.addAll(reviewImages);
	}

	public void update(int score, String contents) {
		this.score = score;
		this.content = contents;
	}

	//
	public Set<ReviewImageEntity> removeImages() {
		Set<ReviewImageEntity> removes = this.images;

		this.images.clear();

		return removes;
	}
}
