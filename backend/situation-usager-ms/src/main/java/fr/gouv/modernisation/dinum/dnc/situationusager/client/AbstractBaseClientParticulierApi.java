package fr.gouv.modernisation.dinum.dnc.situationusager.client;

import fr.gouv.modernisation.dinum.dnc.client.particulier.open.generated.api.invoker.ApiClient;
import fr.gouv.modernisation.dinum.dnc.common.client.AbstractClientApi;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * Classe abstraite pour les clients aux API d'API Particuliers non franceconnectée.
 * @param <A> l'interface de l'API ciblée
 */
public abstract class AbstractBaseClientParticulierApi<A> extends AbstractClientApi<A, ApiClient> {

	@Override
	protected ApiClient getApiClient() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		restTemplate.setInterceptors(Arrays.asList(new HttpRequestInterceptor()));

		final ApiClient apiClient = new ApiClient(restTemplate);

		return apiClient.setBasePath(getBaseURL());
	}

	@Override
	public void interceptRequest(HttpRequest request) {
		// Rien à faire car l'API gère l'API Key et l'X-User via paramètre de méthode.
	}

	@Override
	protected void updateClientAuthentication(ApiClient client) {
		// Rien à faire car l'API gère l'API Key et l'X-User via paramètre de méthode.
	}
}
