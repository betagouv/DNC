package fr.gouv.modernisation.dinum.dnc.common.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor gérant le X-Correlation-ID.
 * Ajoute le correlation ID au contexte des logs à l'arrivée d'une requête et l'enlève après l'envoie de la réponse.
 */
public class CorrelationIdInterceptor extends HandlerInterceptorAdapter {

	public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-ID";
	public static final String CORRELATION_ID_VARIABLE_NAME = "correlationId";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		final String correlationId = getCorrelationIdFromHeader(request);
		MDC.put(CORRELATION_ID_VARIABLE_NAME, correlationId);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		MDC.remove(CORRELATION_ID_VARIABLE_NAME);
	}

	private String getCorrelationIdFromHeader(final HttpServletRequest request) {
		String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
		if(StringUtils.isBlank(correlationId)) {
			correlationId = UUID.randomUUID().toString();
		}
		return correlationId;
	}
}
