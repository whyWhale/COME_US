package com.platform.order.ranking.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.product.controller.dto.response.product.RankingReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingRegionOrderProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingWishProductResponseDto;
import com.platform.order.ranking.service.RankingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/rankings")
@RestController
public class RankingController {
	private final RankingService rankingService;

	@GetMapping("/wish")
	public List<RankingWishProductResponseDto> getMaximumWishProducts() {
		return rankingService.getMaximumWishProducts();
	}

	@GetMapping("/read")
	public List<RankingReadProductResponseDto> getMaximumReadProducts() {
		return rankingService.getMaximumReadProducts();
	}

	@GetMapping("/order")
	public RankingRegionOrderProductResponseDto getMaximumOrderProductsByLocation(@Valid Location location) {
		return rankingService.getMaximumOrderProductsByLocation(location);
	}
}
