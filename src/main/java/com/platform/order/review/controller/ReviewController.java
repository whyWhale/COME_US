package com.platform.order.review.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.common.validation.Multipart;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.response.CreateReviewResponseDto;
import com.platform.order.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Validated
@PreAuthorize("hasRole('ROLE_USER')")
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
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

}
