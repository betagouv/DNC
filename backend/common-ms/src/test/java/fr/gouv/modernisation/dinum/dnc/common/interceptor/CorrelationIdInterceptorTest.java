package fr.gouv.modernisation.dinum.dnc.common.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CorrelationIdInterceptorTest {

	private CorrelationIdInterceptor correlationIdInterceptor = new CorrelationIdInterceptor();

	@Mock
	HttpServletRequest httpServletRequest;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void interceptorRequestWithoutCorrelationIdTest() throws Exception {

		Mockito.when(httpServletRequest.getHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER_NAME)).thenReturn(null);

		boolean response = correlationIdInterceptor.preHandle(httpServletRequest, null, null);

		assertTrue(response);
		assertNotNull(MDC.get("correlationId"));
	}

	@Test
	public void interceptorRequestWithCorrelationIdTest() throws Exception {

		String correlationId= UUID.randomUUID().toString();
		Mockito.when(httpServletRequest.getHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER_NAME)).thenReturn(correlationId);

		boolean response = correlationIdInterceptor.preHandle(httpServletRequest, null, null);

		assertTrue(response);
		assertEquals(correlationId, MDC.get("correlationId"));
	}

}
