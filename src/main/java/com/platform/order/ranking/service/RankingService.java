package com.platform.order.ranking.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.order.service.redis.OrderRedisService;
import com.platform.order.product.controller.dto.response.product.RankingReadProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingRegionOrderProductResponseDto;
import com.platform.order.product.controller.dto.response.product.RankingWishProductResponseDto;
import com.platform.order.product.domain.product.entity.ProductEntity;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.service.redis.ProductRedisService;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RankingService {

	private final ProductRedisService productRedisService;
	private final OrderRedisService orderRedisService;
	private final ProductRepository productRepository;

	private final RankingMapper rankingMapper;

	public List<RankingWishProductResponseDto> getMaximumWishProducts() {
		List<Long> productIds = productRedisService.getMaximumWishProducts();
		List<ProductEntity> rankingProducts = productRepository.findByIdInWithCategoryAndThumbnail(productIds);

		return rankingMapper.toRankingWishProductResponses(rankingProducts);
	}

	public List<RankingReadProductResponseDto> getMaximumReadProducts() {
		List<Long> productIds = productRedisService.getMaximumReadProducts();
		List<ProductEntity> rankingProducts = productRepository.findByIdInWithCategoryAndThumbnail(productIds);

		return rankingMapper.toRankingReadProductResponses(rankingProducts);
	}

	public RankingRegionOrderProductResponseDto getMaximumOrderProductsByLocation(Location location) {
		List<Long> orderProductIdsByCity = orderRedisService.getMaximumOrderProductByRegionCity(location);
		var productsByCity = productRepository.findByIdInWithCategoryAndThumbnail(orderProductIdsByCity);
		List<Long> orderProductIdsByCountry = orderRedisService.getMaximumOrderProductByRegionCountry(location);
		var productsByCountry = productRepository.findByIdInWithCategoryAndThumbnail(orderProductIdsByCountry);
		List<Long> orderProductIdsByDistrict = orderRedisService.getMaximumOrderProductByRegionDistrict(location);
		var productsByDistrict = productRepository.findByIdInWithCategoryAndThumbnail(orderProductIdsByDistrict);

		return rankingMapper.toRankingRegionOrderProductResponses(
			productsByCity,
			productsByCountry,
			productsByDistrict
		);
	}
}
