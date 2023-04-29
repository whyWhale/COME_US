package com.platform.order.ranking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.ranking.controller.dto.response.RankingReadProductResponseDto;
import com.platform.order.ranking.controller.dto.response.RankingRegionOrderProductResponseDto;
import com.platform.order.ranking.controller.dto.response.RankingWishProductResponseDto;
import com.platform.order.ranking.service.RankingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "추천 API")
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
@RestController
public class RankingController {
	private final RankingService rankingService;

	@Operation(summary = "찜 상품 10개 조회", description = "가장 많이 찜이 눌린 상품을 10개 조회합니다.")
	@GetMapping("/wish")
	public List<RankingWishProductResponseDto> getMaximumWishProducts() {
		return rankingService.getMaximumWishProducts();
	}

	@Operation(summary = "최대 조회 상품 10개 조회", description = "가장 많이 조회된 상품 10개를 조회합니다.")
	@GetMapping("/read")
	public List<RankingReadProductResponseDto> getMaximumReadProducts() {
		return rankingService.getMaximumReadProducts();
	}

	@Operation(summary = "가장 많이 주문된 상품 10개 조회", description = "인근 지역에서 가장 많이 주문된 상품 10개를 조회합니다.")
	@GetMapping("/order")
	public RankingRegionOrderProductResponseDto getMaximumOrderProductsByLocation(
		@Parameter
		@Valid
		Location location
	) {
		return rankingService.getMaximumOrderProductsByLocation(location);
	}
}
