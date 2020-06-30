package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Person;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class DonneeUsagerUtils {

	/**
	 * Code source pour les données FranceConnect
	 */
	public static final String CODE_SOURCE_FRANCECONNECT = "FC";

	/**
	 * Code source pour les données de la DGFIP (via API Particuliers)
	 */
	public static final String CODE_SOURCE_DGFIP = "DGFIP";

	/**
	 * Code source pour les données de la CNAM (via API Particuliers)
	 */
	public static final String CODE_SOURCE_CNAM = "CNAM";

	/**
	 * Code source pour les données de l'API ANTS
	 */
	public static final String CODE_SOURCE_ANTS = "ANTS";

	/**
	 * Code source pour les données de l'API Entreprise
	 */
	public static final String CODE_SOURCE_ENTREPRISE = "ENTREPRISE";

	/**
	 * Code source pour les données sélectionnées par l'Usager
	 */
	public static final String CODE_SOURCE_SELECTION = "SELECTION";

	/**
	 * Format String des DonneeUsager
	 */
	public static final String FORMAT_DONNEE_USAGER_STRING = "string";

	/**
	 * Format List des DonneeUsager
	 */
	public static final String FORMAT_DONNE_USAGER_LIST = "list";

	/**
	 * Format Objet des DonneeUsager
	 */
	public static final String FORMAT_DONNEE_USAGER_OBJECT = "object";

	/**
	 * Nom du champ date de naissance
	 */
	public static final String DONNEE_USAGER_DATE_NAISSANCE = "dateNaissance";

	/**
	 * Nom du champ adresse complète
	 */
	public static final String DONNEE_USAGER_ADRESSE_COMPLETE = "adresseComplete";
	public static final String DONNEE_USAGER_ADRESSE_PARENT = "adresseParent";

	/**
	 * Renvoie la {@link Map} de toutes les données de l'usager par source correspondant à sa situation actuelle.
	 * @param situationUsager {@link SituationUsager} situation de l'usager à utiliser
	 * @return la {@link Map} des {@link DonneeUsager} correspondantes aux données disponibles.
	 */
	public static Map<String, List<DonneeUsager>> getMapFromSituationUsager(SituationUsager situationUsager) {
		Map<String, List<DonneeUsager>> result = new HashMap<>();

		if(Objects.isNull(situationUsager)) {
			return result;
		}

		if( Objects.nonNull(situationUsager.getSessionUsager()) && Objects.nonNull(situationUsager.getSessionUsager().getIdentitePivot())) {
			result.put(CODE_SOURCE_FRANCECONNECT, getListDonneeUsagerFromIdentitePivot(situationUsager.getSessionUsager().getIdentitePivot()));
		}

		if(Objects.nonNull(situationUsager.getFamille())) {
			result.put(CODE_SOURCE_CNAM, getDonneeUsagerFromFamille(situationUsager.getFamille()));
		}

		if(Objects.nonNull(situationUsager.getBeneficiaireCnam())) {
			result.put(CODE_SOURCE_CNAM, getDonneeUsagerFromBeneficiaire(situationUsager.getBeneficiaireCnam()));
		}

		if(Objects.nonNull(situationUsager.getDeclaration())) {
			result.put(CODE_SOURCE_DGFIP, getDonneeUsagerFromDeclaration(situationUsager.getDeclaration()));
		}

		result.put(CODE_SOURCE_SELECTION, new ArrayList<>());

		return result;
	}

	/**
	 * Renvoie la {@link List} des données correspondante à l'objet {@link IdentitePivot}.
	 * @param identitePivot {@link IdentitePivot} à traiter
	 * @return {@link List} des {@link DonneeUsager} correspondante à l'objet {@link IdentitePivot}
	 */
	public static List<DonneeUsager> getListDonneeUsagerFromIdentitePivot(IdentitePivot identitePivot) {
		List<DonneeUsager> listDonnees = new ArrayList<>();

		if(Objects.isNull(identitePivot)) {
			return listDonnees;
		}
		listDonnees.add(createDonneeUsager("nom", CODE_SOURCE_FRANCECONNECT, identitePivot.getFamilyName()));
		listDonnees.add(createDonneeUsager("nomParent", CODE_SOURCE_FRANCECONNECT, identitePivot.getFamilyName()));
		listDonnees.add(createDonneeUsager("prenoms", CODE_SOURCE_FRANCECONNECT, identitePivot.getGivenName()));
		listDonnees.add(createDonneeUsager("prenomParent", CODE_SOURCE_FRANCECONNECT, identitePivot.getGivenName()));
		listDonnees.add(createDonneeUsager("paysNaissance", CODE_SOURCE_FRANCECONNECT, identitePivot.getBirthcountry()));
		listDonnees.add(createDonneeUsager("villeNaissance", CODE_SOURCE_FRANCECONNECT, identitePivot.getBirthplace()));
		listDonnees.add(createDonneeUsager(DONNEE_USAGER_DATE_NAISSANCE, CODE_SOURCE_FRANCECONNECT, CommonUtils.convertLocalDateToStringNullSafe(identitePivot.getBirthdate(), "dd-MM-yyyy")));
		listDonnees.add(createDonneeUsager("telephone", CODE_SOURCE_FRANCECONNECT, identitePivot.getPhoneNumber()));
		listDonnees.add(createDonneeUsager("email", CODE_SOURCE_FRANCECONNECT, identitePivot.getEmail()));
		listDonnees.add(createDonneeUsager("nomUsage", CODE_SOURCE_FRANCECONNECT, identitePivot.getPreferredUsername()));

		// Gestion des Adresses
		if(identitePivot.getAddress() != null ){
			listDonnees.add(
					createDonneeUsager(DONNEE_USAGER_ADRESSE_COMPLETE, CODE_SOURCE_FRANCECONNECT, StringUtils.trimToEmpty(StringUtils.joinWith(" ",
							identitePivot.getAddress().getStreetAddress(),
							identitePivot.getAddress().getPostalCode(),
							identitePivot.getAddress().getLocality(),
							identitePivot.getAddress().getCountry()
					))
					)
			);
			listDonnees.add(
					createDonneeUsager(DONNEE_USAGER_ADRESSE_PARENT, CODE_SOURCE_FRANCECONNECT, StringUtils.trimToEmpty(StringUtils.joinWith(" ",
							identitePivot.getAddress().getStreetAddress(),
							identitePivot.getAddress().getPostalCode(),
							identitePivot.getAddress().getLocality(),
							identitePivot.getAddress().getCountry()
					))
					)
			);
		}

		return listDonnees;
	}

	/**
	 * Renvoie la {@link List} des données correspondante à l'objet {@link Declaration}.
	 * @param declaration {@link Declaration} à utiliser
	 * @return la {@link List} des {@link DonneeUsager} correspondante à l'objet {@link Declaration}
	 */
	public static List<DonneeUsager> getDonneeUsagerFromDeclaration(Declaration declaration) {
		List<DonneeUsager> listDonnees = new ArrayList<>();

		if(Objects.isNull(declaration)) {
			return listDonnees;
		}

		listDonnees.add(createDonneeUsager("revenuImposable", CODE_SOURCE_DGFIP, Objects.toString(declaration.getRevenuImposable())));
		listDonnees.add(createDonneeUsager("revenuFiscalReference", CODE_SOURCE_DGFIP, Objects.toString(declaration.getRevenuFiscalReference())));
		listDonnees.add(createDonneeUsager("nombrePersonnesCharge", CODE_SOURCE_DGFIP, Objects.toString(declaration.getNombrePersonnesCharge())));
		listDonnees.add(createDonneeUsager("montantImpot", CODE_SOURCE_DGFIP, Objects.toString(declaration.getMontantImpot())));
		listDonnees.add(createDonneeUsager("nombrePart", CODE_SOURCE_DGFIP, declaration.getNombreParts() != null ? ""+declaration.getNombreParts().intValue() : ""));

		if(!Objects.isNull(declaration.getFoyerFiscal())) {
			listDonnees.add(createDonneeUsager(DONNEE_USAGER_ADRESSE_COMPLETE, CODE_SOURCE_DGFIP,declaration.getFoyerFiscal().getAdresse()));
			listDonnees.add(createDonneeUsager(DONNEE_USAGER_ADRESSE_PARENT, CODE_SOURCE_DGFIP,declaration.getFoyerFiscal().getAdresse()));
		}

		return listDonnees;
	}

	/**
	 * Renvoie la {@link List} des données correspondante à l'objet {@link Beneficiaire}.
	 * @param beneficiaire {@link Beneficiaire} à utiliser
	 * @return la {@link List} des {@link DonneeUsager} correspondante à l'objet {@link Beneficiaire}
	 */
	public static List<DonneeUsager> getDonneeUsagerFromBeneficiaire(Beneficiaire beneficiaire) {
		List<DonneeUsager> listDonnees = new ArrayList<>();

		if(Objects.isNull(beneficiaire)) {
			return listDonnees;
		}

		listDonnees.add(createDonneeUsager(DONNEE_USAGER_DATE_NAISSANCE, CODE_SOURCE_CNAM,
				CommonUtils.convertLocalDateToStringNullSafe(beneficiaire.getDateNaissance(), "dd/MM/yyyy")));
		listDonnees.add(createDonneeUsager("medecinTraitantChoisi", CODE_SOURCE_CNAM, Boolean.toString(beneficiaire.isMedecinTraitantChoisi())));
		listDonnees.add(createDonneeUsager("presenceCMUCACS", CODE_SOURCE_CNAM, Boolean.toString(beneficiaire.isPresenceCMUCACS())));
		listDonnees.add(createDonneeUsager("nir", CODE_SOURCE_CNAM, beneficiaire.getNir()));
		listDonnees.add(createDonneeUsager("nom", CODE_SOURCE_CNAM, beneficiaire.getNomFamille()));
		listDonnees.add(createDonneeUsager("nomUsager", CODE_SOURCE_CNAM, beneficiaire.getNomUsage()));
		listDonnees.add(createDonneeUsager("prenom", CODE_SOURCE_CNAM, beneficiaire.getPrenom()));


		return listDonnees;
	}

	/**
	 * Renvoie la liste des {@link DonneeUsager} correspondant à un objet {@link Famille}.
	 * @param famille {@link Famille} données de la Famille de l'usager à utiliser
	 * @return la {@link List} des {@link DonneeUsager} représentant l'objet {@link Famille}
	 */
	public static List<DonneeUsager> getDonneeUsagerFromFamille(Famille famille) {
		List<DonneeUsager> listDonnees = new ArrayList<>();

		if(Objects.isNull(famille)) {
			return listDonnees;
		}

		listDonnees.add(createDonneeUsager("quotientFamilial", CODE_SOURCE_CNAM, ""+famille.getQuotientFamilial()));
		listDonnees.add(createDonneeUsager("nombreEnfantACharge", CODE_SOURCE_CNAM, ""+ CollectionUtils.size(famille.getEnfants())));

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
			listDonnees.add(createDonneeUsager(DONNEE_USAGER_ADRESSE_COMPLETE, CODE_SOURCE_CNAM,adresseComplete));
			listDonnees.add(createDonneeUsager(DONNEE_USAGER_ADRESSE_PARENT, CODE_SOURCE_CNAM,adresseComplete));
		}

		// Gestion des enfants
		listDonnees.add(
				createDonneeUsager(
						"enfants", CODE_SOURCE_CNAM, null, FORMAT_DONNE_USAGER_LIST,
						famille.getEnfants().stream()
								.filter(Objects::nonNull)
								.map(person -> getDonneeUsagerFromPerson(person, true))
								.collect(Collectors.toList())
				)
		);

		// Gestion des allocataires
		listDonnees.add(
				createDonneeUsager(
						"allocataires", CODE_SOURCE_CNAM, null, FORMAT_DONNE_USAGER_LIST,
						famille.getAllocataires().stream()
								.filter(Objects::nonNull)
								.map(person -> getDonneeUsagerFromPerson(person, false))
								.collect(Collectors.toList())
				)
		);

		return listDonnees;
	}

	/**
	 * Convertit un objet {@link Person} en objet {@link DonneeUsager}.
	 * @param person {@link Person} objet à convertir. Ne peut être null.
	 * @param isEnfant flag indiquant si l'objet représent un enfant ou un allocataire
	 * @return {@link DonneeUsager} correspondant à l'objet {@link Person}
	 */
	private static DonneeUsager getDonneeUsagerFromPerson(@NotNull Person person, boolean isEnfant) {
		String prefixNom = isEnfant ? "enfant" : "allocataire";
		List<DonneeUsager> donneesPerson = new ArrayList<>();
		donneesPerson.add(createDonneeUsager(prefixNom+"-nomPrenom", CODE_SOURCE_CNAM, person.getNomPrenom()));
		donneesPerson.add(createDonneeUsager(prefixNom+"-sexe", CODE_SOURCE_CNAM, person.getSexe()));
		donneesPerson.add(createDonneeUsager(prefixNom+"-"+ DONNEE_USAGER_DATE_NAISSANCE, CODE_SOURCE_CNAM,
				CommonUtils.convertStringDateToNewFormat(person.getDateDeNaissance(), "ddMMyyyy", "dd/MM/yyyy")));

		return createDonneeUsager(prefixNom, CODE_SOURCE_CNAM, null, FORMAT_DONNEE_USAGER_OBJECT, donneesPerson);
	}

	/**
	 * MOCK - Renvoie les données simulées pour l'API Entreprise
	 * @param siret {@link String} SIRET de l'entreprise
	 * @param raisonSociale {@link String} Raison sociale de l'entreprise
	 * @return {@link List} de {@link DonneeUsager} correspondant au siret et à la raison sociale.
	 */
	public static List<DonneeUsager> getDonneesUsagerFromEntreprise(String siret, String raisonSociale) {
		List<DonneeUsager> donneesEntreprise = new ArrayList<>();
		donneesEntreprise.add(createDonneeUsager("siret", CODE_SOURCE_ENTREPRISE, siret));
		donneesEntreprise.add(createDonneeUsager("raisonSociale", CODE_SOURCE_ENTREPRISE, raisonSociale));
		return donneesEntreprise;
	}

	/**
	 * MOCK - Renvoie les données simulées pour les véhicules.
	 * @return {@link List} de {@link DonneeUsager} avec une liste de 3 véhicules par défaut.
	 */
	public static List<DonneeUsager> getDonneesUsagerFromAnts() {
		List<DonneeUsager> listDonneeUsager = new ArrayList<>();

		List<DonneeUsager> vehicules = new ArrayList<>();
		vehicules.add(getDonneeUsagerForVehicule("vehicule_1","Kia Soul","AA-123-AA",false ));
		vehicules.add(getDonneeUsagerForVehicule("vehicule_2","Peugeot 208","BB-456-BB",false ));
		vehicules.add(getDonneeUsagerForVehicule("vehicule_3","Opel Corsa","CC-789-CC",true ));

		listDonneeUsager.add(createDonneeUsager("vehicules", CODE_SOURCE_ANTS, null, FORMAT_DONNE_USAGER_LIST, vehicules));
		return listDonneeUsager;
	}

	/**
	 * Renvoi l'objet {@link DonneeUsager} correspondant au donnée d'un vehicule en fonction des paramètres.
	 *
	 * @param id {@link String} identifiant du véhicule
	 * @param modele {@link String} modèle du véhicule
	 * @param immatriculation {@link String} immatriculation du véhicule
	 * @param electrique flag indiquant si le vehicule est électrique
	 * @return {@link DonneeUsager} correspondant à l'objet {@link Person}
	 */
	private static DonneeUsager getDonneeUsagerForVehicule(String id, String modele, String immatriculation, boolean electrique) {
		List<DonneeUsager> donneesVehicule = new ArrayList<>();
		donneesVehicule.add(createDonneeUsager("id", CODE_SOURCE_ANTS, id));
		donneesVehicule.add(createDonneeUsager("modele", CODE_SOURCE_ANTS, modele));
		donneesVehicule.add(createDonneeUsager("immatriculation", CODE_SOURCE_ANTS, immatriculation));
		donneesVehicule.add(createDonneeUsager("electrique", CODE_SOURCE_ANTS, Boolean.toString(electrique)));

		return createDonneeUsager("voiture", CODE_SOURCE_ANTS, null, FORMAT_DONNEE_USAGER_OBJECT, donneesVehicule);
	}

	/**
	 * Crée un objet {@link DonneeUsager} à partir des paramètres.
	 * Le format de la donnée sera toujours string.
	 * @param name {@link String} nom de la donnée
	 * @param codeSource {@link String} code de la source de la donnée
	 * @param valeur {@link String} valeur de la donnée
	 * @return {@link DonneeUsager} correspondant
	 */
	public static DonneeUsager createDonneeUsager(String name, String codeSource, String valeur) {
		return createDonneeUsager(name, codeSource, valeur, FORMAT_DONNEE_USAGER_STRING, null);
	}

	/**
	 * Renvoie un objet {@link DonneeUsager} en fonction des paramètres.
	 * @param name {@link String} nom de la donnée
	 * @param codeSource {@link String} code de la source de la donnée
	 * @param valeur {@link String} valeur de la donnée
	 * @param format {@link String} le format de la donnée parmi string, list et object
	 * @param donnees {@link List} des {@link DonneeUsager} de la donnée
	 * @return {@link DonneeUsager} correspondant
	 */
	private static DonneeUsager createDonneeUsager(String name, String codeSource, String valeur, String format, List<DonneeUsager> donnees) {
		DonneeUsager donneeUsager = new DonneeUsager();
		donneeUsager.setName(name);
		donneeUsager.setCodeSource(codeSource);
		donneeUsager.setFormat(format);
		donneeUsager.setValeur(valeur);
		donneeUsager.setListeDonnees(donnees);
		return donneeUsager;
	}

	/**
	 * Protection du constructeur par défaut
	 */
	private DonneeUsagerUtils() {
		// Constructeur privée protéger
	}
}
