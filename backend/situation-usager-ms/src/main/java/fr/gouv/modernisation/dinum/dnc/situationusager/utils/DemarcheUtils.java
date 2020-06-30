package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.Adresse;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.CarteStationnement;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demandeur;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Méthode utilitaire pour les démarches.
 * Présente la méthode de conversion d'une {@link SessionUsager} vers l'objet {@link Demarche} correspondant
 */
public class DemarcheUtils {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemarcheUtils.class);

	/**
	 * Constantes pour les clés dans la Map des données d'une démarche
	 */
	public static final String MAP_DATA_DEMARCHE_KEYS_VEHICULE_SELECTIONNE = "vehiculeSelectionne";
	public static final String MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES = "adressesConnues";

	/**
	 * Renvoie le {@link TypeJustificatif} correspondant à la démarche en paramètre.
	 * @param demarcheEnum {@link DemarcheEnum} démarche en cours de traitement
	 * @return le {@link TypeJustificatif} correspondant sinon {@code null}
	 */
	public static TypeJustificatif getTypeJustificatifFromDemarcheEnum(DemarcheEnum demarcheEnum) {
		return EnumUtils.getEnum(TypeJustificatif.class, demarcheEnum.name());
	}

	/**
	 * Renvoie un objet {@link Demarche} à partir d'une {@link Map} contenant les données brutes
	 * venant du Front ou d'un Fournisseur de Données.
	 * Utilise un {@link ObjectMapper} pour réaliser la conversion {@link ObjectMapper#convertValue(Object, Class)}
	 * pour créer les objets {@link Demarche} et {@link Demandeur}
	 * @param demarcheEnum {@link DemarcheEnum} type de la démarche
	 * @param rawData {@link Map} des données brutes de la démarche
	 * @return l'objet {@link Demarche} et son objet {@link Demandeur} correspondant aux paramètres
	 */
	public static Demarche getDemarcheFromRawData(@NotNull DemarcheEnum demarcheEnum, @NotNull Map<String, String> rawData) {
		ObjectMapper objectMapper = CommonUtils.getDefaultMapper();
		Demarche demarche = objectMapper.convertValue(rawData, Demarche.class);
		demarche.setCode(demarcheEnum);

		Demandeur demandeur = objectMapper.convertValue(rawData, Demandeur.class);
		demarche.setDemandeur(demandeur);

		return demarche;
	}

	/**
	 * Mets à jour un objet {@link DemarcheUsager} et les objets sous-jacents à partir des données brutes.
	 * Utilisé une fois les données brutes de la démarches mises à jour.
	 * @param demarcheUsager {@link DemarcheUsager} à traiter
	 */
	public static void updateDemarcheUsagerWithRawData(@NotNull DemarcheUsager demarcheUsager) {
		switch (demarcheUsager.getDemarche().getCode()) {
			case CARTE_STATIONNEMENT:
				updateDemarcheUsagerForCarteStationnement(demarcheUsager);
				break;
			case RESTAURATION_SCOLAIRE:
			case DEMANDE_AIDE_PONCTUELLE:
			default:
				break;
		}
	}

	/**
	 * Renvoie les données sous forme de {@link Map} d'objets {@link String} à partir d'un objet {@link IdentitePivot}.
	 * Les clés dans la {@link Map} sont les noms français des champs.
	 * La valeur dans la Map pour l'adresse {@code "adressesConnues"} est une liste d'adresse complète.
	 * @param identitePivot {@link IdentitePivot} à traiter
	 * @return la {@link Map} correspondant aux données de l'objet
	 */
	public static Map<String, String> getDataFromIdentitePivot(@NotNull IdentitePivot identitePivot) {
		Map<String, String> map = new HashMap<>();
		map.put("nom", identitePivot.getFamilyName());
		map.put("prenoms", identitePivot.getGivenName());
		map.put("paysNaissance", identitePivot.getBirthcountry());
		map.put("villeNaissance", identitePivot.getBirthplace());
		map.put("dateNaissance", CommonUtils.convertLocalDateToStringNullSafe(identitePivot.getBirthdate(), "dd-MM-yyyy"));
		map.put("telephone", identitePivot.getPhoneNumber());
		map.put("email", identitePivot.getEmail());
		map.put("nomUsage", identitePivot.getPreferredUsername());

		// Gestion des Adresses
		if(identitePivot.getAddress() != null ){
			map.put(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES, getAdresseCompleteFromAdresse(identitePivot.getAddress()));
		}

		return map;
	}

	/**
	 * Renvoie l'adresse complète à partir d'un objet {@link Adresse}.
	 * @param adresse {@link Adresse} à traiter
	 * @return la concaténation de l'adresse au format cible (Rue codePostal ville pays)
	 */
	public static String getAdresseCompleteFromAdresse(@NotNull Adresse adresse) {
		return StringUtils.trimToEmpty(StringUtils.joinWith(" ",
				adresse.getStreetAddress(),
				adresse.getPostalCode(),
				adresse.getLocality(),
				adresse.getCountry()
				));
	}

	/**
	 * Met à jour la {@link Map} des données avec les données d'un objet {@link Declaration}.
	 * @param mapDonnees {@link Map} des données
	 * @param declaration {@link Declaration} à utiliser
	 */
	public static void updateDataFromDeclaration(@NotNull Map<String, String> mapDonnees, @NotNull Declaration declaration) {

		mapDonnees.put("revenuImposable", Objects.toString(declaration.getRevenuImposable()));
		mapDonnees.put("revenuFiscalReference", Objects.toString(declaration.getRevenuFiscalReference()));
		mapDonnees.put("nombrePersonnesCharge", Objects.toString(declaration.getNombrePersonnesCharge()));
		mapDonnees.put("montantImpot", Objects.toString(declaration.getMontantImpot()));
		mapDonnees.put("nombrePart", declaration.getNombreParts() != null ? ""+declaration.getNombreParts().intValue() : "");

		if(!Objects.isNull(declaration.getFoyerFiscal())) {
			// un adresse est connue et qu'elle ne contient pas l'adresse du foyer fiscal, on l'ajoute
			if(mapDonnees.containsKey(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES)
					&& !mapDonnees.get(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES).contains(declaration.getFoyerFiscal().getAdresse())) {
				mapDonnees.put(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES,
						StringUtils.joinWith("|", mapDonnees.get(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES),
								declaration.getFoyerFiscal().getAdresse()));
			}
			else {
				mapDonnees.put(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES,declaration.getFoyerFiscal().getAdresse());
			}
		}
	}

	/**
	 * Réalise une mise à jour de la {@link Map} des données pour les entrées contenant des listes ou des tableaux d'objets.
	 * Si la liste ou le tableau considére ne contiennent qu'un élément, l'entrée de la donnée sélectionnée est rempli avec la valeur correspondante.
	 * Pour les {@link String} on utillise simplement la valeur.
	 * Pour les listes d'objets au format JSON, le JSON de l'objet sélectionnée est utilisée comme valeur.
	 * @param mapDonnees {@link Map} des données
	 */
	public static void updateDataForListValues(@NotNull Map<String, String> mapDonnees) {
		String adressesConnues = mapDonnees.get(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES);
		// Si l'usager n'a qu'une adresse connue, on l'utilise directement
		if(!StringUtils.contains(adressesConnues, "|")) {
			mapDonnees.put("adresseComplete", adressesConnues);
		}

		// Si l'usager n'a qu'un Vehicule associé, il devient d'emblée le véhicule sélectionné
		String vehicules = mapDonnees.get("vehicules");
		if(StringUtils.isNotBlank(vehicules)) {
			try {
				List<Map<String,String>> vehiculesList = CommonUtils.getDefaultMapper().readValue(vehicules, new TypeReference<List<Map<String,String>>>() {
				});
				if(vehiculesList.size() == 1) {
					mapDonnees.put(MAP_DATA_DEMARCHE_KEYS_VEHICULE_SELECTIONNE, CommonUtils.getDefaultMapper().writeValueAsString(vehiculesList.get(0)));
				}
			}
			catch (JsonProcessingException e) {
				LOGGER.error("Erreur lors de la lecture des données des véhicules de l'usager {}", mapDonnees.get("vehicules"));
			}
		}
	}

	/**
	 * Met à jour la {@link Map} des données avec les données d'un objet {@link Famille}.
	 * @param mapDonnees {@link Map} des données à mettre à jour
	 * @param famille {@link Famille} données de la Famille de l'usager à utiliser
	 */
	public static void updateDataFromFamille(@NotNull Map<String, String> mapDonnees, @NotNull Famille famille) {
		mapDonnees.put("quotientFamilial", ""+famille.getQuotientFamilial());
		mapDonnees.put("nombreEnfantACharge", ""+CollectionUtils.size(famille.getEnfants()));

		// Gestion de l'adresse
		if(!Objects.isNull(famille.getAdresse())) {
			String adresseComplete = StringUtils.joinWith(" ",
					famille.getAdresse().getNumeroRue(),
					famille.getAdresse().getComplementIdentite(),
					famille.getAdresse().getComplementIdentiteGeo(),
					famille.getAdresse().getCodePostalVille(),
					famille.getAdresse().getLieuDit(),
					famille.getAdresse().getPays()
			);
			if(mapDonnees.containsKey(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES)) {
				mapDonnees.put(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES,
						StringUtils.joinWith("|", mapDonnees.get(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES),
								adresseComplete));
			}
			else {
				mapDonnees.put(MAP_DATA_DEMARCHE_KEYS_ADRESSES_CONNUES,adresseComplete);
			}
		}

		// Gestion des enfants
		mapDonnees.put("enfants", CommonUtils.convertObjectToJson(CollectionUtils.emptyIfNull(famille.getEnfants()), "Famille.enfants"));
		if(CollectionUtils.emptyIfNull(famille.getEnfants()).size() == 1) {
			mapDonnees.put("enfantSelectionnee", CommonUtils.convertObjectToJson(famille.getEnfants().get(0), "enfantSelectionnee"));
		}


		// Gestion des allocataires
		mapDonnees.put("allocataires", CommonUtils.convertObjectToJson(CollectionUtils.emptyIfNull(famille.getAllocataires()), "Famille.allocataires"));
	}

	/**
	 * Mets à jour un objet {@link DemarcheUsager} spécifiquement pour une démarche de demande de
	 * carte de stationnement ({@link DemarcheEnum#CARTE_STATIONNEMENT}).
	 * En fin de traitement, le champ {@link CarteStationnement} est renseigné dans l'objet {@link Demarche}.
	 * @param demarcheUsager {@link DemarcheUsager} à traiter
	 */
	private static void updateDemarcheUsagerForCarteStationnement(@NotNull DemarcheUsager demarcheUsager) {
		String vehiculeSelectionne = demarcheUsager.getRawData().get(MAP_DATA_DEMARCHE_KEYS_VEHICULE_SELECTIONNE);
		if(StringUtils.isNotBlank(vehiculeSelectionne)) {
			try {
				CarteStationnement carteStationnement = CommonUtils.getDefaultMapper().readValue(
						vehiculeSelectionne, CarteStationnement.class
				);
				carteStationnement.setSiret(demarcheUsager.getRawData().get("siret"));
				carteStationnement.setRaisonSociale(demarcheUsager.getRawData().get("raisonSociale"));

				demarcheUsager.getDemarche().setCarteStationnement(carteStationnement);
				TypeReference<Map<String,String>> typeRef = new TypeReference<>() {};
				// AJout des données du véhicules sélectionnée dans les données brutes
				CommonUtils.getDefaultMapper().convertValue(carteStationnement, typeRef)
						.forEach((key,value) -> demarcheUsager.getRawData().put(key, value));
			}
			catch (JsonProcessingException e) {
				LOGGER.error("Impossible de lire la donnée du véhicule sélectionnée pour la démarche {}",demarcheUsager.getId(), e);
				throw new BadRequestException(MAP_DATA_DEMARCHE_KEYS_VEHICULE_SELECTIONNE);
			}
		}
	}

	/**
	 * Constructeur privée pour protéger le constructeur par défaut
	 */
	private DemarcheUtils(){
		//Constructeur privée pour protéger le constructeur par défaut
	}
}
