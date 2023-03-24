package com.platform.order.ranking.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.MultiValueMap;

import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtProperty;
import com.platform.order.order.controller.dto.request.Location;
import com.platform.order.ranking.service.RankingService;
import com.platform.order.testenv.ControllerTest;
import com.platform.order.utils.ParameterUtils;

@WebMvcTest({RankingController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtProperty.class})
class RankingControllerTest extends ControllerTest {
	final String URI_PREFIX = "/api/rankings";

	@MockBean
	RankingService rankingService;

	@Test
	@DisplayName("인근 지역에서 가장 많이 주문한 상품을 조회한다.")
	void testGetMaximumOrderProductsByRegion() throws Exception {
		//given
		Location location = new Location("서울시", "강남구", "대치동");
		var params = new ParameterUtils<Location>().toMultiValueParams(objectMapper, location);
		//when
		ResultActions perform = mockMvc.perform(
			get(URI_PREFIX + "/order").params(params)
		);
		//then
		perform.andExpect(status().isOk());
	}

	@Nested
	@DisplayName("인근 지역에서 가장 많이 주문한 상품을 조회할 때,")
	class RankingOrderProductValidation {
		@DisplayName("city를 입력하지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}:  {0}")
		void testNullAndEmptyCity(String city) throws Exception {
			//given
			Location location = new Location(city, "강남구", "대치동");
			var params = new ParameterUtils<Location>().toMultiValueParams(objectMapper, location);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("country를 입력하지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}:  {0}")
		void testNullAndEmptyCountry(String city) throws Exception {
			//given
			Location location = new Location("서울특별시", city, "대치동");
			var params = new ParameterUtils<Location>().toMultiValueParams(objectMapper, location);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("country를 입력하지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}:  {0}")
		void testNullAndEmptyDistrict(String district) throws Exception {
			//given
			Location location = new Location("서울특별시", "강남구", district);
			var params = new ParameterUtils<Location>().toMultiValueParams(objectMapper, location);
			//when
			ResultActions perform = getPerform(params);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@NotNull
		private ResultActions getPerform(MultiValueMap<String, String> params) throws Exception {
			return mockMvc.perform(
				get(URI_PREFIX + "/order").params(params)
			);
		}
	}

}