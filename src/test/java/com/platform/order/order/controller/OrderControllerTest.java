package com.platform.order.order.controller;

import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.auth.controller.handler.LoginSuccessHandler;
import com.platform.order.auth.controller.handler.LogoutSuccessHandler;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.common.security.service.TokenService;
import com.platform.order.order.controller.dto.request.CreateOrderRequestDto;
import com.platform.order.order.service.OrderService;
import com.platform.order.security.WithJwtMockUser;

@WithJwtMockUser
@WebMvcTest({OrderController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	LoginSuccessHandler.class,
	LogoutSuccessHandler.class,
	LogoutSuccessHandler.class,
	JwtConfig.class})
class OrderControllerTest {
	final String URI_PREFIX = "/api/orders";
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper objectMapper;
	@MockBean
	OrderService orderService;

	@MockBean
	TokenService tokenService;

	Long buyingProductId = 1L;
	String address = "서울특별시 강남구 강남동";
	String zipCode = "123-12";

	@Test
	@DisplayName("상품을 주문한다.")
	void testOrder() throws Exception {
		//given
		Long orderQuantity = 1L;
		var orderProductsRequest = List.of(
			new CreateOrderRequestDto.OrderProductRequestDto(buyingProductId, orderQuantity)
		);
		CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, address, zipCode);
		//when
		ResultActions perform = mockMvc.perform(
			post(URI_PREFIX)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isOk());
		verify(orderService, times(1)).placeOrder(1L, requestDto);
	}

	@DisplayName("상품 주문시 ")
	@Nested
	class CreateOrderValidation {
		static Stream<Arguments> provideNullWithProductIdOrOrderQuantity() { // argument source method
			return Stream.of(
				Arguments.of(null, 1L),
				Arguments.of(1L, null),
				Arguments.of(null, null)
			);
		}

		@Test
		@DisplayName("주문할 상품 정보가 존재하지 않으면 BadRequest로 응답한다.")
		void failOrder() throws Exception {
			//given
			var orderProductsRequest = new ArrayList<CreateOrderRequestDto.OrderProductRequestDto>();
			CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, address, zipCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("배송정보중 주소가 기재되어있지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}: address: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failOrderWithInvalidAddress(String invalidAddress) throws Exception {
			//given
			var orderProductsRequest = List.of(
				new CreateOrderRequestDto.OrderProductRequestDto(buyingProductId, 1L)
			);
			CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, invalidAddress, zipCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("배송정보중 우편번호가 기재되어있지 않으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}: zipCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failOrderWithInvalidZipCode(String invalidZipCode) throws Exception {
			//given
			var orderProductsRequest = List.of(
				new CreateOrderRequestDto.OrderProductRequestDto(buyingProductId, 1L)
			);
			CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, address, invalidZipCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("주문할 상품정보에 아무것도 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: productId: {0}, orderQuantity: {1}")
		@MethodSource("provideNullWithProductIdOrOrderQuantity")
		void failOrderWithInvalidOrderProductDto(Long productId, Long orderQuantity) throws Exception {
			//given
			var orderProductsRequest = List.of(
				new CreateOrderRequestDto.OrderProductRequestDto(productId, orderQuantity)
			);
			CreateOrderRequestDto requestDto = new CreateOrderRequestDto(orderProductsRequest, address, zipCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(CreateOrderRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				post(URI_PREFIX)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}
}