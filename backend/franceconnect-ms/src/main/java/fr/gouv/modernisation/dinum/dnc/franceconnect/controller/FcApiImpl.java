package fr.gouv.modernisation.dinum.dnc.franceconnect.controller;

import fr.gouv.modernisation.dinum.dnc.franceconnect.client.RestClientFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.AuthorizationRequest;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.server.FcApi;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import fr.gouv.modernisation.dinum.dnc.franceconnect.service.SessionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;

/**
 * RestController implémentant l'interface {@link FcApi}
 */
@Controller
public class FcApiImpl implements FcApi {

	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(FcApiImpl.class);
	public static final String FORMAT_ERREUR_SESSION_INEXISTANTE = "Session %s inexistante ou expirée";

	/**
	 * Client à l'API Franceconnect
	 */
	@Autowired
	RestClientFranceconnect restClientFranceconnect;

	/**
	 * Service des {@link Session}
	 */
	@Autowired
	SessionService sessionService;

	/**
	 * Cet endpoint est appelé uniquement par le front-office.
	 * Il sert à récupérer le code d'authorisation France Connect, et déclenche
	 * par la suite la cinématique de récupération de l'access token FranceConnect.
	 * En fonction de l'existence ou non du cookie ID-Session dans le header de la requette,
	 * on fait la création de la session ou la vérification d'existence d'une session
	 * qui est gérée par une régle métier.
	 *
	 * @param authorizationRequest le code d'authorisation dans la redirection FranceConnect et l'éventuelle id de session
	 * @return le Token Franceconnect correspondant
	 */
	@Override
	public ResponseEntity<SessionUsager> getOrRefreshTokenFranceconnect(@Valid AuthorizationRequest authorizationRequest) {

		// Récupération du Token FC
		TokenFranceconnect token = restClientFranceconnect.getAccessToken(authorizationRequest);

		if(token == null) {
			LOGGER.error(String.format(
					"Une erreur est survenue lors de la récupération du token Franceconnect %s",
					authorizationRequest.getAuthorizationCode()
			));
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					String.format(
							"Une erreur est survenue lors de la récupération du token Franceconnect %s",
							authorizationRequest.getAuthorizationCode()
					));
		}

		Session session = null;

		// 1ere connexion, l'utilisateur a besoin d'une création de session
		if(authorizationRequest.getIdSession() == null) {
			// Récupération de l'identité Pivot
			IdentitePivot identitePivot = restClientFranceconnect.getIdPivot(token);

			// Check identité pivot
			if(identitePivot == null) {
				LOGGER.error(String.format(
						"Une erreur est survenue lors de la récupération de l'identité pivot avec le token %s (expire in:%s, acces_token:%s, token_type:%s)",
						token.getIdToken(), token.getExpiresIn(), token.getAccessToken(), token.getTokenType()
				));
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						String.format(
								"Une erreur est survenue lors de la récupération de l'identité pivot avec le token %s (expire in:%s, acces_token:%s, token_type:%s)",
								token.getIdToken(), token.getExpiresIn(), token.getAccessToken(), token.getTokenType()
						));
			}

			// Création de la session
			session = sessionService.save(token, identitePivot);
		}
		// Utilisateur déjà connecté, pas besoin de recréer la session,
		// On réalise un check puis on renvoie l'access Token à partir du JWT
		else {
			//Récupération et mise à jour de la session existante
			session = sessionService.refreshToken(sessionService.getSession(authorizationRequest.getIdSession()), token);
		}

		// Envoie de la réponse
		return ResponseEntity.ok(createSessionUsagerFromSession(session));
	}

	@Override
	public ResponseEntity<SessionUsager> createSessionFromIdentitePivot(@Valid SessionUsager body) {

		Session session = null;
		if(body.getIdSession() != null ){
			TokenFranceconnect tokenFranceconnect = new TokenFranceconnect();
			tokenFranceconnect.setAccessToken(body.getCurrentToken());
			tokenFranceconnect.setIdToken(body.getCurrentToken());
			tokenFranceconnect.setTokenType("Bearer");
			tokenFranceconnect.setExpiresIn(30);
			//Récupération et mise à jour de la session existante
			session = sessionService.refreshToken(sessionService.getSession(body.getIdSession()), tokenFranceconnect);
		}
		else {
			if(body.getCurrentToken() == null || body.getIdentitePivot() == null) {
				LOGGER.error("L'identité Pivot et/ou le token ne peuvent pas être null");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"L'identité Pivot et/ou le token ne peuvent pas être null"
				);
			}

			// Création de la session
			TokenFranceconnect tokenFranceconnect = new TokenFranceconnect();
			tokenFranceconnect.setAccessToken(body.getCurrentToken());
			tokenFranceconnect.setIdToken(body.getCurrentToken());
			tokenFranceconnect.setTokenType("Bearer");
			tokenFranceconnect.setExpiresIn(30);
			session = sessionService.save(tokenFranceconnect, body.getIdentitePivot());
		}

		return ResponseEntity.ok(createSessionUsagerFromSession(session));
	}

	/**
	 * Enpoint vérifiant la validité d'une session.
	 *
	 * @param idSession l'identifiant de la session à valider
	 * @return Code 200 si la session existe, sinon 404
	 */
	@Override
	public ResponseEntity<SessionUsager> checkSession(String idSession) {
		Session session = sessionService.getSession(idSession);

		if(Objects.isNull(session)) {
			LOGGER.debug(String.format(FORMAT_ERREUR_SESSION_INEXISTANTE, idSession));
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(FORMAT_ERREUR_SESSION_INEXISTANTE, idSession));
		}

		LOGGER.debug(String.format("Session %s existante", idSession));
		return ResponseEntity.ok(createSessionUsagerFromSession(session));
	}

	/**
	 * Endpoint permettant de supprimer explicitement une session.
	 * @param idSession le token de la session à valider
	 * @return réponse 200 si la session est supprimée, 404 si la session est inexistante
	 */
	@Override
	public ResponseEntity<Void> deleteSession(String idSession) {
		if(idSession == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if(Objects.isNull(sessionService.getSession(idSession))) {
			LOGGER.debug(String.format(FORMAT_ERREUR_SESSION_INEXISTANTE, idSession));
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(FORMAT_ERREUR_SESSION_INEXISTANTE, idSession));
		}

		sessionService.delete(idSession);
		LOGGER.debug(String.format("Suppression de la Session %s", idSession));
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Crée un objet {@link SessionUsager} à partir d'un objet {@link Session}
	 * @param session source des données
	 * @return l'objet {@link SessionUsager} correspondant
	 */
	private SessionUsager createSessionUsagerFromSession(Session session) {
		// Création de la réponse
		SessionUsager sessionUsager = new SessionUsager();
		// Création de la réponse
		sessionUsager.setIdSession(session.getId());
		sessionUsager.setIdentitePivot(session.getIdentitePivot());
		sessionUsager.setUserId(session.getIdUsager());
		sessionUsager.setCurrentToken(session.getTokenFranceconnect().getAccessToken());
		sessionUsager.setIdToken(session.getTokenFranceconnect().getIdToken());
		return sessionUsager;
	}
}
