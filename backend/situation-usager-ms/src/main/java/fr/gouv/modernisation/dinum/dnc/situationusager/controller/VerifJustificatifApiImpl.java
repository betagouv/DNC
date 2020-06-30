package fr.gouv.modernisation.dinum.dnc.situationusager.controller;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.VerifJustificatifApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.CipherUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller des endpoints sur les Vérifications des justificatifs (/verif-justificatif/*).
 * Ces endpoints sont ouverts.
 */
@RestController
public class VerifJustificatifApiImpl implements VerifJustificatifApi {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifJustificatifApiImpl.class);

	@Value("${dnc.secret}")
	private String secretKey;

	@Override
	public ResponseEntity<List<String>> verifJustificatifGet(@NotNull @Valid List<String> data) {
		List<String> content = new ArrayList<>();

		// Décryptage des données
		// Création de l'outil de décryptage
		try {
			data.forEach(encryptedString ->
				content.add(CipherUtils.decrypt(StringUtils.defaultIfBlank(encryptedString, ""), secretKey)
			));
		}
		catch (NullPointerException npe) {
			LOGGER.error("Erreur lors du décryptage", npe);
			content.add("Cette URL n'est pas valide pour le DNC");
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors du traitement", e);
			content.add("Cette URL n'est pas valide pour le DNC");
		}

		return ResponseEntity.ok(content);
	}
}
