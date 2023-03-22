package com.platform.order.order.service.redis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.testenv.IntegrationTest;

class OrderRedisServiceTest extends IntegrationTest {

	@Autowired
	OrderRedisService orderRedisService;

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@AfterEach
	public void setDown() {
		redisTemplate.delete("서울특별시");
		redisTemplate.delete("서울특별시강남구");
		redisTemplate.delete("서울특별시강남구청담동");
		redisTemplate.delete("서울특별시강남구방배동");
		redisTemplate.delete("서울특별시동구");
		redisTemplate.delete("서울특별시동구자양동");
		redisTemplate.delete("서울특별시중구");
		redisTemplate.delete("서울특별시중구목동");
	}

	@Test
	@DisplayName("해당 지역에서 상품을 주문할 때 주문횟수를 증가시킨다.")
	void testIncreaseOrderByRegion() {
		//given
		Long orderProductId = 1L;
		List<Location> gangNams = List.of(new Location("서울특별시", "강남구", "청담동"),
			new Location("서울특별시", "강남구", "청담동"),
			new Location("서울특별시", "강남구", "청담동"),
			new Location("서울특별시", "강남구", "청담동"));
		List<Location> dongGus = List.of(
			new Location("서울특별시", "동구", "자양동"),
			new Location("서울특별시", "동구", "자양동"),
			new Location("서울특별시", "동구", "자양동"),
			new Location("서울특별시", "동구", "자양동")
		);
		List<Location> jungGus = List.of(
			new Location("서울특별시", "중구", "목동"),
			new Location("서울특별시", "중구", "목동")
		);
		List<Location> locations = Stream.of(gangNams, dongGus, jungGus)
			.flatMap(Collection::stream)
			.toList();
		//when
		locations.forEach(location -> orderRedisService.increaseOrderByRegion(orderProductId, location));
		int countInSeoul = redisTemplate.opsForZSet().score("서울특별시", orderProductId.toString()).intValue();
		int countInSeoulAndGangNam = redisTemplate.opsForZSet().score("서울특별시강남구", orderProductId.toString()).intValue();
		int countInSeoulAndDongGu = redisTemplate.opsForZSet().score("서울특별시동구", orderProductId.toString()).intValue();
		int countInSeoulAndJungGu = redisTemplate.opsForZSet().score("서울특별시중구", orderProductId.toString()).intValue();
		//then
		Assertions.assertThat(countInSeoul).isEqualTo(locations.size());
		Assertions.assertThat(countInSeoulAndGangNam).isEqualTo(gangNams.size());
		Assertions.assertThat(countInSeoulAndDongGu).isEqualTo(dongGus.size());
		Assertions.assertThat(countInSeoulAndJungGu).isEqualTo(jungGus.size());
	}

	@Test
	@DisplayName("해당 지역에서 구매한 상품을 취소할 때 주문횟수를 차감한다.")
	void testDecreaseOrderByRegion() {
		//given
		Long cancelProductId = 1L;
		Location cancelLocation = new Location("서울특별시", "강남구", "방배동");
		int initOrderCnt = 100;
		redisTemplate.opsForZSet().incrementScore(cancelLocation.city(), cancelProductId.toString(), initOrderCnt);
		redisTemplate.opsForZSet()
			.incrementScore(cancelLocation.toStringUntilCountry(), cancelProductId.toString(), 100);
		redisTemplate.opsForZSet()
			.incrementScore(cancelLocation.toStringUntilDistrict(), cancelProductId.toString(), 100);
		//when
		orderRedisService.decreaseOrderByRegion(cancelProductId, cancelLocation);
		int countInSeoul = redisTemplate.opsForZSet()
			.score(cancelLocation.city(), cancelProductId.toString())
			.intValue();
		int countInSeoulAndGangNam = redisTemplate.opsForZSet()
			.score(cancelLocation.toStringUntilCountry(), cancelProductId.toString())
			.intValue();
		int countInSeoulAndGangNamBangBea = redisTemplate.opsForZSet()
			.score(cancelLocation.toStringUntilDistrict(), cancelProductId.toString())
			.intValue();
		//then
		Assertions.assertThat(countInSeoul).isEqualTo(initOrderCnt - 1);
		Assertions.assertThat(countInSeoulAndGangNam).isEqualTo(initOrderCnt - 1);
		Assertions.assertThat(countInSeoulAndGangNamBangBea).isEqualTo(initOrderCnt - 1);
	}

}