package fr.gouv.modernisation.dinum.dnc.situationusager.client;

import fr.gouv.modernisation.dinum.dnc.client.cnam.open.generated.api.client.BeneficiaireApi;
import fr.gouv.modernisation.dinum.dnc.client.cnam.open.generated.api.invoker.ApiClient;
import fr.gouv.modernisation.dinum.dnc.client.cnam.open.generated.api.invoker.auth.ApiKeyAuth;
import fr.gouv.modernisation.dinum.dnc.client.cnam.open.generated.api.invoker.auth.OAuth;
import fr.gouv.modernisation.dinum.dnc.common.client.AbstractClientApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

/**
 * Client REST pour l'API Franceconnecté de la Caisse Nationale d'Assurance Maladie (CNAM).
 */
@Service
@RequestScope
public class RestClientFCCnam extends AbstractClientApi<BeneficiaireApi, ApiClient> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientFCCnam.class);

	/**
	 * Initialisation des valeurs en configuration
	 */
	@Autowired
	public void setupConfiguration(@Value(value = "${api.cnam.baseUrl}") String apiBaseUrl,
								   @Value(value = "${api.cnam.apiKey}") String apiKey) {
		this.setBaseURL(apiBaseUrl);
		this.setApiKeyAuthName("API-Key-Cnam");
		this.setApiKey(apiKey);
		this.setFranceconnectAuthName("Access-Token-France-Connect");
	}

	@Override
	public BeneficiaireApi getApi() {
		return new BeneficiaireApi(getApiClient());
	}

	@Override
	protected ApiClient getApiClient() {
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));
		restTemplate.setInterceptors(Arrays.asList(new HttpRequestInterceptor()));

		final ApiClient apiClient = new ApiClient(restTemplate);

		updateClientAuthentication(apiClient);

		return apiClient.setBasePath(getBaseURL());
	}

	@Override
	protected void interceptRequest(HttpRequest request) {
		// Rien à faire car pas de traitement par interceptor
	}

	@Override
	protected void updateClientAuthentication(ApiClient client) {
		// Api Key
		if(StringUtils.isNotBlank(getApiKeyAuthName())) {
			ApiKeyAuth apiKeyAuth = (ApiKeyAuth) client.getAuthentication(getApiKeyAuthName());
			apiKeyAuth.setApiKey(getApiKey());
		}

		// AOuth pour FranceConnect
		if(StringUtils.isNotBlank(getFranceconnectAuthName())) {
			OAuth oAuth = (OAuth) client.getAuthentication(getFranceconnectAuthName());
			oAuth.setAccessToken(getTokenFranceconnect());
		}

	}

	/**
	 * Wrapper de la méthode {@link BeneficiaireApi#getMe(LocalDate, LocalDate)}.
	 * La date de début est toujours la date du jour.
	 * @return l'objet {@link Beneficiaire} si il existe, sinon {@code null}
	 */
	@Async
	public CompletableFuture<Beneficiaire> getBeneficiaire(String currentTokenFranceconnect) {
		try {
			setTokenFranceconnect(currentTokenFranceconnect);
			return CompletableFuture.completedFuture(getApi().getMe(LocalDate.now(), null));
		}
		catch(RestClientException e) {
			LOGGER.error("Erreur lors de l'appel à l'API de la CNAM", e);
		}

		// Pas de réponse de l'API, on renvoie un future null
		return CompletableFuture.completedFuture(null);
	}
}
