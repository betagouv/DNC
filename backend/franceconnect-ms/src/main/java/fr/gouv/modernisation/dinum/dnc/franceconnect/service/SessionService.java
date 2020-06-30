package fr.gouv.modernisation.dinum.dnc.franceconnect.service;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.repository.SessionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Service de gestion des {@link Session}.
 */
@Service
public class SessionService {

	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(SessionService.class);

	@Autowired
	private SessionRepository sessionRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${dnc.session.timeToLive}")
	private int sessionTimeToLive;

	/**
	 * Sauvegarde d'un objet {@link Session} à partir
	 * des objets {@link TokenFranceconnect} et {@link IdentitePivot}
	 * @param token {@link TokenFranceconnect} à sauvegarder
	 * @param identitePivot {@link IdentitePivot} à sauvegarder
	 * @return l'objet {@link Session} sauvegardé correspondant
	 */
	public Session save(TokenFranceconnect token, IdentitePivot identitePivot) {
		Assert.notNull(token, "Le Token Franceconnect ne doit pas être null");
		Assert.notNull(identitePivot, "L'identité pivot ne doit pas être null");

		Session session = sessionRepository.save(new Session(token, identitePivot));
		redisTemplate.expire(Session.NAMESPACE_REDIS + ":" + session.getId(), sessionTimeToLive, TimeUnit.SECONDS);

		return session;
	}

	/**
	 * Met à jour le token d'un objet {@link Session} à partir
	 * d'un objet {@link TokenFranceconnect}
	 * @param session {@link Session} à mettre à jour
	 * @param token {@link TokenFranceconnect} à sauvegarder
	 * @return l'objet {@link Session} sauvegardé correspondant
	 */
	public Session refreshToken(Session session, TokenFranceconnect token) {
		Assert.notNull(token, "Le Token Franceconnect ne doit pas être null");
		Assert.notNull(session, "La session ne doit pas être null");

		session.setTokenFranceconnect(token);
		Session sessionMiseAJour = sessionRepository.save(session);
		redisTemplate.expire(Session.NAMESPACE_REDIS  + ":" + sessionMiseAJour.getId(), sessionTimeToLive, TimeUnit.SECONDS);

		return sessionMiseAJour;
	}

	/**
	 * Raffraichie le TTL et renvoie l'objet {@link Session} correspondant à l'id en paramètre sinon {@code null}
	 * @param idSession identifiant de la session
	 * @return l'objet {@link Session} correspondant à l'id en paramètre sinons {@code null}
	 */
	public Session getSession(String idSession) {
		Assert.notNull(idSession, "L'identifiant de session ne doit pas être null");

		Optional<Session> optionalSession = sessionRepository.findById(idSession);
		if(optionalSession.isEmpty()) {
			LOGGER.debug(String.format("Session %s inexistante ou expirée", idSession));
			return null;
		}

		Session session = optionalSession.get();
		// Raffraichissement du TTL
		redisTemplate.expire(Session.NAMESPACE_REDIS + ":" + session.getId(), sessionTimeToLive, TimeUnit.SECONDS);


		return session;
	}

	/**
	 * Suppression d'une session.
	 * {@link SessionRepository#delete(Object)}
	 * @param idSessionDnc identifiant de la session
	 */
	public void delete(String idSessionDnc) {
		sessionRepository.deleteById(idSessionDnc);
	}

}
