package com.platform.order.review.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.platform.order.common.config.WebSecurityConfig;
import com.platform.order.common.security.JwtProviderManager;
import com.platform.order.common.security.constant.JwtConfig;
import com.platform.order.review.controller.dto.request.CreateReviewRequestDto;
import com.platform.order.review.controller.dto.request.ReviewPageRequestDto;
import com.platform.order.review.controller.dto.request.UpdateReviewRequestDto;
import com.platform.order.review.service.ReviewService;
import com.platform.order.security.WithJwtMockUser;
import com.platform.order.testenv.ControllerTest;
import com.platform.order.utils.ParameterUtils;

@WebMvcTest({ReviewController.class,
	WebSecurityConfig.class,
	JwtProviderManager.class,
	JwtConfig.class})
class ReviewControllerTest extends ControllerTest {
	final String URI_PREFIX = "/api/reviews";

	@MockBean
	ReviewService reviewService;

	ResourceLoader resourceLoader = new DefaultResourceLoader();
	MultipartFile mockfile;
	List<MultipartFile> mockfiles;

	@BeforeEach
	public void setUp() throws IOException {
		String fileName = "test.png";
		Resource resource = resourceLoader.getResource("classpath:/static/" + fileName);
		mockfile = new MockMultipartFile("file", fileName, null, resource.getInputStream());
		mockfiles = Stream.generate(() -> mockfile).limit(5).toList();
	}

	@WithJwtMockUser
	@DisplayName("이미지 파일을 포함하여 리뷰를 생성한다.")
	@Test
	void create() throws Exception {
		// given
		CreateReviewRequestDto createReviewRequestDto = new CreateReviewRequestDto(1L, 3, "test");
		MockMultipartFile createReviewRequestJson = new MockMultipartFile(
			"createReviewRequest",
			null,
			APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(createReviewRequestDto));
		// when
		ResultActions perform = mockMvc.perform(
			multipart(URI_PREFIX)
				.file((MockMultipartFile)mockfile)
				.file(createReviewRequestJson)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
		);
		// then
		perform.andExpect(status().isOk());
	}

	@WithJwtMockUser
	@Nested
	@DisplayName("리뷰를 생성할 때, ")
	class CreateReviewValidation {
		@DisplayName("createReviewRequest가 null이면 BadRequest로 응답한다")
		@Test
		void failNullRequestDto() throws Exception {
			//given
			//when
			ResultActions perform = mockMvc.perform(
				multipart(URI_PREFIX)
					.file((MockMultipartFile)mockfile)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("createReviewRequest 객체의 orderProductId가 null이면 BadRequest로 응답한다")
		@Test
		void failNullOrderProductIdInRequestDto() throws Exception {
			//given
			CreateReviewRequestDto createReviewRequestDto = new CreateReviewRequestDto(null, 3, "");
			MockMultipartFile createReviewRequestJson = getCreateReviewRequest(createReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("createReviewRequest 객체의 score가 null이면 BadRequest로 응답한다")
		@Test
		void failNullScoreInRequestDto() throws Exception {
			//given
			CreateReviewRequestDto createReviewRequestDto = new CreateReviewRequestDto(1L, null, "");
			MockMultipartFile createReviewRequestJson = getCreateReviewRequest(createReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("createReviewRequest 객체의 score가 1미만 6이상이면 BadRequest로 응답한다")
		@ParameterizedTest(name = "{index}: score {0}")
		@ValueSource(ints = {-1, 0, 6})
		void failNullOutRangeScoreInRequestDto(Integer score) throws Exception {
			//given
			CreateReviewRequestDto createReviewRequestDto = new CreateReviewRequestDto(1L, null, "");
			MockMultipartFile createReviewRequestJson = getCreateReviewRequest(createReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("createReviewRequest 객체의 content가 Null 또는 공백많이 들어 있으면 BadRequest로 응답한다")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}:  {0}")
		void failNullAndEmptyContentInRequestDto(String content) throws Exception {
			//given
			CreateReviewRequestDto createReviewRequestDto = new CreateReviewRequestDto(1L, 3, content);
			MockMultipartFile createReviewRequestJson = getCreateReviewRequest(createReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		private MockMultipartFile getCreateReviewRequest(CreateReviewRequestDto createReviewRequestDto) throws
			JsonProcessingException {
			return new MockMultipartFile(
				"createReviewRequest",
				null,
				APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(createReviewRequestDto)
			);
		}

		private ResultActions getPerform(MockMultipartFile createReviewRequestJson) throws Exception {
			return mockMvc.perform(
				multipart(URI_PREFIX)
					.file((MockMultipartFile)mockfile)
					.file(createReviewRequestJson)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			);
		}
	}

	@WithJwtMockUser
	@DisplayName("이미지 파일을 포함하여 리뷰를 수정한다.")
	@Test
	void update() throws Exception {
		// given
		Long reviewId = 1L;
		UpdateReviewRequestDto updateReviewRequest = new UpdateReviewRequestDto(1L, 3, "test");
		MockMultipartFile updateReviewRequestJson = new MockMultipartFile(
			"updateReviewRequest",
			null,
			APPLICATION_JSON_VALUE,
			objectMapper.writeValueAsBytes(updateReviewRequest));
		// when
		ResultActions perform = mockMvc.perform(
			multipart(HttpMethod.PATCH, URI_PREFIX + "/" + reviewId)
				.file((MockMultipartFile)mockfile)
				.file(updateReviewRequestJson)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
		);
		// then
		perform.andExpect(status().isOk());
	}

	@WithJwtMockUser
	@Nested
	@DisplayName("리뷰를 수정할 때,")
	class UpdateReviewValidation {
		@DisplayName("updateReviewRequest가 null이면 BadRequest로 응답한다")
		@Test
		void failNullRequestDto() throws Exception {
			//given
			//when
			ResultActions perform = mockMvc.perform(
				multipart(URI_PREFIX)
					.file((MockMultipartFile)mockfile)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("updateReviewRequest 객체의 orderProductId가 null이면 BadRequest로 응답한다")
		@Test
		void failNullOrderProductIdInRequestDto() throws Exception {
			//given
			UpdateReviewRequestDto updateReviewRequest = new UpdateReviewRequestDto(null, 3, "");
			MockMultipartFile createReviewRequestJson = getUpdateReviewRequest(updateReviewRequest);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("updateReviewRequest 객체의 score가 null이면 BadRequest로 응답한다")
		@Test
		void failNullScoreInRequestDto() throws Exception {
			//given
			UpdateReviewRequestDto updateReviewRequestDto = new UpdateReviewRequestDto(1L, null, "");
			MockMultipartFile createReviewRequestJson = getUpdateReviewRequest(updateReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("updateReviewRequest 객체의 score가 1미만 6이상이면 BadRequest로 응답한다")
		@ParameterizedTest(name = "{index}: score {0}")
		@ValueSource(ints = {-1, 0, 6})
		void failNullOutRangeScoreInRequestDto(Integer score) throws Exception {
			//given
			UpdateReviewRequestDto updateReviewRequestDto = new UpdateReviewRequestDto(1L, null, "");
			MockMultipartFile createReviewRequestJson = getUpdateReviewRequest(updateReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		@DisplayName("updateReviewRequest 객체의 content가 Null 또는 공백많이 들어 있으면 BadRequest로 응답한다")
		@NullAndEmptySource
		@ParameterizedTest(name = "{index}:  {0}")
		void failNullAndEmptyContentInRequestDto(String content) throws Exception {
			//given
			UpdateReviewRequestDto createReviewRequestDto = new UpdateReviewRequestDto(1L, 3, content);
			MockMultipartFile createReviewRequestJson = getUpdateReviewRequest(createReviewRequestDto);
			//when
			ResultActions perform = getPerform(createReviewRequestJson);
			//then
			perform.andExpect(status().isBadRequest());
		}

		private MockMultipartFile getUpdateReviewRequest(UpdateReviewRequestDto createReviewRequestDto) throws
			JsonProcessingException {
			return new MockMultipartFile(
				"createReviewRequest",
				null,
				APPLICATION_JSON_VALUE,
				objectMapper.writeValueAsBytes(createReviewRequestDto)
			);
		}

		private ResultActions getPerform(MockMultipartFile createReviewRequestJson) throws Exception {
			return mockMvc.perform(
				multipart(URI_PREFIX)
					.file((MockMultipartFile)mockfile)
					.file(createReviewRequestJson)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
			);
		}
	}

	@Test
	@DisplayName("상품에 해당하는 리뷰들을 조회한다.")
	void testReadAll() throws Exception {
		//given
		Long productId = 1L;
		ReviewPageRequestDto pageRequest = new ReviewPageRequestDto(1, 10, null, null);
		var params = new ParameterUtils<ReviewPageRequestDto>()
			.toMultiValueParams(objectMapper, pageRequest);
		//when
		ResultActions perform = mockMvc.perform(
			get(URI_PREFIX + "/" + productId.toString())
				.params(params)
				.contentType(APPLICATION_JSON)
		);
		//then
		perform.andExpect(status().isOk());
	}

	@Nested
	@DisplayName("상품에 해당하는 리뷰들을 조회할 때,")
	class PageRequestValidation {
		@DisplayName("별점이 1미만 이거나 5를 초과하면 BadRequest 로 응답한다")
		@ParameterizedTest(name = "{index}: score {0}")
		@ValueSource(ints = {-1, 6})
		void testLower1AndGrater5(int score) throws Exception {
			//given
			Long productId = 1L;
			ReviewPageRequestDto pageRequest = new ReviewPageRequestDto(1, 10, score, null);
			var params = new ParameterUtils<ReviewPageRequestDto>()
				.toMultiValueParams(objectMapper, pageRequest);
			//when
			ResultActions perform = mockMvc.perform(
				get(URI_PREFIX + "/" + productId.toString())
					.params(params)
					.contentType(APPLICATION_JSON)
			);
			//then
			perform.andExpect(status().isBadRequest());
		}
	}
}