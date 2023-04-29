package com.platform.order.product.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.platform.order.common.dto.offset.OffsetPageResponseDto;
import com.platform.order.common.security.model.JwtAuthentication;
import com.platform.order.common.validation.FileContent;
import com.platform.order.product.controller.dto.request.product.CreateProductRequestDto;
import com.platform.order.product.controller.dto.request.product.ProductPageRequestDto;
import com.platform.order.product.controller.dto.request.product.UpdateProductRequestDto;
import com.platform.order.product.controller.dto.request.userproduct.WishUserProductPageRequestDto;
import com.platform.order.product.controller.dto.response.product.CreateProductImagesResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.CreateThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadAllProductResponseDto;
import com.platform.order.product.controller.dto.response.product.ReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductImageResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductResponseDto;
import com.platform.order.product.controller.dto.response.product.UpdateProductThumbnailResponseDto;
import com.platform.order.product.controller.dto.response.userproduct.ReadAllUserProductResponseDto;
import com.platform.order.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상품 API")
@Validated
@RequiredArgsConstructor
@RequestMapping(value = "/api/products")
@RestController
public class ProductController {
	private final ProductService productService;

	@Operation(summary = "상품 생성", description = "상품 생성은 Owner 권한을 가진 사용자만 가능합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping
	public CreateProductResponseDto create(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		CreateProductRequestDto createProductRequest) {

		return productService.create(principal.id(), createProductRequest);
	}

	@Operation(summary = "상품 섬네일 생성", description = "상품 기본 정보 저장후 섬네일 이미지를 생성합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping(value = "/thumbnail/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CreateThumbnailResponseDto createThumbnail(
		@Parameter(name = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@FileContent
		@NotNull
		@RequestPart
		MultipartFile thumbnail
	) {
		return productService.createThumbnail(productId, principal.id(), thumbnail);
	}

	@Operation(summary = "상품 이미지 생성", description = "상품 기본 정보 저장후 상세정보에 표시할 이미지를 생성합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PostMapping(value = "/images/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<CreateProductImagesResponseDto> createImages(
		@Parameter(name = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@Size(min = 3, max = 10)
		@RequestPart
		List<@Valid @FileContent MultipartFile> images
	) {
		return productService.createImages(productId, principal.id(), images);
	}

	@Operation(summary = "상품 수정", description = "Owner 권한 및 상품을 생성한 Owner 만이 해당 상품 정보를 수정할 수 있습니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PatchMapping("/{productId}")
	public UpdateProductResponseDto upadte(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@RequestBody
		UpdateProductRequestDto updateProductRequest,

		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId
	) {
		return productService.update(principal.id(), productId, updateProductRequest);
	}

	@Operation(summary = "상품 성네일 수정", description = "기존에 있던 섬네일을 삭제하고 새로 저장합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PatchMapping(value = "/thumbnail/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public UpdateProductThumbnailResponseDto updateThumbnail(
		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "섬네일 파일")
		@RequestPart
		MultipartFile thumbnail
	) {
		return productService.updateThumbnail(productId, principal.id(), thumbnail);
	}

	@Operation(summary = "상품 이미지 수정", description = "기존에 있던 모든 이미지들을 삭제하고 새로 저장합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@PatchMapping(value = "/images/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public List<UpdateProductImageResponseDto> updateImages(
		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Valid
		@Size(min = 3, max = 10)
		@RequestPart
		List<@Valid @FileContent MultipartFile> images
	) {
		return productService.updateImages(productId, principal.id(), images);
	}

	@Operation(summary = "상품 삭제", description = "해당 상품을 생성한 Owner 만이 삭제 가능하며, 이미지 및 섬네일을 모두 삭제합니다.")
	@PreAuthorize("hasRole('ROLE_OWNER')")
	@DeleteMapping("/{productId}")
	public Long delete(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId
	) {
		return productService.delete(productId, principal.id());
	}

	@Operation(summary = "상품 단건 조회", description = "식별되지 않는 사용자를 포함하여 해당 상품 상세 정보를 조회할 수 있습니다.")
	@GetMapping("/{productId}")
	public ReadProductResponseDto read(
		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId,

		@Parameter(description = "읽기 중복 식별을 위한 쿠키 값", in = ParameterIn.COOKIE, required = true)
		@CookieValue(name = "visitor")
		String visitor) {

		return productService.read(productId, visitor);
	}

	@Operation(summary = "상품 전체 조회", description = "식별되지 않는 사용자를 포함하여 해당 상품들을 조회할 수 있습니다.")
	@GetMapping("/category/{categoryCode}")
	public OffsetPageResponseDto<ReadAllProductResponseDto> readAll(
		@Parameter(description = "카테고리 코드", required = true)
		@PathVariable
		String categoryCode,

		@ParameterObject
		@Valid
		ProductPageRequestDto pageRequest
	) {
		return productService.readAll(pageRequest, categoryCode);
	}

	@Operation(summary = "상품 찜", description = "로그인한 사용자만이 이용할 수 있으며 장바구니에 해당 상품을 담습니다.")
	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping("/wish/{productId}")
	public Long wishProduct(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long productId) {

		return productService.wishProduct(productId, principal.id());
	}

	@Operation(summary = "찜 목록 조회", description = "로그인한 사용자만이 이용할 수 있으며 장바구니에 담긴 상품들을 조회합니다.")
	@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping("/wish")
	public OffsetPageResponseDto<ReadAllUserProductResponseDto> readAllWishProducts(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@ParameterObject
		WishUserProductPageRequestDto pageRequestDto) {

		return productService.readAllWishProducts(principal.id(), pageRequestDto);
	}

	@Operation(summary = "찜한 상품 취소", description = "로그인한 사용자만이 이용할 수 있으며 장바구니에 담긴 상품 하나를 삭제합니다.")
	@PreAuthorize("hasRole('ROLE_USER')")
	@DeleteMapping("/unwish/{userProductId}")
	public Long unWishProduct(
		@AuthenticationPrincipal
		JwtAuthentication principal,

		@Parameter(description = "상품 아이디", required = true)
		@PathVariable
		Long userProductId) {

		return productService.unWishProduct(principal.id(), userProductId);
	}
}
