package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.repository.SituationUsagerRepository;
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
 * Service de gestion des {@link SituationUsager}.
 */
@Service
public class SituationUsagerService {

	/**
	 * Logger {@link Log}
	 */
	private static final Log LOGGER = LogFactory.getLog(SituationUsagerService.class);

	@Autowired
	private SituationUsagerRepository situationUsagerRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Value("${dnc.situationUsager.timeToLive}")
	private int situationUsagerTimeToLive;

	/**
	 * Sauvegarde d'un objet {@link SituationUsager}
	 * @return l'objet {@link SituationUsager} sauvegardé correspondant
	 */
	public SituationUsager save(SessionUsager sessionUsager) {
		Assert.notNull(sessionUsager, "La session de l'usager ne doit pas être null");

		SituationUsager situationUsager = new SituationUsager(sessionUsager);
		SituationUsager entiteCree = situationUsagerRepository.save(situationUsager);
		redisTemplate.expire(SituationUsager.NAMESPACE_REDIS + ":" + entiteCree.getId(), situationUsagerTimeToLive, TimeUnit.SECONDS);

		return entiteCree;
	}

	/**
	 * Mise à jour d'un objet {@link SituationUsager}
	 * @return l'objet {@link SituationUsager} sauvegardé correspondant
	 */
	public SituationUsager update(SituationUsager situationUsager) {
		Assert.notNull(situationUsager, "La situation de l'usager ne doit pas être null");
		Assert.notNull(situationUsager.getId(), "L'id usager de la situation de l'usager ne doit pas être null");

		SituationUsager entiteAJour = situationUsagerRepository.save(situationUsager);
		redisTemplate.expire(SituationUsager.NAMESPACE_REDIS + ":" + entiteAJour.getId(), situationUsagerTimeToLive, TimeUnit.SECONDS);

		return entiteAJour;
	}

	/**
	 * Raffraichie le TTL et renvoie l'objet {@link SituationUsager} correspondant à l'id en paramètre sinon {@code null}
	 * @param idSituationUsager identifiant de la SituationUsager
	 * @return l'objet {@link SituationUsager} correspondant à l'id en paramètre sinons {@code null}
	 */
	public SituationUsager getSituationUsager(String idSituationUsager) {
		Assert.notNull(idSituationUsager, "L'identifiant de SituationUsager ne doit pas être null");

		Optional<SituationUsager> optionalSituationUsager = situationUsagerRepository.findById(idSituationUsager);
		if(optionalSituationUsager.isEmpty()) {
			LOGGER.debug(String.format("SituationUsager %s inexistante ou expirée", idSituationUsager));
			return null;
		}

		SituationUsager situationUsager = optionalSituationUsager.get();
		// Raffraichissement du TTL
		redisTemplate.expire(SituationUsager.NAMESPACE_REDIS + ":" + situationUsager.getId(), situationUsagerTimeToLive, TimeUnit.SECONDS);


		return situationUsager;
	}

	/**
	 * Suppression d'une SituationUsager.
	 * {@link SituationUsagerRepository#delete(Object)}
	 * @param idSituationUsagerDnc identifiant de la SituationUsager
	 */
	public void delete(String idSituationUsagerDnc) {
		situationUsagerRepository.deleteById(idSituationUsagerDnc);
	}

}
