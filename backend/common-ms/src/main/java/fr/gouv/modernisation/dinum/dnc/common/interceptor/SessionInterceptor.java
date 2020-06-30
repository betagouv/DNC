package fr.gouv.modernisation.dinum.dnc.common.interceptor;

import fr.gouv.modernisation.dinum.dnc.common.client.FranceconnectMsClientDnc;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Filtre permettant de vérifier que la session dans le header est correcte vis à vis du Backend du DNC.
 * Utilise un client vers le microservice Franceconnect-MS pour réaliser les vérifications.
 */
public class SessionInterceptor extends HandlerInterceptorAdapter {

	/**
	 * Logger {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionInterceptor.class);

	/**
	 * Nom du header où est stocké l'identifiant de session
	 */
	public static final String HEADER_NAME = "DNC-ID-SESSION";

	/**
	 * Client vers le microservice Franceconnect-MS
	 */
	private FranceconnectMsClientDnc franceconnectMsClient;

	/**
	 * Constructeur de base
	 * @param baseUrlFranceconnectMs base URL pour le microservice Franceconnect-MS
	 * @param apiKey API Key à utiliser lors des appels
	 */
	public SessionInterceptor(String baseUrlFranceconnectMs, String apiKey) {
		this.franceconnectMsClient = new FranceconnectMsClientDnc(baseUrlFranceconnectMs, apiKey);
	}

	/**
	 * Lecture du Header possédant l'id de session, récupération de l'identité pivot et stockage dans le contexte du
	 * {@link SecurityContextHolder}.
	 * Si le header est absent ou que la session n'existe plus, lève une exception {@link ResponseStatusException}.
	 * @param request requête entrante
	 * @param response réponse de la requête, non utilisé
	 * @param handler gestionnaire de la requête, non utilisé
	 * @return {@code true} si la session existe, sinon lève une exception
	 * @throws Exception lève une exception {@link ResponseStatusException} avec le {@link HttpStatus#NOT_FOUND} si la session n'existe pas
	 * et avec le {@link HttpStatus#BAD_REQUEST} si l'header est absent
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if("OPTIONS".equals(request.getMethod())) {
			return true;
		}

		String idSession = request.getHeader(HEADER_NAME);
		if(Objects.isNull(idSession)) {
			idSession = request.getHeader(HEADER_NAME.toLowerCase());
		}

		if(Objects.isNull(idSession)) {
			LOGGER.error("Le header d'identification est absent de la requête");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le header d'identification est absent de la requête");
		}

		SessionUsager sessionUsager = getSessionUsagerFromFranceconnectMs(idSession);

		if(Objects.isNull(sessionUsager)) {
			LOGGER.error("Aucune session associée à l'identifiant {}", idSession);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Aucune session associée à l'identifiant %s", idSession));
		}
		SecurityContextHolder.getContext()
				.setAuthentication(new PreAuthenticatedAuthenticationToken(sessionUsager, idSession));

		return true;
	}

	/**
	 * Renvoie les données de la session active via Franceconnect-MS si elle existe, sinon renvoie {@code null}
	 * @param idSession l'identifiant de la session
	 * @return l'objet {@link SessionUsager} correspondant à la session, sinon {@code null}
	 */
	private SessionUsager getSessionUsagerFromFranceconnectMs(String idSession) {
		try {
			return franceconnectMsClient.getApi().checkSession(idSession);
		}
		catch (HttpClientErrorException e) {
			LOGGER.error("Erreur lors de l'appel à Franceconnect-MS", e);
		}
		return null;
	}


	/**
	 * Nettoyage du contexte après envoie de la réponse à la requête.
	 * @param request requête entrante
	 * @param response réponse de la requête
	 * @param handler gestionnaire de la requête
	 * @param ex exception liée à la requête
	 * @throws Exception pas d'exception pour cette implémentation.
	 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter#afterCompletion(HttpServletRequest, HttpServletResponse, Object, Exception)
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		super.afterCompletion(request, response, handler, ex);
		SecurityContextHolder.clearContext();
	}
}
