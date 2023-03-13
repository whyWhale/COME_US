package com.platform.order.review.service;

import static com.platform.order.common.storage.FileSuffixPath.REVIEW_IMAGE;
import static java.util.stream.Collectors.toMap;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.exception.custom.NotFoundResourceException;
import com.platform.order.common.storage.AwsStorageService;
import com.platform.order.common.storage.request.UploadFileRequestDto;
import com.platform.order.common.storage.response.UploadFileResponseDto;
import com.platform.order.order.domain.orderproduct.entity.OrderProductEntity;
import com.platform.order.order.domain.orderproduct.repository.OrderProductRepository;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.domain.review.ReviewEntity;
import com.platform.order.review.domain.review.ReviewRepository;
import com.platform.order.review.domain.reviewimage.ReviewImageEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {
	private final AwsStorageService awsStorageService;
	private final ReviewRepository reviewRepository;
	private final OrderProductRepository orderProductRepository;
	private final ReviewMapper reviewMapper;

	@Transactional
	public CreateReviewResponseDto create(
		Long authId,
		CreateReviewRequestDto createReviewRequest,
		List<MultipartFile> images
	) {
		Long orderProductId = createReviewRequest.orderProductId();
		OrderProductEntity foundOrderProduct = orderProductRepository.findById(orderProductId)
			.orElseThrow(() -> new NotFoundResourceException(
				MessageFormat.format("order product : {0} is not found", orderProductId)));

		List<UploadFileRequestDto> uploadFileRequests = images.stream()
			.map(reviewMapper::toUploadFileRequest)
			.toList();
		List<UploadFileResponseDto> uploadResponses = awsStorageService.upload(uploadFileRequests, REVIEW_IMAGE);
		Map<String, UploadFileResponseDto> fileNames = uploadResponses.stream()
			.collect(toMap(responseDto -> responseDto.multipartFile().getOriginalFilename(), Function.identity()));
		List<ReviewImageEntity> reviewImages = images.stream()
			.map(multipartFile -> reviewMapper.toReviewImage(fileNames, multipartFile))
			.toList();
		ReviewEntity review = reviewMapper.toReview(authId, createReviewRequest, foundOrderProduct);
		review.addReviewImage(reviewImages);

		ReviewEntity savedReview = reviewRepository.save(review);

		return reviewMapper.toCreateReviewResponse(savedReview);
	}


}