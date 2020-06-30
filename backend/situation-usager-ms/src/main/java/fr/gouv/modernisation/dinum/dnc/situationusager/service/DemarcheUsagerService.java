package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import fr.gouv.modernisation.dinum.dnc.common.constante.DemarcheQueueConstantes;
import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.exception.ForbiddenAccesException;
import fr.gouv.modernisation.dinum.dnc.common.exception.NotFoundException;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.CorrelationIdInterceptor;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.config.JustificatifConfig;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.PieceJointe;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.repository.DemarcheUsagerRepository;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.ModeleJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.validator.DemarcheUsagerValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Service pour la gestion des objets {@link Demarche}
 */
@Service
public class DemarcheUsagerService {

	/**
	 * Logger {@link Log}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemarcheUsagerService.class);
	public static final String ERREUR_ASSERT_IDENTIFIANT_DEMARCHE_NULL_MESSAGE = "L'identifiant de la démarche ne peut pas être null";

	@Autowired
	private DemarcheUsagerRepository demarcheUsagerRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JustificatifService justificatifService;


	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private JustificatifConfig justificatifConfig;

	/**
	 * TTL des données des démarches
	 */
	@Value("${dnc.demarcheUsager.timeToLive}")
	private int demarcheUsagerTimeToLive;

	/**
	 * Supprime un objet {@link DemarcheUsager} de la base.
	 * Utilisé uniquement après récupération des données par un Fournisseur de service
	 *
	 * @param demarcheUsager l'objet à supprimer
	 */
	public void delete(DemarcheUsager demarcheUsager) {
		demarcheUsagerRepository.delete(demarcheUsager);
	}

	/**
	 * Sauvegarde un objet {@link DemarcheUsager} à partir des données brutes de la démarche.
	 * @param sessionUsager {@link SessionUsager} session de l'usager associé à la démarche
	 * @param typeDemarche {@link DemarcheEnum} type de démarche en cours de traitement
	 * @param donneesBrutes {@link Map} de {@link String} représentant les données brutes de la démarches
	 * @return l'objet {@link DemarcheUsager} créé ou mis à jour
	 */
	public DemarcheUsager saveFromRawData(SessionUsager sessionUsager, DemarcheEnum typeDemarche, Map<String, String> donneesBrutes) {
		LOGGER.info("Initialisation d'une démarche de type {} pour l'usager {}", typeDemarche, sessionUsager.getUserId());
		// Conversion des données brutes en objets
		Demarche demarche = DemarcheUtils.getDemarcheFromRawData(typeDemarche, donneesBrutes);

		// Création de l'objet DemarcheUsager correspondant
		DemarcheUsager demarcheUsager = new DemarcheUsager(demarche);
		demarcheUsager.setIdUsager(sessionUsager.getUserId());
		demarcheUsager.setModifiable(true);
		demarcheUsager.getDemarche().setId(demarcheUsager.getId());
		// Ajout de l'identifiant de la démarche dans les données brutes
		donneesBrutes.put("idDemarche", demarcheUsager.getId().toString());
		// Ajout des données brutes dans l'objet
		demarcheUsager.setRawData(donneesBrutes);
		//Sauvegarde de l'objet et ajout du TTL
		DemarcheUsager entiteCree = demarcheUsagerRepository.save(demarcheUsager);
		redisTemplate.expire(DemarcheUsager.NAMESPACE_REDIS + ":" + entiteCree.getId().toString(), demarcheUsagerTimeToLive, TimeUnit.SECONDS);

		return demarcheUsager;
	}

	/**
	 * Mise à jour des données brutes d'un objet {@link DemarcheUsager} en base.
	 * @param demarcheUsager {@link DemarcheUsager} en cours de traitement à mettre à jour
	 * @param donneesDeMiseAJour {@link Map} de {@link String} représentant les données de mise à jour de la démarches
	 * @param sessionUsager {@link SessionUsager} session de l'usager associé à la démarche
	 * @param etape étape du traitement - WIP pas encore utilisé
	 * @return l'objet {@link DemarcheUsager} créé ou mis à jour
	 */
	public DemarcheUsager updateFromRawData(DemarcheUsager demarcheUsager, Map<String, String> donneesDeMiseAJour,
											SessionUsager sessionUsager, Integer etape) {
		LOGGER.info("Mise à jour de la démarche d'identifiant {} par l'usager {}, étape {}",
				demarcheUsager.getId(), sessionUsager.getUserId(), etape);
		if(!Objects.equals(sessionUsager.getUserId(), demarcheUsager.getIdUsager())) {
			throw new ForbiddenAccesException("L'usager sauvegardé pour cette démarche ne correspond pas à l'usager de cette session", demarcheUsager.getIdUsager());
		}

		Optional<ModeleJustificatif> optional = justificatifConfig.getModeleFromTypeJustificatif(DemarcheUtils.getTypeJustificatifFromDemarcheEnum(demarcheUsager.getDemarche().getCode()));
		// Si on a pas de modèle correspondant, on ne peut pas valider la mise à jour
		if(optional.isEmpty()) {
			LOGGER.warn("Pas de validation pour la démarche de type {}, idDemarche : {}, idSession : {}",
					demarcheUsager.getDemarche().getCode(), demarcheUsager.getId(), sessionUsager.getIdSession());
		}
		else {
			ModeleJustificatif modeleJustificatif = optional.get();
			// On vérifie qu'au moins un champ dans la mise à jour fait parti des champs du justificatif final
			boolean auMoinsUnChampSelectionnee = donneesDeMiseAJour.keySet().stream().anyMatch(modeleJustificatif.getAllChamps()::contains);

			boolean champNonPrisEnCompte = Stream.of("vehiculeSelectionne","enfant").anyMatch(donneesDeMiseAJour.keySet()::contains);
			// Si aucun champ n'est compatible, on lève une erreur
			if(!auMoinsUnChampSelectionnee && !champNonPrisEnCompte) {
				throw new BadRequestException(
						"AucuneSelectionCompatible",
						"Au moins un champs cible doit être fourni pour réaliser une mise à jour de démarche",
						null,
						String.join(",", modeleJustificatif.getAllChamps())
				);
			}
		}

		// Mise à jour des données brutes
		demarcheUsager.getRawData().putAll(donneesDeMiseAJour);

		DemarcheUsager entiteCree = demarcheUsagerRepository.save(demarcheUsager);
		redisTemplate.expire(DemarcheUsager.NAMESPACE_REDIS + ":" + entiteCree.getId().toString(), demarcheUsagerTimeToLive, TimeUnit.SECONDS);

		return entiteCree;
	}

	/**
	 * Finalise une démarche en mettant à jour l'objet {@link DemarcheUsager} avec les données brutes.
	 * L'objet est validé et lève une exception en cas d'erreur.
	 *
	 * @see DemarcheUsagerValidator
	 * @param demarcheUsager {@link DemarcheUsager} à mettre à jour
	 * @return l'objet {@link DemarcheUsager} à jour
	 */
	public DemarcheUsager finalizeDemarche(@NotNull DemarcheUsager demarcheUsager) {
		Assert.notNull(demarcheUsager, "La démarche usager ne peut pas être null");
		LOGGER.debug("Finalisation de la démarche {}", demarcheUsager.getId());
		//Validation des données brutes de la démarche
		DemarcheUsagerValidator.validate(demarcheUsager);

		// Traitement des données de la démarche
		DemarcheUtils.updateDemarcheUsagerWithRawData(demarcheUsager);

		// Génération du justificatif
		demarcheUsager.setJustificatif(justificatifService.generateJustificatif(demarcheUsager));
		PieceJointe pieceJointe = new PieceJointe();
		pieceJointe.setFilename(demarcheUsager.getJustificatif().getFilename());
		pieceJointe.setContenu(demarcheUsager.getJustificatif().getContenu());
		demarcheUsager.getDemarche().setJustificatif(pieceJointe);


		// Désactivation de la modification
		demarcheUsager.setModifiable(false);

		// Sauvegarde, Mise à jour du TTL & renvoi
		DemarcheUsager entiteCree = demarcheUsagerRepository.save(demarcheUsager);
		redisTemplate.expire(DemarcheUsager.NAMESPACE_REDIS + ":" + entiteCree.getId().toString(), demarcheUsagerTimeToLive, TimeUnit.SECONDS);

		return entiteCree;
	}

	/**
	 * Récupère les données d'une démarche
	 * @param tokenDemarche {@link UUID} identifiant de la démarche
	 * @param siretPartenaire {@link String} SIRET du partenaire venant chercher les données de la démarche
	 * @return {@link Demarche} associée aux paramètres
	 */
	public Demarche getDemarche(UUID tokenDemarche, String siretPartenaire) {
		Assert.notNull(tokenDemarche, ERREUR_ASSERT_IDENTIFIANT_DEMARCHE_NULL_MESSAGE);
		LOGGER.debug("Lecture de la démarche {} par le partenaire {}", tokenDemarche, siretPartenaire);

		Optional<DemarcheUsager> optional = demarcheUsagerRepository.findById(tokenDemarche);
		if(optional.isEmpty()) {
			throw new NotFoundException(
					new Exception("Le token de démarche utilisé n'existe pas en base."),
					tokenDemarche.toString()
			);
		}

		DemarcheUsager demarcheUsager = optional.get();

		if(!StringUtils.equals(siretPartenaire, demarcheUsager.getDemarche().getSiretPartenaire())) {
			throw new ForbiddenAccesException(
					new Exception("Le siret utilisée n'a pas accès aux données demandées."),
					siretPartenaire
			);
		}

		Map<String, String> mapDemarches = new HashMap<>();
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_CREATION);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE, demarcheUsager.getId().toString());
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER, demarcheUsager.getIdUsager());
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE, demarcheUsager.getDemarche().getSiretPartenaire());
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE, demarcheUsager.getDemarche().getCode().toString());
		String correlationId = StringUtils.defaultIfEmpty(
				MDC.get(CorrelationIdInterceptor.CORRELATION_ID_VARIABLE_NAME),
				UUID.randomUUID().toString()
		);
		LOGGER.info("Envoie des données sur la queue des démarches : Correlation ID : {}, Partenaire : {}", correlationId, siretPartenaire);
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME,
				mapDemarches,
				message -> {
					message.setJMSCorrelationID(correlationId);
					return message;
				});

		return demarcheUsager.getDemarche();
	}

	/**
	 * Récupère les données d'une démarche
	 * @param tokenDemarche {@link UUID} identifiant de la démarche
	 * @param siretPartenaire {@link String} SIRET du partenaire venant chercher les données de la démarche
	 * @param statut {@link String} nouveau statut de la démarche poussée par le partenaire
	 * @param commentaires {@link String} commentaire associé à la mise à jour du statut par le partenaire
	 */
	public void updateDemarcheForPartenaire(UUID tokenDemarche, String siretPartenaire, String statut, String commentaires) {
		Assert.notNull(tokenDemarche, ERREUR_ASSERT_IDENTIFIANT_DEMARCHE_NULL_MESSAGE);
		LOGGER.debug("Lecture de la démarche {} par le partenaire {}", tokenDemarche, siretPartenaire);

		Map<String, String> mapDemarches = new HashMap<>();
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_MISE_A_JOUR);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE, tokenDemarche.toString());
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE, siretPartenaire);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_STATUT, statut);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_COMMENTAIRES, commentaires);
		LOGGER.debug("Envoie des données sur la queue des démarches : {}", mapDemarches);
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});
	}

	/**
	 * Récupère les données d'une démarche
	 * @param tokenDemarche {@link UUID} identifiant de la démarche
	 * @return {@link DemarcheUsager} associée aux paramètre
	 */
	public DemarcheUsager getDemarcheUsager(UUID tokenDemarche) {
		Assert.notNull(tokenDemarche, ERREUR_ASSERT_IDENTIFIANT_DEMARCHE_NULL_MESSAGE);

		Optional<DemarcheUsager> optional = demarcheUsagerRepository.findById(tokenDemarche);
		if(optional.isEmpty()) {
			throw new NotFoundException(
					new Exception("Le token de démarche utilisé n'existe pas en base."),
					tokenDemarche.toString()
			);
		}
		redisTemplate.expire(DemarcheUsager.NAMESPACE_REDIS + ":" + tokenDemarche.toString(), demarcheUsagerTimeToLive, TimeUnit.SECONDS);

		return optional.get();
	}
}
