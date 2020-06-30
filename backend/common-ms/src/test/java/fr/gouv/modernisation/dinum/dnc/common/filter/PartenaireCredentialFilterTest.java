package fr.gouv.modernisation.dinum.dnc.common.filter;

import fr.gouv.modernisation.dinum.dnc.client.partenaire.generated.api.client.PartenaireApi;
import fr.gouv.modernisation.dinum.dnc.common.client.PartenaireMsClientDnc;
import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.exception.UnknownException;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.GrantAccessPartner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests Unitaires de {@link PartenaireCredentialFilterTest}
 */
public class PartenaireCredentialFilterTest {

	public static final String HTTP_TEST_ENDPOINT = "http://test/endpoint";
	HttpServletRequest httpServletRequest;

	ServletResponse response;

	FilterChain chain;

	private static String TEST_VALUE = "Test";

	private static String BASIC_AUTHORIZATION_VALUE = "Basic "+ Base64.getEncoder().encodeToString((TEST_VALUE+":"+TEST_VALUE).getBytes(StandardCharsets.UTF_8));

	@InjectMocks
	PartenaireCredentialFilter partenaireCredentialFilter = new PartenaireCredentialFilter(TEST_VALUE,TEST_VALUE);

	@Mock
	PartenaireMsClientDnc partenaireMsClientDnc;

	@Mock
	PartenaireApi partenaireApi;

	@BeforeEach
	public void init() {
		partenaireCredentialFilter = new PartenaireCredentialFilter(TEST_VALUE,TEST_VALUE);
		MockitoAnnotations.initMocks(this);

		httpServletRequest = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		chain = new MockFilterChain();

		ReflectionTestUtils.setField(partenaireCredentialFilter, "partenaireMsClientDnc", partenaireMsClientDnc);
		partenaireCredentialFilter.setContinueFilterChainOnUnsuccessfulAuthentication(false);

		Mockito.when(partenaireMsClientDnc.getApi()).thenReturn(partenaireApi);

		SecurityContextHolder.clearContext();
	}

	@Test
	public void requeteOKTest() {

		// Mock du retour du client
		GrantAccessPartner grantAccessPartner = new GrantAccessPartner();
		grantAccessPartner.setId(UUID.randomUUID().toString());
		Mockito.when(partenaireApi.checkCredentialsPartenaire(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any()))
				.thenReturn(grantAccessPartner);

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, BASIC_AUTHORIZATION_VALUE)
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertDoesNotThrow(
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKORetourPartenaireMSNullTest() {

		// Mock du retour du client
		Mockito.when(partenaireApi.checkCredentialsPartenaire(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any()))
				.thenReturn(null);

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, BASIC_AUTHORIZATION_VALUE)
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertDoesNotThrow(
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKOCredentialsRefusesTest() {

		// Mock du retour du client
		Mockito.when(partenaireApi.checkCredentialsPartenaire(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any()))
				.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Access Denied"));

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, BASIC_AUTHORIZATION_VALUE)
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertThrows(UnknownException.class,
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKOHeaderParametreSiretPartenaireManquantTest() {

		// Mock du retour du client
		GrantAccessPartner grantAccessPartner = new GrantAccessPartner();
		grantAccessPartner.setId(UUID.randomUUID().toString());
		Mockito.when(partenaireApi.checkCredentialsPartenaire(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any()))
				.thenReturn(grantAccessPartner);

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, BASIC_AUTHORIZATION_VALUE)
				.buildRequest(new MockServletContext());


		assertThrows(BadRequestException.class,
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKOHeaderAuthorizationManquantTest() {

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertThrows(BadRequestException.class,
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKOHeaderAuthorizationNeCommencePasParBasicTest() {

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, "ThisIsNotBasic"+ BASIC_AUTHORIZATION_VALUE)
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertThrows(BadRequestException.class,
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void requeteKOHeaderAuthorizationValeurEnBase64IncorrecteTest() {

		httpServletRequest = MockMvcRequestBuilders
				.get(HTTP_TEST_ENDPOINT)
				.header(PartenaireCredentialFilter.HEADER_AUTHORIZATION, "Basic "+ Base64.getEncoder().encodeToString((TEST_VALUE).getBytes(StandardCharsets.UTF_8)))
				.param(PartenaireCredentialFilter.PARAMETER_SIRET_PARTENAIRE, TEST_VALUE)
				.buildRequest(new MockServletContext());


		assertThrows(BadRequestException.class,
				() -> partenaireCredentialFilter.doFilter(httpServletRequest, response, chain)
		);
	}

}
