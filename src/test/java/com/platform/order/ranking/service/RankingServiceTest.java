package com.platform.order.ranking.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.order.service.redis.OrderRedisService;
import com.platform.order.product.domain.product.repository.ProductRepository;
import com.platform.order.product.service.redis.ProductRedisService;
import com.platform.order.ranking.service.mapper.RankingMapper;
import com.platform.order.testenv.ServiceTest;

class RankingServiceTest extends ServiceTest {

	@InjectMocks
	RankingService rankingService;

	@Mock
	ProductRedisService productRedisService;

	@Mock
	OrderRedisService orderRedisService;

	@Mock
	ProductRepository productRepository;

	@Spy
	RankingMapper rankingMapper;

	@Test
	@DisplayName("찜이 가장 많은 상품 10개를 조회한다")
	void testGetMaximumWishProducts() {
		//given
		given(productRedisService.getMaximumWishProducts()).willReturn(List.of());
		//when
		rankingService.getMaximumWishProducts();
		//then
		verify(productRedisService, times(1)).getMaximumWishProducts();
	}

	@Test
	@DisplayName("조회수가 가장 많은 상품 10개를 조회한다")
	void testGetMaximumReadProducts() {
		//given
		given(productRedisService.getMaximumReadProducts()).willReturn(List.of());
		//when
		rankingService.getMaximumReadProducts();
		//then
		verify(productRedisService, times(1)).getMaximumReadProducts();
	}

	@Test
	@DisplayName("인근 지역에서 가장 많이 주문한 상품을 조회한다")
	void testGetMaximumOrderProducts() {
		//given
		Location location = new Location("서울시", "강남구", "대치동");
		given(orderRedisService.getMaximumOrderProductByRegionCity(any())).willReturn(List.of());
		given(orderRedisService.getMaximumOrderProductByRegionCountry(any())).willReturn(List.of());
		given(orderRedisService.getMaximumOrderProductByRegionDistrict(any())).willReturn(List.of());
		//when
		rankingService.getMaximumOrderProductsByLocation(location);
		//then
		verify(orderRedisService, times(1)).getMaximumOrderProductByRegionCity(location);
		verify(orderRedisService, times(1)).getMaximumOrderProductByRegionCountry(location);
		verify(orderRedisService, times(1)).getMaximumOrderProductByRegionDistrict(location);
	}
}