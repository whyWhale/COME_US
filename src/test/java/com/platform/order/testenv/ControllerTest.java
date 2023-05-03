package com.platform.order.testenv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.platform.order.authentication.service.AuthService;
import com.platform.order.common.security.service.TokenService;
import com.platform.order.user.service.UserService;

public class ControllerTest {
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	@MockBean
	protected AuthService authService;

	@MockBean
	protected TokenService tokenService;

	@MockBean
	protected UserService userService;
}
