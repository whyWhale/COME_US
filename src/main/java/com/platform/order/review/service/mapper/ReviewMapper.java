package com.platform.order.review.service.mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.common.storage.request.UploadFileRequestDto;
import com.platform.order.common.storage.response.UploadFileResponseDto;
import com.platform.order.common.utils.FileUtils;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto.CreateReviewImageResponseDto;
import com.platform.order.review.controller.dto.response.ReadReviewResponseDto;
import com.platform.order.review.controller.dto.response.ReviewProductMetaResponseDto;
import com.platform.order.review.controller.dto.response.UpdateReviewResponseDto;
import com.platform.order.review.controller.dto.response.UpdateReviewResponseDto.UpdateReviewImageResponseDto;
import com.platform.order.review.domain.review.entity.ReviewEntity;
import com.platform.order.review.domain.reviewimage.ReviewImageEntity;

@Component
public class ReviewMapper {
	public ReviewEntity toReview(
		Long authId,
		CreateReviewRequestDto createReviewRequest,
		OrderProductEntity foundOrderProduct
	) {
		return ReviewEntity.builder()
			.userId(authId)
			.orderProduct(foundOrderProduct)
			.score(createReviewRequest.score())
			.content(createReviewRequest.contents())
			.build();
	}

	public ReviewImageEntity toReviewImage(Map<String, UploadFileResponseDto> fileNames,
		MultipartFile multipartFile) {
		String originName = multipartFile.getOriginalFilename();
		UploadFileResponseDto uploadFileResponse = fileNames.get(originName);

		return ReviewImageEntity.builder()
			.originName(originName)
			.fileName(uploadFileResponse.fileName())
			.path(uploadFileResponse.path())
			.extension(uploadFileResponse.extension())
			.size(uploadFileResponse.multipartFile().getSize())
			.build();
	}

	public UploadFileRequestDto toUploadFileRequest(MultipartFile multipartFile) {
		return new UploadFileRequestDto(
			UUID.randomUUID().toString(),
			FileUtils.getExtension(multipartFile.getOriginalFilename()),
			multipartFile)
			;
	}

	public CreateReviewResponseDto toCreateReviewResponse(ReviewEntity review) {
		List<CreateReviewImageResponseDto> createReviewImageResponses = review.getImages().stream()
			.map(reviewImage -> new CreateReviewImageResponseDto(reviewImage.getId(), reviewImage.getPath()))
			.toList();

		return new CreateReviewResponseDto(
			review.getId(),
			review.getOrderProduct().getId(),
			review.getUserId(),
			review.getScore(),
			createReviewImageResponses
		);
	}

	public UpdateReviewResponseDto toUpdateReviewResponses(ReviewEntity foundReview) {
		List<UpdateReviewImageResponseDto> updateReviewImageResponses = foundReview.getImages()
			.stream()
			.map(reviewImage -> new UpdateReviewImageResponseDto(reviewImage.getId(), reviewImage.getPath()))
			.toList();

		return new UpdateReviewResponseDto(
			foundReview.getId(),
			foundReview.getScore(),
			foundReview.getContent(),
			updateReviewImageResponses
		);
	}

	public OffsetPageResponseDto<ReadReviewResponseDto> toPageResponseDto(
		Page<ReviewEntity> reviewPage,
		Map<Long, String> users
	) {
		List<ReadReviewResponseDto> readReviewResponses = reviewPage.getContent().stream()
			.map(review -> new ReadReviewResponseDto(
				users.get(review.getUserId()),
				review.getScore(),
				review.getContent(),
				review.getImages().stream().map(ReviewImageEntity::getPath).toList())
			).toList();

		return new OffsetPageResponseDto<>(
			reviewPage.getTotalPages(),
			reviewPage.getPageable().getPageNumber(),
			reviewPage.getPageable().getPageSize(),
			readReviewResponses
		);
	}

	public ReviewProductMetaResponseDto toReviewProductMetaResponseDto(long count, int average) {
		return new ReviewProductMetaResponseDto(average, count);
	}
}
