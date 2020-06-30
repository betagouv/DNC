package fr.gouv.modernisation.dinum.dnc.common.filter;

import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.exception.ForbiddenAccesException;
import fr.gouv.modernisation.dinum.dnc.common.exception.NotFoundException;
import fr.gouv.modernisation.dinum.dnc.common.exception.UnknownException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionHandlerFilterTest {

	@InjectMocks
	ExceptionHandlerFilter exceptionHandlerFilter;

	HttpServletRequest httpServletRequest;

	HttpServletResponse response;

	@Mock
	FilterChain chain;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);

		httpServletRequest = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void testOK() {
		assertDoesNotThrow(
				() -> exceptionHandlerFilter.doFilter(httpServletRequest, response, chain)
		);
	}

	@Test
	public void testExceptionsSpecifiqueDNCUnknownException() throws IOException, ServletException {
		Mockito.doThrow(new UnknownException("ErreurInterne",
				"Impossible d'identifier le partenaire via le système DNC: siret : Test, clientId : Test",
				HttpStatus.INTERNAL_SERVER_ERROR,
				null))
				.when(chain).doFilter(ArgumentMatchers.any(),ArgumentMatchers.any());
		;

		exceptionHandlerFilter.doFilter(httpServletRequest, response, chain);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertNotNull(response.getOutputStream().toString());
	}

	@Test
	public void testExceptionsSpecifiqueDNCForbiddenAccesException() throws IOException, ServletException {
		Mockito.doThrow(new ForbiddenAccesException("Forbidden",
				"ForbiddenAccesException"))
				.when(chain).doFilter(ArgumentMatchers.any(),ArgumentMatchers.any());
		;

		exceptionHandlerFilter.doFilter(httpServletRequest, response, chain);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertNotNull(response.getOutputStream().toString());
	}

	@Test
	public void testExceptionsSpecifiqueDNCNotFoundException() throws IOException, ServletException {
		Mockito.doThrow(new NotFoundException("NotFound",
				"NotFoundException"))
				.when(chain).doFilter(ArgumentMatchers.any(),ArgumentMatchers.any());
		;

		exceptionHandlerFilter.doFilter(httpServletRequest, response, chain);

		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertNotNull(response.getOutputStream().toString());
	}

	@Test
	public void testExceptionsSpecifiqueDNCBadRequestException() throws IOException, ServletException {
		Mockito.doThrow(new BadRequestException("BadRequest"))
				.when(chain).doFilter(ArgumentMatchers.any(),ArgumentMatchers.any());
				;

		exceptionHandlerFilter.doFilter(httpServletRequest, response, chain);

		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertNotNull(response.getOutputStream().toString());
	}

	@Test
	public void testExceptionGeneralDNC() throws IOException, ServletException {
		Mockito.doThrow(new NullPointerException("Exception non gérée"))
				.when(chain).doFilter(ArgumentMatchers.any(),ArgumentMatchers.any());
		;

		exceptionHandlerFilter.doFilter(httpServletRequest, response, chain);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertNotNull(response.getOutputStream().toString());
	}
}
