package com.platform.order.product.web.controller;

import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.product.service.ProductService;
import com.platform.order.product.web.dto.request.CreateProductRequestDto;
import com.platform.order.product.web.dto.request.UpdateProductRequestDto;
import com.platform.order.security.JwtProviderManager;
import com.platform.order.security.TokenService;
import com.platform.order.security.WebSecurityConfig;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.security.property.JwtConfig;

@WithJwtMockUser
@WebMvcTest({ProductController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtConfig.class})
class ProductControllerTest {

	final String URI_PREFIX = "/api/products";
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	TokenService tokenService;

	@MockBean
	ProductService productService;
	Long productId;

	@Test
	@DisplayName("상품을 생성한다")
	void testCreate() throws Exception {
		//given
		CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, "C001");
		String body = objectMapper.writeValueAsString(requestDto);
		//when
		ResultActions perform = mockMvc.perform(post(URI_PREFIX)
			.content(body)
			.contentType(MediaType.APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).create(1L, requestDto);
	}

	@DisplayName("상품 생성시 ")
	@Nested
	class CreateProductValidation {

		@DisplayName("이름이 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: name: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failCreateWithBlankName(String name) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto(name, 10L, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("수량이 음수이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: quantity: {0}")
		@ValueSource(longs = {-1, -12})
		void failCreateWithNegativeQuantity(Long quantity) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", quantity, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("수량이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failCreateWithNullQuantity() throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", null, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("가격이 음수이거나 0이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: price: {0}")
		@ValueSource(longs = {-1, 0})
		void failCreateWithNegativePrice(Long price) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, price, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("가격이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failCreateWithNullPrice() throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, null, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("카테고리 코드가 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: categoryCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failCreateWithBlankCategoryCode(String categoryCode) throws Exception {
			//given
			CreateProductRequestDto requestDto = new CreateProductRequestDto("test 상품", 10L, 1000L, categoryCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(CreateProductRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				post(URI_PREFIX)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}

	@Test
	@DisplayName("상품을 수정한다.")
	void testUpdate() throws Exception {
		//given
		productId = 1L;
		UpdateProductRequestDto requestDto = new UpdateProductRequestDto("updating product", 10000L, 1000L,
			"C032");
		//when
		ResultActions perform = mockMvc.perform(
			patch(URI_PREFIX + "/" + productId)
				.content(objectMapper.writeValueAsString(requestDto))
				.contentType(APPLICATION_JSON));
		//then
		perform.andExpect(status().isOk());
		verify(productService, times(1)).update(1L, productId, requestDto);
	}

	@DisplayName("상품 수정시 ")
	@Nested
	class UpdateProductValidation {

		@DisplayName("이름이 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: name: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failUpdateWithBlankName(String name) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto(name, 10L, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("수량이 음수이거나 0이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: quantity: {0}")
		@ValueSource(longs = {-1, 0})
		void failUpdateWithNegativeQuantity(Long quantity) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", quantity, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("수량이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failUpdateWithNullQuantity() throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", null, 1000L, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("가격이 음수이거나 0이면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: price: {0}")
		@ValueSource(longs = {-1, 0})
		void failUpdateWithNegativePrice(Long price) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, price, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("가격이 없으면 BadRequest로 응답한다.")
		@NullAndEmptySource
		void failUpdateWithNullPrice() throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, null, "C001");
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("카테고리 코드가 없으면 BadRequest로 응답한다.")
		@ParameterizedTest(name = "{index}: categoryCode: {0}")
		@ValueSource(strings = {"\t", "\n"})
		void failUpdateWithBlankCategoryCode(String categoryCode) throws Exception {
			//given
			UpdateProductRequestDto requestDto = new UpdateProductRequestDto("test 상품", 10L, 1000L, categoryCode);
			//when
			ResultActions perform = getPerform(requestDto);
			//then
			perform.andExpect(status().isBadRequest());
		}

		ResultActions getPerform(UpdateProductRequestDto requestDto) throws Exception {
			return mockMvc.perform(
				patch(URI_PREFIX+"/"+productId)
					.content(objectMapper.writeValueAsString(requestDto))
					.contentType(APPLICATION_JSON)
			);
		}
	}
}