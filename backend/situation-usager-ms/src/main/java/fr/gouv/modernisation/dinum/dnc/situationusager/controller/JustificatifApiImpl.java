package fr.gouv.modernisation.dinum.dnc.situationusager.controller;

import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.exception.NotFoundException;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemandeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.JustificatifApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.JustificatifService;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.SituationUsagerService;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Implémentations des endpoints liés aux justificatifs du DNC.
 *
 */
@RestController
public class JustificatifApiImpl implements JustificatifApi {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JustificatifApiImpl.class);

	/**
	 * Service des {@link fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager}
	 */
	@Autowired
	private SituationUsagerService situationUsagerService;

	/**
	 * Service de génération de Justificaitif
	 */
	@Autowired
	private JustificatifService justificatifService;

	@Override
	public ResponseEntity<Justificatif> createJustificatif(@Valid DemandeJustificatif body) {
		SessionUsager sessionUsager = (SessionUsager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		LOGGER.info("Création d'un justificatif personalisé pour la session {}", sessionUsager.getIdSession());

		// Récupération de la situation de l'usager si elle existe
		SituationUsager situationUsager = situationUsagerService.getSituationUsager(sessionUsager.getUserId());
		if(Objects.isNull(situationUsager)) {
			throw new NotFoundException(
					String.format("La situation de l'usager pour la session %s est inconnue", sessionUsager.getIdSession()),
					sessionUsager.getIdSession()
			);
		}

		if(Objects.isNull(body)) {
			throw new BadRequestException("DemandeJustificatif");
		}

		Map<String, List<DonneeUsager>> donnees = DonneeUsagerUtils.getMapFromSituationUsager(situationUsager);
		if(CollectionUtils.isNotEmpty(body.getDonneesSelectionnees())) {
			donnees.put(DonneeUsagerUtils.CODE_SOURCE_SELECTION, body.getDonneesSelectionnees());
		}

		Justificatif justificatif = justificatifService.generateJustificatif(body.getType(), donnees);

		return ResponseEntity.ok(justificatif);
	}
}
