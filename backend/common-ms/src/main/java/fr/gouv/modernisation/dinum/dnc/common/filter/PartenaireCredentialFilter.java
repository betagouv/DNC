package fr.gouv.modernisation.dinum.dnc.common.filter;

import fr.gouv.modernisation.dinum.dnc.common.client.PartenaireMsClientDnc;
import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.exception.UnknownException;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.GrantAccessPartner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Filtre pour les connexions entrantes sur les endpoints Partenaires
 */
public class PartenaireCredentialFilter extends RequestHeaderAuthenticationFilter {

	/**
	 * Logger {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PartenaireCredentialFilter.class);

	/**
	 * Header contenant les credentials du partenaire
	 */
	public static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * Header contenant le SIRET du partenaire
	 */
	public static final String PARAMETER_SIRET_PARTENAIRE = "siretPartenaire";

	/**
	 * Client de l'API Partenaire-MS pour le check des credentials
	 */
	private PartenaireMsClientDnc partenaireMsClientDnc;

	/**
	 * Constructeur de base
	 * @param baseUrlPartenaireMs base URL pour le microservice Partenaire-MS
	 * @param apiKeyForPartenaireMs API Key à utiliser lors des appels
	 */
	public PartenaireCredentialFilter(String baseUrlPartenaireMs, String apiKeyForPartenaireMs) {
		this.partenaireMsClientDnc = new PartenaireMsClientDnc(baseUrlPartenaireMs, apiKeyForPartenaireMs);

		this.setPrincipalRequestHeader(HEADER_AUTHORIZATION);
		this.setCredentialsRequestHeader(HEADER_AUTHORIZATION);

		this.setAuthenticationManager(authentication -> {
			String siret = (String) authentication.getPrincipal();
			Pair<String,String> credentials = (Pair<String,String>) authentication.getCredentials();

			try {
				GrantAccessPartner grantAccessPartner = partenaireMsClientDnc.getApi().checkCredentialsPartenaire(siret, credentials.getLeft(), credentials.getRight());
				if(!Objects.isNull(grantAccessPartner)) {
					LOGGER.debug(String.format("Authentification réussi pour le partenaire : siret : %s, clientId : %s", siret, credentials.getLeft()));
					authentication.setAuthenticated(true);
				}
			}
			catch (HttpClientErrorException e) {
				throw new UnknownException("ErreurInterne",
						String.format("Impossible d'identifier le partenaire via le système DNC: siret : %s, clientId : %s", siret,credentials.getLeft()),
						HttpStatus.INTERNAL_SERVER_ERROR,
						e);
			}

			return authentication;
		});
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String siretPartenaire = request.getParameter(PARAMETER_SIRET_PARTENAIRE);
		if(StringUtils.isBlank(siretPartenaire)) {
			throw new BadRequestException("ErreurHeader", "Votre requête ne contient pas le SIRET du Partenaire", null, PARAMETER_SIRET_PARTENAIRE);
		}
		return siretPartenaire;
	}

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {

		try {
			String authorization = request.getHeader(HEADER_AUTHORIZATION);
			if(StringUtils.isBlank(authorization)) {
				throw new BadRequestException("ErreurHeader", "Votre requête ne contient pas vos credentials de Partenaire", null, HEADER_AUTHORIZATION);
			}
			LOGGER.debug("Requête d'authentification reçue avec l'entête : {}", authorization);
			if (authorization.startsWith("Basic")) {
				String base64UserId = authorization.substring("Basic".length()).trim();
				byte[] decodedClientIDClientSecret = Base64.getDecoder().decode(base64UserId);
				String[] clientIDAndClientSecret = new String(decodedClientIDClientSecret, StandardCharsets.UTF_8).split(":");
				if(clientIDAndClientSecret.length != 2) {
					throw new BadRequestException("ErreurHeaderFormat", "L'entete http Authorization n'est pas au bon format", null, HEADER_AUTHORIZATION);
				}
				return Pair.of(clientIDAndClientSecret[0], clientIDAndClientSecret[1]);
			} else {
				LOGGER.warn("Absence de l'entete http Authorization ou entete incorrecte : {}", authorization);
				throw new BadRequestException("ErreurHeaderFormat", "L'entete http Authorization n'est pas au bon format", null, HEADER_AUTHORIZATION);
			}
		} catch (PreAuthenticatedCredentialsNotFoundException ex) {
			throw new AccessDeniedException("Accès non autorisé à la page " + request.getRequestURI(), ex);
		}
	}
}
