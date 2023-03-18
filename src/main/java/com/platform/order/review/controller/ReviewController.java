package com.platform.order.review.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.dto.offset.PageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.common.validation.Multipart;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.controller.dto.request.UpdateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.controller.dto.response.ReadReviewResponseDto;
import com.platform.order.review.controller.dto.response.UpdateReviewResponseDto;
import com.platform.order.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Validated
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CreateReviewResponseDto create(
		@AuthenticationPrincipal
		JwtAuthentication auth,

		@Valid
		@RequestPart
		CreateReviewRequestDto createReviewRequest,

		@Size(max = 3)
		@RequestPart(required = false)
		List<@Multipart MultipartFile> images
	) {
		return reviewService.create(auth.id(), createReviewRequest, images);
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PatchMapping(
		value = "/{reviewId}",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public UpdateReviewResponseDto upadte(
		@AuthenticationPrincipal
		JwtAuthentication auth,

		@PathVariable
		Long reviewId,

		@Valid
		@RequestPart
		UpdateReviewRequestDto updateReviewRequest,

		@Size(max = 3)
		@RequestPart(required = false)
		List<@Multipart MultipartFile> images
	) {
		return reviewService.update(auth.id(), reviewId, updateReviewRequest, images);
	}

	@GetMapping(value = "/{productId}")
	public PageResponseDto<ReadReviewResponseDto> readAll(
		@PathVariable
		Long productId,

		@Valid
		ReviewPageRequestDto pageRequestDto
	){
		return reviewService.readAll(productId,pageRequestDto);
	}


}
