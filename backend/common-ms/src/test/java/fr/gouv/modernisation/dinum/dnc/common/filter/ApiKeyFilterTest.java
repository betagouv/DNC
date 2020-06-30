package fr.gouv.modernisation.dinum.dnc.common.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests Unitaires de {@link ApiKeyFilter}
 */
@TestPropertySource(properties = "logging.level.root:DEBUG")
public class ApiKeyFilterTest {

	public static final String HTTP_TEST_ENDPOINT = "http://test/endpoint";
	HttpServletRequest httpServletRequest;

	ServletResponse response;

	FilterChain chain;

	@BeforeEach
	public void init() {
		httpServletRequest = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();

		Configurator.setLevel(ApiKeyFilter.class.getName(), Level.DEBUG);

		SecurityContextHolder.clearContext();
	}

	@Test
	public void aucuneApiKeyTest() {
		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(ApiKeyFilter.HEADER_NAME, "test").buildRequest(new MockServletContext());

		ApiKeyFilter apiKeyFilter = new ApiKeyFilter(new HashMap<>());
		apiKeyFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);

		assertThrows(BadCredentialsException.class,
				() -> apiKeyFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void mauvaiseApiKeyTest() {
		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(ApiKeyFilter.HEADER_NAME, "test").buildRequest(new MockServletContext());

		Map<String, String> apiKeys = new HashMap<>();
		apiKeys.put("mauvaiseApp1", UUID.randomUUID().toString());
		apiKeys.put("mauvaiseApp2", UUID.randomUUID().toString());
		ApiKeyFilter apiKeyFilter = new ApiKeyFilter(apiKeys);
		apiKeyFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);

		assertThrows(BadCredentialsException.class,
				() -> apiKeyFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void apiKeyOkTest() {
		Map<String, String> apiKeys = new HashMap<>();
		apiKeys.put("testApp1", UUID.randomUUID().toString());
		apiKeys.put("testApp2", UUID.randomUUID().toString());
		ApiKeyFilter apiKeyFilter = new ApiKeyFilter(apiKeys);

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(ApiKeyFilter.HEADER_NAME, apiKeys.get("testApp1")).buildRequest(new MockServletContext());

		assertDoesNotThrow(() -> apiKeyFilter.doFilter(httpServletRequest, response, chain));
	}

}
