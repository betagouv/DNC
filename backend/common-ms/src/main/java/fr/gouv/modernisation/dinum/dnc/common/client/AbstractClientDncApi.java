package fr.gouv.modernisation.dinum.dnc.common.client;

import fr.gouv.modernisation.dinum.dnc.client.generated.api.invoker.ApiClient;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.CorrelationIdInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Classe abstraite permettant d'avoir un client à une API Rest
 * @param <T> l'interface API ciblé par le client
 */
public abstract class AbstractClientDncApi<T> extends AbstractClientApi<T, ApiClient> {

	/**
	 * API ciblée
	 * @return l'API ciblé par le client
	 */
	public abstract T getApi();

	/**
	 * @return {@link ApiClient} correspondant au service ciblé
	 */
	protected ApiClient getApiClient() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		restTemplate.setInterceptors(Arrays.asList(new HttpRequestInterceptor()));

		final ApiClient apiClient = new ApiClient(restTemplate);

		return apiClient.setBasePath(getBaseURL());
	}

	@Override
	protected void updateClientAuthentication(ApiClient client) {
		// Rien à faire tout est fait via l'interceptor
	}

	/**
	 * Méthode permettant d'intercepter les requêtes envoyées par le client
	 * @param request requête qui sera envoyée par le client
	 */
	public void interceptRequest(HttpRequest request) {
		request.getHeaders().add(CorrelationIdInterceptor.CORRELATION_ID_HEADER_NAME, String.valueOf(MDC.get(CorrelationIdInterceptor.CORRELATION_ID_VARIABLE_NAME)));
		if(StringUtils.isNotBlank(getApiKey())) {
			request.getHeaders().add(getApiHeaderName(), String.valueOf(getApiKey()));
		}
	}
}
