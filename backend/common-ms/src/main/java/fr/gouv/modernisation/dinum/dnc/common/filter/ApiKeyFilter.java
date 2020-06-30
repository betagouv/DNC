package fr.gouv.modernisation.dinum.dnc.common.filter;

import fr.gouv.modernisation.dinum.dnc.common.interceptor.CorrelationIdInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Filtre HTTP ne laissant passer que les appels utilisant une API Key valide vis à vis de la configuration.
 */
public class ApiKeyFilter extends AbstractPreAuthenticatedProcessingFilter {

	/**
	 * Logger {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyFilter.class);

	/**
	 * Nom du Header utilisé pour lire l'API Key
	 */
	public static final String HEADER_NAME = "X-API-Key";

	/**
	 * Map des API Keys autorisées par applications
	 */
	private Map<String, String> apiKeysByApp = new HashMap<>();

	/**
	 * Constructeur avec la map des apikeys à utiliser
	 * @param apiKeysByApp {@link Map} des API Keys à utiliser pour valider les appels
	 */
	public ApiKeyFilter(Map<String, String> apiKeysByApp){
		Assert.notNull(apiKeysByApp, "La Map des API Keys ne peut pas être null");
		Assert.isTrue(apiKeysByApp.values().size() == new HashSet<>(apiKeysByApp.values()).size(),
				"Les valeurs de la map doivent être unique");

		if(apiKeysByApp.isEmpty()) {
			LOGGER.warn("Aucune API Key détecté, tous les appels sur endpoints sécurisés seront refusés");
		}

		this.apiKeysByApp = apiKeysByApp;
		this.setAuthenticationManager(authentication -> {
			String apiKey = (String) authentication.getPrincipal();
			if(!getApiKeysByApp().containsValue(apiKey)) {
				LOGGER.error(String.format("L'API Key <%s> n'est pas connue du systèmes", apiKey));
				throw new BadCredentialsException(String.format("L'API Key <%s> n'est pas connue du systèmes", apiKey));
			}

			if(LOGGER.isDebugEnabled()) {
				String appName = getApiKeysByApp().entrySet().stream()
						.filter(entry -> Objects.equals(entry.getValue(), apiKey))
						.map(Map.Entry::getKey)
						.findFirst().orElse("Erreur Application Inconnue");
				LOGGER.debug(String.format("L'application %s a été autorisée", appName));
			}
			authentication.setAuthenticated(true);
			return authentication;
		});
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		// Comme le filter passe avant l'interceptor, on récupère le correlation ID dans la requete et on l'ajoute au contexte
		MDC.put(CorrelationIdInterceptor.CORRELATION_ID_VARIABLE_NAME,
				request.getHeader(CorrelationIdInterceptor.CORRELATION_ID_HEADER_NAME));

		return request.getHeader(HEADER_NAME);
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		return "N/A";
	}

	/**
	 * Getter du champ apiKeysByApp
	 * return {@link Map} la valeur du champ apiKeysByApp
	 */
	public Map<String, String> getApiKeysByApp() {
		return apiKeysByApp;
	}

	/**
	 * Setter du champ apiKeysByApp
	 *
	 * @param apiKeysByApp valeur à setter
	 */
	public void setApiKeysByApp(Map<String, String> apiKeysByApp) {
		this.apiKeysByApp = apiKeysByApp;
	}
}
