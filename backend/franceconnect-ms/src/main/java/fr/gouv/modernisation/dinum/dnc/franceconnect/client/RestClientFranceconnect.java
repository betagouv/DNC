package fr.gouv.modernisation.dinum.dnc.franceconnect.client;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.AuthorizationRequest;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenRequestBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client REST pour l'API Franceconnect
 */
@Component
public class RestClientFranceconnect {

	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(RestClientFranceconnect.class);


	/**
	 * URL de base de l'API Franceconnect
	 */
	@Value("${api.franceconnect.baseUrl}")
	private String baseUrl;

	/**
	 * Client ID pour l'API Franceconnect
	 */
	@Value("${api.franceconnect.cliendId}")
	private String cliendId;

	/**
	 * Client secret pour l'API Franceconnect
	 */
	@Value("${api.franceconnect.clientSecret}")
	private String clientSecret;

	/**
	 * URL de callback pour l'application Front du DNC
	 */
	@Value("${dnc.front.callbackUrl}")
	private String dncFrontCallbackUrl;

	/**
	 * Appel du endpoint token de l'API Franceconnect pour récupérer l'access token de l'utilisateur.
	 * @param authorizationRequest {@link AuthorizationRequest} reçu pour la récupération de l'access token
	 * @return objet {@link TokenFranceconnect} correspondant à l'Access token
	 */
	public TokenFranceconnect getAccessToken(AuthorizationRequest authorizationRequest) {
		String url = baseUrl + "/api/v1/token";
		LOGGER.debug(String.format("Tentative de récupération du Token Franceconnect : URL : %s, Authorization Request : %s",url, authorizationRequest));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `content-type` header
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		RestTemplate restTemplate = new RestTemplate();
		try {
			LOGGER.debug(String.format("Envoie de la requête de récupération du Token Franceconnect : URL : %s, Authorization Request : %s",url, authorizationRequest));
			return restTemplate.postForEntity(url,
					new HttpEntity<MultiValueMap<String, String>>(getFormData(authorizationRequest), headers),
					TokenFranceconnect.class).getBody();
		}
		catch (RestClientException rce) {
			LOGGER.error("Une erreur est survenue lors de la récupération du token Franceconnect : Authorization Code : " + authorizationRequest, rce);
		}

		// send POST request
		return null;
	}

	/**
	 * Appel du endpoint userinfo de l'API
	 * @param token token Franceconnect
	 * @return l'objet {@link IdentitePivot} correspondant renvoyé par Franceconnect
	 */
	public IdentitePivot getIdPivot(TokenFranceconnect token) {
		String url = baseUrl + "/api/v1/userinfo?schema=openid";
		LOGGER.debug(String.format("Tentative de récupération du Token Franceconnect : URL : %s, ID Token : %s",url,token.getIdToken()));

		// create headers
		HttpHeaders headers = new HttpHeaders();
		// set `authorization` header
		headers.setBearerAuth(token.getAccessToken());

		// build the request
		HttpEntity<TokenRequestBody> entity = new HttpEntity<>(headers);

		// send GET request
		ResponseEntity<IdentitePivot> response = getRestTemplate().exchange(url, HttpMethod.GET, entity, IdentitePivot.class);

		if(!HttpStatus.OK.equals(response.getStatusCode())){
			LOGGER.error(String.format(
					"Une erreur est survenue lors de la récupération de l'identité pivot avec le token %s (expire in:%s, acces_token:%s, token_type:%s)",
					token.getIdToken(), token.getExpiresIn(), token.getAccessToken(), token.getTokenType()
			));
			return null;
		}

		LOGGER.debug("Récupération OK de l'identité pivot avec le token d'id : "+token.getIdToken());
		return response.getBody();
	}

	/**
	 * Création d'un {@link RestTemplate} basique pour l'appel à Franceconnect
	 * @return un objet {@link RestTemplate} basique
	 */
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(restTemplate.getRequestFactory()));

		return restTemplate;
	}

	/**
	 * Franceconnect n'acceptant que les données au format {@link MediaType#APPLICATION_FORM_URLENCODED},
	 * on utilise une {@link MultiValueMap} qui est un objet géré nativement par {@link RestTemplate} pour
	 * les envoie de données avec ce format.
	 * @param authorizationRequest {@link AuthorizationRequest} reçu pour la récupération de l'access token
	 * @return la {@link MultiValueMap} correspondant au code et au paramétrage de l'application
	 */
	private MultiValueMap<String, String> getFormData(AuthorizationRequest authorizationRequest) {
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "authorization_code");
		formData.add("redirect_uri", StringUtils.defaultIfBlank(authorizationRequest.getRedirectUri(),dncFrontCallbackUrl));
		formData.add("client_id", cliendId);
		formData.add("client_secret", clientSecret);
		formData.add("code", authorizationRequest.getAuthorizationCode());
		return formData;
	}

}
