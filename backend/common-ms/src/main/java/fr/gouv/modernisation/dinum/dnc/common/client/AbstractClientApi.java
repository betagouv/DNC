package fr.gouv.modernisation.dinum.dnc.common.client;

import fr.gouv.modernisation.dinum.dnc.client.generated.api.invoker.ApiClient;
import fr.gouv.modernisation.dinum.dnc.client.generated.api.invoker.auth.Authentication;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Classe abstraite permettant d'avoir un client à une API Rest
 * @param <A> l'interface API ciblé par le client
 * @param <C> l'interface ApiClient par le client
 */
public abstract class AbstractClientApi<A,C> {

	/**
	 * Base URL de l'API
	 */
	protected String baseURL;

	/**
	 * API Key a utilisé par le client
	 */
	protected String apiKey;

	/**
	 * Nom du Header pour l'API Key.
	 * Par défaut vaut X-API-Key.
	 */
	protected String apiHeaderName = "X-API-Key";

	/**
	 * Nom de l'authentification pour l'API Key
	 */
	protected String apiKeyAuthName = "";

	/**
	 * Token Franceconnect à inclure si l'API cible est Franceconnectée
	 */
	protected String tokenFranceconnect;

	/**
	 * Le nom du schéma d'authentification pour l'API Franceconnectée
	 */
	protected String franceconnectAuthName;

	/**
	 * API ciblée
	 * @return l'API ciblé par le client
	 */
	public abstract A getApi();

	/**
	 * @return {@link ApiClient} correspondant au service ciblé
	 */
	protected abstract C getApiClient();

	/**
	 * Mise à jour des {@link Authentication} correspondant au client
	 */
	protected abstract void updateClientAuthentication(C client);

	/**
	 * Méthode permettant d'intercepter les requêtes envoyées par le client
	 * @param request requête qui sera envoyée par le client
	 */
	protected abstract void interceptRequest(HttpRequest request);

	/**
	 * Getter du champ baseURL
	 * return {@link String} la valeur du champ baseURL
	 */
	public String getBaseURL() {
		return baseURL;
	}

	/**
	 * Setter du champ baseURL
	 *
	 * @param baseURL valeur à setter
	 */
	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	/**
	 * Getter du champ apiKey
	 * return {@link String} la valeur du champ apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * Setter du champ apiKey
	 *
	 * @param apiKey valeur à setter
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * Getter du champ apiHeaderName
	 * return {@link String} la valeur du champ apiHeaderName
	 */
	public String getApiHeaderName() {
		return apiHeaderName;
	}

	/**
	 * Setter du champ apiHeaderName
	 *
	 * @param apiHeaderName valeur à setter
	 */
	public void setApiHeaderName(String apiHeaderName) {
		this.apiHeaderName = apiHeaderName;
	}

	/**
	 * Getter du champ apiKeyAuthName
	 * return {@link String} la valeur du champ apiKeyAuthName
	 */
	public String getApiKeyAuthName() {
		return apiKeyAuthName;
	}

	/**
	 * Setter du champ apiKeyAuthName
	 *
	 * @param apiKeyAuthName valeur à setter
	 */
	public void setApiKeyAuthName(String apiKeyAuthName) {
		this.apiKeyAuthName = apiKeyAuthName;
	}

	/**
	 * Getter du champ tokenFranceconnect
	 * return {@link String} la valeur du champ tokenFranceconnect
	 */
	public String getTokenFranceconnect() {
		return tokenFranceconnect;
	}

	/**
	 * Setter du champ tokenFranceconnect
	 *
	 * @param tokenFranceconnect valeur à setter
	 */
	public void setTokenFranceconnect(String tokenFranceconnect) {
		this.tokenFranceconnect = tokenFranceconnect;
	}

	/**
	 * Getter du champ franceconnectAuthName
	 * return {@link String} la valeur du champ franceconnectAuthName
	 */
	public String getFranceconnectAuthName() {
		return franceconnectAuthName;
	}

	/**
	 * Setter du champ franceconnectAuthName
	 *
	 * @param franceconnectAuthName valeur à setter
	 */
	public void setFranceconnectAuthName(String franceconnectAuthName) {
		this.franceconnectAuthName = franceconnectAuthName;
	}

	/**
	 * Interceptor interne pour l'ajout du correlation ID et de l'API Key.
	 */
	public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {
		/**
		 * @see ClientHttpRequestInterceptor#intercept(HttpRequest,
		 *      byte[], ClientHttpRequestExecution)
		 */
		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {

			interceptRequest(request);

			return execution.execute(request, body);
		}
	}
}
