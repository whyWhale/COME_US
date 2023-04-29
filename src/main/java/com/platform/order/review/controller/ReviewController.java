package com.platform.order.review.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springdoc.api.annotations.ParameterObject;
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

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.common.validation.FileContent;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.controller.dto.request.UpdateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.controller.dto.response.ReadReviewResponseDto;
import com.platform.order.review.controller.dto.response.UpdateReviewResponseDto;
import com.platform.order.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "리뷰 API")
@Validated
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "리뷰 생성", description = "해당 상품을 구매한 사용자만이 글과 리뷰 이미지를 작성할 수 있습니다.")
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
		List<@FileContent MultipartFile> images
	) {
		return reviewService.create(auth.id(), createReviewRequest, images);
	}

	@Operation(summary = "리뷰 수정", description = "리뷰를 작성한 사용자만이 이미지를 포함하여 리뷰 글을 수정합니다.")
	@PreAuthorize("hasRole('ROLE_USER')")
	@PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public UpdateReviewResponseDto upadte(
		@AuthenticationPrincipal
		JwtAuthentication auth,

		@Parameter(description = "리뷰 아이디", required = true)
		@PathVariable
		Long reviewId,

		@Valid
		@RequestPart
		UpdateReviewRequestDto updateReviewRequest,

		@Size(max = 3)
		@RequestPart(required = false)
		List<@FileContent MultipartFile> images
	) {
		return reviewService.update(auth.id(), reviewId, updateReviewRequest, images);
	}

	@Operation(summary = "리뷰 전제 조회", description = "해당 상품에 작성된 리뷰글을 모두 조회합니다.")
	@GetMapping(value = "/{productId}")
	public OffsetPageResponseDto<ReadReviewResponseDto> readAll(
		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@ParameterObject
		@Valid
		ReviewPageRequestDto pageRequestDto
	) {
		return reviewService.readAll(productId, pageRequestDto);
	}
}
