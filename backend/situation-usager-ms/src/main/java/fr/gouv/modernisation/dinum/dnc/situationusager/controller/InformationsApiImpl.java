package fr.gouv.modernisation.dinum.dnc.situationusager.controller;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientCnaf;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientDgfip;
import fr.gouv.modernisation.dinum.dnc.situationusager.config.JustificatifConfig;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.InfosUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.InformationsApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.FetchDataService;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.SituationUsagerService;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.ModeleJustificatif;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation des endpoints /informations/**.
 * Tous les endpoints sont protéger par l'existence d'une session.
 * Le {@link org.springframework.security.core.context.SecurityContextHolder} contient les informations de la session et
 * de l'usager.
 *
 * {@link fr.gouv.modernisation.dinum.dnc.common.interceptor.SessionInterceptor}
 */
@RestController
public class InformationsApiImpl implements InformationsApi {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(InformationsApiImpl.class);

	/**
	 * Format du message d'erreur pour une SituationUsager manquante
	 */
	public static final String ERREUR_SESSION_NOT_FOUND_FORMAT = "La situation de l'usager pour la session %s est inconnue";

	/**
	 * Service des {@link fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager}
	 */
	@Autowired
	private SituationUsagerService situationUsagerService;

	/**
	 * Service de récupérations des données
	 */
	@Autowired
	private FetchDataService fetchDataService;

	/**
	 * Client sur API CNAF (api.particuliers)
	 */
	@Autowired
	private RestClientCnaf restClientCnaf;

	/**
	 * Client sur API DGFIP (api.particuliers)
	 */
	@Autowired
	private RestClientDgfip restClientDgfip;

	/**
	 * Sources des Messages pour les libellés
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Configuration des Justificatifs
	 */
	@Autowired
	private JustificatifConfig justificatifConfig;

	@Override
	public ResponseEntity<InfosUsager> getInfosUsager() {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGGER.info("Récupération des infos d'un usager pour la session {}", sessionUsager.getIdSession());

		// Récupération de la situation de l'usager si elle existe
		SituationUsager situationUsager = situationUsagerService.getSituationUsager(sessionUsager.getUserId());
		if(Objects.isNull(situationUsager)) {
			// Nouvelle session, on va récupérée les données franceconnectée
			// Tous les appels seront fait en asynchrone
			situationUsager = situationUsagerService.save(sessionUsager);

			fetchDataService.getDonnneesFranceconnecteesForStandAlone(situationUsager);

			situationUsager = situationUsagerService.update(situationUsager);
		}

		InfosUsager infosUsager = new InfosUsager();
		infosUsager.setIdCitoyen(situationUsager.getId());
		infosUsager.setIdSession(situationUsager.getSessionUsager().getIdSession());

		//Données Franceconnectée
		infosUsager.setBeneficiareCnam(situationUsager.getBeneficiaireCnam());

		return ResponseEntity.ok(infosUsager);
	}

	@Override
	public ResponseEntity<Declaration> getInfoDGFIP(@NotNull @Valid String numeroFiscal, @NotNull @Valid String referenceAvisFiscal) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGGER.info("Récupération des infos DGFIP d'un usager pour la session {}", sessionUsager.getIdSession());

		// Récupération de la situation de l'usager si elle existe
		SituationUsager situationUsager = situationUsagerService.getSituationUsager(sessionUsager.getUserId());
		if(Objects.isNull(situationUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(ERREUR_SESSION_NOT_FOUND_FORMAT, sessionUsager.getIdSession())
			);
		}

		Declaration declaration = situationUsager.getDeclaration();
		// Données inconnue, on tente de la récupérée
		if(Objects.isNull(declaration)) {
			// Récupération sur l'API de la CNAF
			declaration = restClientDgfip.getDeclaration(numeroFiscal, referenceAvisFiscal);
			// Initialisation et mise à jour des données en base
			situationUsager.setDeclaration(declaration);
			situationUsager = situationUsagerService.update(situationUsager);
		}

		// Si la donnée est inconnue de l'API, on lève une exception
		if (Objects.isNull(declaration)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("Aucune donnée trouvée pour la session %s et les infos suivantes : numéro fiscal : %s, référence avis fiscal : %s",
							sessionUsager.getIdSession(), numeroFiscal, referenceAvisFiscal)
			);
		}

		// Envoie de la réponse
		return ResponseEntity.ok(situationUsager.getDeclaration());
	}


	@Override
	public ResponseEntity<Famille> getInfoCNAF(@NotNull @Valid String numroDallocataire, @NotNull @Valid String codePostal) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGGER.info("Récupération des infos CNAF d'un usager pour la session {}", sessionUsager.getIdSession());

		// Récupération de la situation de l'usager si elle existe
		SituationUsager situationUsager = situationUsagerService.getSituationUsager(sessionUsager.getUserId());
		if(Objects.isNull(situationUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(ERREUR_SESSION_NOT_FOUND_FORMAT, sessionUsager.getIdSession())
			);
		}

		Famille famille = situationUsager.getFamille();
		// Données inconnue, on tente de la récupérée
		if(Objects.isNull(famille)) {
			// Récupération sur l'API de la CNAF
			famille = restClientCnaf.getFamille(numroDallocataire, codePostal);
			// Initialisation et mise à jour des données en base
			situationUsager.setFamille(famille);
			situationUsager = situationUsagerService.update(situationUsager);
		}

		// Si la donnée est inconnue de l'API, on lève une exception
		if (Objects.isNull(famille)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format("Aucune donnée trouvée pour la session %s et les infos suivantes : numéro allocataire : %s, codePostal : %s",
							sessionUsager.getIdSession(), numroDallocataire, codePostal)
			);
		}

		// Envoie de la réponse
		return ResponseEntity.ok(situationUsager.getFamille());
	}

	@Override
	public ResponseEntity<Map<String, List<DonneeUsager>>> getInfosJustificatifs(@Valid TypeJustificatif typeJustificatif) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGGER.info("Récupération des infos pour la génération d'un justificatif de type {} pour la session {}", typeJustificatif, sessionUsager.getIdSession());

		// Récupération de la situation de l'usager si elle existe
		SituationUsager situationUsager = situationUsagerService.getSituationUsager(sessionUsager.getUserId());
		if(Objects.isNull(situationUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(ERREUR_SESSION_NOT_FOUND_FORMAT, sessionUsager.getIdSession())
			);
		}

		Map<String, List<DonneeUsager>> result = DonneeUsagerUtils.getMapFromSituationUsager(situationUsager);
		// Données bouchonnées
		result.put(DonneeUsagerUtils.CODE_SOURCE_ANTS, DonneeUsagerUtils.getDonneesUsagerFromAnts());

		// Si le modèle de justificatif correspondant existe, on élimine certains champs de la map
		Optional<ModeleJustificatif> optional = justificatifConfig.getModeleFromTypeJustificatif(typeJustificatif);
		if(optional.isPresent()) {
			List<String> allChampsJustificatifs = optional.get().getAllChamps();

			result.forEach((codeSource, donneeUsagers) -> {
						// Récupération de la liste des champs à retirer de la liste
						List<DonneeUsager> donneesToRemove = donneeUsagers.stream()
								.filter(donneeUsager -> !allChampsJustificatifs.contains(donneeUsager.getName()))
								.collect(Collectors.toList());
						// Suppression de la liste existante
						donneeUsagers.removeAll(donneesToRemove);
					});
		}

		// Alimentation du libellé pour tous les objets
		result.forEach((codeSource, donneeUsagers) -> donneeUsagers.forEach(this::setLibelleFromName));

		return ResponseEntity.ok(result);
	}

	/**
	 * Alimente le champ libellé d'un objet {@link DonneeUsager} à partir du {@link MessageSource}.
	 * Le texte <i>Champ inconnu</i> est utilisé si le libellé n'existe pas dans la source.
	 * Réalise l'action sur toutes les champs de la liste d'un objet ou d'une liste.
	 * @param donneeUsager {@link DonneeUsager} à traiter
	 */
	private void setLibelleFromName(DonneeUsager donneeUsager) {
		if(CollectionUtils.isEmpty(donneeUsager.getListeDonnees())) {
			donneeUsager.setLibelle(messageSource.getMessage(
					donneeUsager.getName()+"-label",
					null,
					"Champ Inconnu",
					Locale.getDefault()
			));
		}
		else {
			donneeUsager.getListeDonnees().forEach(this::setLibelleFromName);
		}
	}
}
