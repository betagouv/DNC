package fr.gouv.modernisation.dinum.dnc.situationusager.validator;

import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator pour les données brutes des démarches.
 */
public class DemarcheUsagerValidator {

	/**
	 * Constructeur par défaut
	 */
	private DemarcheUsagerValidator() {
		// Constructeur privée pour protéger le constructeur par défaut
	}

	/**
	 * Valide un objet {@link DemarcheUsager} en fonction du code de démarche concerné.
	 * Lève une {@link BadRequestException} en cas d'erreur de validation
	 * @param demarcheUsager {@link DemarcheUsager} à valider
	 */
	public static List<String> validate(@NotNull DemarcheUsager demarcheUsager) {
		List<String> listChampsManquants = null;
		switch (demarcheUsager.getDemarche().getCode()) {
			case CARTE_STATIONNEMENT:
				listChampsManquants = validateCarteStationnement(demarcheUsager);
				break;
			case DEMANDE_AIDE_PONCTUELLE:
			case RESTAURATION_SCOLAIRE:
			default:
				break;
		}

		if(CollectionUtils.isNotEmpty(listChampsManquants)) {
			throw new BadRequestException("ChampsManquants",
					String.format("Les champs %s sont nécessaires à une démarche de type %s",
							listChampsManquants,
							demarcheUsager.getDemarche().getCode()),
					null,
					StringUtils.join(listChampsManquants, ","));
		}

		return listChampsManquants;
	}

	/**
	 * Valide les données brutes d'une démarche pour le cas d'une demande de carte de stationnement.
	 * Lève une exception {@link BadRequestException} si une ou plusieurs données sont manquantes dans la démarche.
	 * @param demarcheUsager {@link DemarcheUsager} à valider
	 */
	private static List<String> validateCarteStationnement(DemarcheUsager demarcheUsager) {

		Map<String, String> donneesBrutes = demarcheUsager.getRawData();

		List<String> listChampsManquants = new ArrayList<>();

		validateDataInMap(donneesBrutes, "nom", listChampsManquants);
		validateDataInMap(donneesBrutes, "prenoms", listChampsManquants);

		validateDataInMap(donneesBrutes, "siret", listChampsManquants);
		validateDataInMap(donneesBrutes, "raisonSociale", listChampsManquants);
		validateDataInMap(donneesBrutes, "vehiculeSelectionne", listChampsManquants);


		return listChampsManquants;
	}

	/**
	 * Valide une entrée dans la Map
	 * @param map la {@link Map} à tester
	 * @param keyInMap la clé dans la {@link Map} à tester
	 * @param keyInErrors la liste des erreurs
	 */
	private static void validateDataInMap(@NotNull Map<String, String> map,@NotNull String keyInMap,@NotNull List<String> keyInErrors) {
		if(!map.containsKey(keyInMap) && StringUtils.isNotEmpty(map.get(keyInMap))) {
			keyInErrors.add(keyInMap);
		}
	}
}
