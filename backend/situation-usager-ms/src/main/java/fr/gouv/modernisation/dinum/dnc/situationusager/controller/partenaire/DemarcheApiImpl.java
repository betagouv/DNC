package fr.gouv.modernisation.dinum.dnc.situationusager.controller.partenaire;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.UpdateStatutDemarcheBody;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.partenaire.DemarcheApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.DemarcheUsagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Implémentation des endpoints pour les partenaires acceptant des démarches via DNC.
 */
@RestController
public class DemarcheApiImpl implements DemarcheApi {
	/**
	 * Logger {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemarcheApiImpl.class);

	@Autowired
	private DemarcheUsagerService demarcheUsagerService;

	@Override
	public ResponseEntity<Demarche> getDemarcheData(UUID demarcheToken, @NotNull @Valid String siretPartenaire) {
		LOGGER.info("Lecture de la démarche {} par le partenaire {}", demarcheToken, siretPartenaire);
		Demarche demarche = demarcheUsagerService.getDemarche(demarcheToken, siretPartenaire);

		return ResponseEntity.ok(demarche);
	}

	@Override
	public ResponseEntity<Void> updateStatutuDemarche(@Valid UpdateStatutDemarcheBody body, @NotNull @Valid String siretPartenaire, UUID demarcheToken) {
		LOGGER.info("Mise à jour du statut de la démarche {} par le partenaire {}", demarcheToken, siretPartenaire);
		demarcheUsagerService.updateDemarcheForPartenaire(demarcheToken,siretPartenaire,body.getStatut(),body.getCommentaires());

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
