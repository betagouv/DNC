package fr.gouv.modernisation.dinum.dnc.situationusager.controller.embedded;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.embedded.EmbeddedApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.DemarcheUsagerService;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.FetchDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Controller des endpoints liés aux échanges entre Front & Back pour les cas d'usage embedded
 */
@RestController
public class EmbeddedApiImpl implements EmbeddedApi {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedApiImpl.class);

	/**
	 * Message d'erreur en cas de démarche non trouvé
	 */
	public static final String DEMARCHE_NOT_FOUND_ERROR_FORMAT = "La Démmarche d'id %s n'a pas été trouvé dans le système.";

	/**
	 * Service des démarches des usagers
	 */
	@Autowired
	private DemarcheUsagerService demarcheUsagerService;

	/**
	 * Service de récupérations des données
	 */
	@Autowired
	private FetchDataService fetchDataService;

	/**
	 * 1er Appel réalisé par le Front lors de la cinématique Embedded.
	 * Renvoie les données de l'usager en fonction du type de démarche en cours de traitement
	 * @param body {@link Map} de {@link String} contenant les secrets supplémentaires nécessaires pour la récupération des données de l'usager.
	 * @param codeDemarche {@link DemarcheEnum} de la démarche en cours de traitement
	 * @return la {@link Map} des données brutes de la démarche.
	 */
	@Override
	public ResponseEntity<Map<String, String>> fetchDataForDemarche(@Valid Map<String, String> body, DemarcheEnum codeDemarche) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String idDemarche = body.get("idDemarche");
		if(StringUtils.isNotBlank(idDemarche)) {
			LOGGER.info("Les données de la démarche {} ont été demandées par le Front", idDemarche);
			DemarcheUsager demarcheUsager = demarcheUsagerService.getDemarcheUsager(UUID.fromString(idDemarche));
			return ResponseEntity.ok(demarcheUsager.getRawData());
		}

		if(StringUtils.isBlank(body.get("siretPartenaire"))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Le SIRET du partenaire est nécessaire pour la création de données de démarches");
		}

		Map<String, String> rawData = fetchDataService.getDonneesForDemarche(codeDemarche, sessionUsager, body);
		DemarcheUsager demarcheUsager = demarcheUsagerService.saveFromRawData(sessionUsager, codeDemarche, rawData);

		return ResponseEntity.ok(demarcheUsager.getRawData());
	}

	@Override
	public ResponseEntity<Map<String, String>> updateDemarche(@Valid Map<String, String> body, UUID idDemarche, @Valid Integer etape) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		DemarcheUsager demarcheUsager = demarcheUsagerService.getDemarcheUsager(idDemarche);
		if(Objects.isNull(demarcheUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(DEMARCHE_NOT_FOUND_ERROR_FORMAT, idDemarche.toString()));
		}
		if(!demarcheUsager.isModifiable()) {
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
					String.format("La Démmarche d'id %s n'est plus modifiable par l'usager.", idDemarche.toString()));
		}

		DemarcheUsager demarcheUsagerMiseAJour = demarcheUsagerService.updateFromRawData(demarcheUsager, body, sessionUsager, etape);

		return ResponseEntity.ok(demarcheUsagerMiseAJour.getRawData());
	}

	@Override
	public ResponseEntity<UUID> finalizeDemarche(@Valid Map<String, String> body, UUID idDemarche) {
		DemarcheUsager demarcheUsager = demarcheUsagerService.getDemarcheUsager(idDemarche);
		if(Objects.isNull(demarcheUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(DEMARCHE_NOT_FOUND_ERROR_FORMAT, idDemarche.toString()));
		}

		demarcheUsager = demarcheUsagerService.finalizeDemarche(demarcheUsager);

		return ResponseEntity.ok(demarcheUsager.getId());
	}

	@Override
	public ResponseEntity<Justificatif> getJustificatifForDemarche(UUID idDemarche) {

		DemarcheUsager demarcheUsager = demarcheUsagerService.getDemarcheUsager(idDemarche);
		if(Objects.isNull(demarcheUsager)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					String.format(DEMARCHE_NOT_FOUND_ERROR_FORMAT, idDemarche.toString()));
		}

		return ResponseEntity.ok(demarcheUsager.getJustificatif());
	}
}
