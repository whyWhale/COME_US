package com.platform.order.review.domain.reviewimage;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.platform.order.common.supperentity.FileBaseEntity;
import com.platform.order.review.domain.review.ReviewEntity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Table(name = "review_image")
@Entity
public class ReviewImageEntity extends FileBaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private ReviewEntity review;

	@Builder
	public ReviewImageEntity(
		String originName,
		String fileName,
		String path,
		String extension,
		Long size
	) {
		super(originName, fileName, path, extension, size);
	}

	public void addReview(ReviewEntity review) {
		this.review = review;
	}
}
