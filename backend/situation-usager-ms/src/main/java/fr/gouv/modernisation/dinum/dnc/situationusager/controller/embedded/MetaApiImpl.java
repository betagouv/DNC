package fr.gouv.modernisation.dinum.dnc.situationusager.controller.embedded;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.embedded.MetaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Controller des endpoints sur les métadonnées des démarches.
 */
@RestController
public class MetaApiImpl implements MetaApi {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaApiImpl.class);

	/**
	 * Scope OpenID, ce scope est obligataoire peut importe la démarche.
	 */
	public static final String SCOPE_OPENID = "openid";

	/**
	 * Scope permettant d'accéder à l'identité pivot
	 */
	public static final String SCOPE_IDENTITE_PIVOT = "identite_pivot";

	/**
	 * Scope permettant d'accéder à l'adresse de l'usager
	 */
	public static final String SCOPE_ADRESSE = "address";

	/**
	 * Scope permettant d'accéder aux infos de la carte grise.
	 */
	public static final String SCOPE_IMMATRICULATION = "immatriculation_vehicule";

	/**
	 * Scope pour la DGFIP - Accès au Revenu fiscal de référence
	 */
	public static final String SCOPE_DGFIP_REVENUE = "dgfip_rfr";

	/**
	 * Scope pour la DGFIP - Accès au Nombre de parts fiscales
	 */
	public static final String SCOPE_DGFIP_NOMBRE_PART = "dgfip_nbpart";

	/**
	 *
	 */
	public static final String SCOPE_DGFIP_SITUATION_FAMILLIAL = "dgfip_sitfam";

	/**
	 *
	 */
	public static final String SCOPE_DGFIP_PAC = "dgfip_pac";

	/**
	 * Scope pour la DGFIP - Accès à l'Adresse fiscale de taxation
	 */
	public static final String SCOPE_DGFIP_AFT = "dgfip_aft";

	/**
	 * Scope pour la DGFIP - Accès aux Données du local
	 */
	public static final String SCOPE_DGFIP_DONNEES_LOCAL = "dgfip_aft";

	/**
	 * Scope pour la CNAM - Accès aux données liés à la couverture sociale
	 */
	public static final String SCOPE_CNAM_ASSURANCE_MALADIE = "droits_assurance_maladie";


	@Override
	public ResponseEntity<List<String>> getScopesFranceconnectFromDemarcheCode(@NotNull @Valid DemarcheEnum codeDemarche) {
		LOGGER.debug("Récupération des scopes correspondant à la démarche {}",codeDemarche);

		switch (codeDemarche) {
			case CARTE_STATIONNEMENT:
				return ResponseEntity.ok(Arrays.asList(SCOPE_OPENID,SCOPE_IDENTITE_PIVOT, SCOPE_ADRESSE, SCOPE_IMMATRICULATION));
			case RESTAURATION_SCOLAIRE:
				return ResponseEntity.ok(Arrays.asList(SCOPE_OPENID,SCOPE_IDENTITE_PIVOT,SCOPE_DGFIP_REVENUE,SCOPE_DGFIP_SITUATION_FAMILLIAL));
			case DEMANDE_AIDE_PONCTUELLE:
				return ResponseEntity.ok(Arrays.asList(SCOPE_OPENID,SCOPE_IDENTITE_PIVOT,SCOPE_DGFIP_REVENUE,SCOPE_DGFIP_SITUATION_FAMILLIAL, SCOPE_DGFIP_NOMBRE_PART));
			default:
				throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "Le code de démarche n'est pas encore gérée par le système");
		}
	}
}
