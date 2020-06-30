package fr.gouv.modernisation.dinum.dnc.common.interceptor;

import fr.gouv.modernisation.dinum.dnc.client.franceconnect.generated.api.client.UserSessionApi;
import fr.gouv.modernisation.dinum.dnc.common.client.FranceconnectMsClientDnc;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SessionInterceptorTest {

	@InjectMocks
	private SessionInterceptor sessionInterceptor = new SessionInterceptor("test","test");

	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	FranceconnectMsClientDnc franceconnectMsClient;

	@Mock
	UserSessionApi userSessionApi;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		SecurityContextHolder.clearContext();
	}

	@Test
	public void interceptorRequestWithoutIdSessionTest() {

		Mockito.when(httpServletRequest.getHeader(SessionInterceptor.HEADER_NAME)).thenReturn(null);

		ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> sessionInterceptor.preHandle(httpServletRequest, null, null));

		assertNotNull(exception);
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	public void interceptorRequestWithoutSessionTest() {

		Mockito.when(httpServletRequest.getHeader(SessionInterceptor.HEADER_NAME)).thenReturn("test");
		Mockito.when(franceconnectMsClient.getApi()).thenReturn(userSessionApi);
		Mockito.when(userSessionApi.checkSession(ArgumentMatchers.any())).thenReturn(null);

		ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> sessionInterceptor.preHandle(httpServletRequest, null, null));

		assertNotNull(exception);
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	public void interceptorRequestOKTest() throws Exception {

		SessionUsager sessionUsager = new SessionUsager();
		sessionUsager.setIdSession(UUID.randomUUID().toString());

		Mockito.when(httpServletRequest.getHeader(SessionInterceptor.HEADER_NAME)).thenReturn(sessionUsager.getIdSession());
		Mockito.when(franceconnectMsClient.getApi()).thenReturn(userSessionApi);
		Mockito.when(userSessionApi.checkSession(sessionUsager.getIdSession())).thenReturn(sessionUsager);

		boolean result = sessionInterceptor.preHandle(httpServletRequest, null, null);

		assertTrue(result);
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals(sessionUsager, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		assertEquals(sessionUsager.getIdSession(), SecurityContextHolder.getContext().getAuthentication().getCredentials());
	}

}
