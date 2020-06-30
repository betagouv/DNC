package fr.gouv.modernisation.dinum.dnc.situationusager.factory;

import com.github.javafaker.Faker;
import fr.gouv.modernisation.dinum.dnc.common.utils.UsagerUtils;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.Adresse;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declarant;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.FoyerFiscal;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Person;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.PosteAdresse;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.FetchDataService;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.CommonUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestDataFactory {

	private final Faker faker = new Faker(new Locale("fr-FR"));

	private FetchDataService fetchDataService = new FetchDataService();

	public DemarcheUsager defaultDemarcheUsager(DemarcheEnum typeDemarche, String siretPartenaire) {

		SessionUsager sessionUsager = defaultSessionUsager("test");

		Map<String, String> donneesComplementaires = new HashMap<>();
		donneesComplementaires.put("siretPartenaire", siretPartenaire);

		Map<String, String> donneesBrutes = fetchDataService.getDonneesForDemarche(typeDemarche, sessionUsager, donneesComplementaires);
		donneesBrutes.put("immatriculation","AA-123-AA");
		donneesBrutes.put("modele","Kia Soul");
		donneesBrutes.put("electrique","true");
		donneesBrutes.put("raisonSociale","DIRECTION INTERMINISTERIELLE DU NUMERIQUE");
		donneesBrutes.put("siret","13002526500013");
		// Conversion des données brutes en objets
		Demarche demarche = DemarcheUtils.getDemarcheFromRawData(typeDemarche, donneesBrutes);

		// Création de l'objet DemarcheUsager correspondant
		DemarcheUsager demarcheUsager = new DemarcheUsager(demarche);
		demarcheUsager.setIdUsager(sessionUsager.getUserId());
		demarcheUsager.setModifiable(true);
		// Ajout de l'identifiant de la démarche dans les données brutes
		donneesBrutes.put("idDemarche", demarcheUsager.getId().toString());
		// Ajout des données brutes dans l'objet
		demarcheUsager.setRawData(donneesBrutes);

		return demarcheUsager;
	}

	public SituationUsager defaultSituationUsager(String scopes) {

		SessionUsager sessionUsager = defaultSessionUsager(scopes);

		SituationUsager situationUsager = new SituationUsager(sessionUsager);
		situationUsager.setSessionUsager(sessionUsager);

		Beneficiaire beneficiaire = defaultBeneficiaire(situationUsager);
		situationUsager.setBeneficiaireCnam(beneficiaire);

		Declaration declaration = defaultDeclaration(sessionUsager.getIdentitePivot());
		situationUsager.setDeclaration(declaration);

		return situationUsager;
	}

	public SessionUsager defaultSessionUsager(String scopes) {
		SessionUsager sessionUsager = new SessionUsager();
		sessionUsager.setIdentitePivot(defaultIdentitePivot());
		sessionUsager.setUserId(UsagerUtils.getUsagerIdFromIdentitePivot(sessionUsager.getIdentitePivot()));
		sessionUsager.setIdSession(UUID.randomUUID().toString());
		sessionUsager.setCurrentToken(UUID.randomUUID().toString());
		sessionUsager.setScopes(scopes);
		return sessionUsager;
	}

	public IdentitePivot defaultIdentitePivot() {
		IdentitePivot identitePivot = new IdentitePivot();
		identitePivot.setFamilyName(faker.name().lastName());
		identitePivot.setGivenName(faker.name().firstName());
		identitePivot.setGender("male");
		identitePivot.setBirthdate(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		identitePivot.setBirthcountry(faker.country().name());
		identitePivot.setBirthplace(faker.country().capital());
		identitePivot.setSub(UUID.randomUUID().toString());
		identitePivot.setPhoneNumber(faker.phoneNumber().phoneNumber());
		identitePivot.setEmail(faker.internet().emailAddress());

		Adresse adresse = new Adresse();
		adresse.setPostalCode(faker.address().zipCode());
		adresse.setLocality(faker.address().cityName());
		adresse.setStreetAddress(faker.address().streetAddress());
		adresse.setCountry("99100");
		adresse.setFormatted(faker.address().fullAddress());
		identitePivot.setAddress(adresse);

		return identitePivot;
	}

	public Declaration defaultDeclaration(IdentitePivot identitePivot) {
		Declaration declaration = new Declaration();
		declaration.setNombreParts(BigDecimal.valueOf(faker.number().randomDigit()));
		declaration.setNombrePersonnesCharge(faker.number().randomDigit());
		declaration.setMontantImpot(faker.number().numberBetween(1000, 100000));
		declaration.setRevenuImposable(faker.number().numberBetween(1000, 100000));
		declaration.setImpotRevenuNetAvantCorrections(faker.number().numberBetween(1000, 100000));
		declaration.setAnneeRevenus(""+LocalDate.now().minusYears(2).getYear());
		declaration.setDateEtablissement(LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		declaration.setDateRecouvrement(LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

		Declarant declarant1 = new Declarant();
		declarant1.setNomNaissance(identitePivot.getFamilyName());
		declarant1.setNom(identitePivot.getPreferredUsername());
		declarant1.setPrenoms(identitePivot.getGivenName());
		declarant1.setDateNaissance(CommonUtils.convertLocalDateToStringNullSafe(identitePivot.getBirthdate(), "dd/MM/yyyy"));
		declaration.setDeclarant1(declarant1);

		Declarant declarant2 = new Declarant();
		declarant1.setNomNaissance(faker.name().lastName());
		declarant1.setNom(identitePivot.getFamilyName());
		declarant1.setPrenoms(faker.name().firstName());
		declarant1.setDateNaissance(CommonUtils.convertLocalDateToStringNullSafe(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), "dd/MM/yyyy"));
		declaration.setDeclarant2(declarant2);

		FoyerFiscal foyerFiscal = new FoyerFiscal();
		foyerFiscal.setAnnee(LocalDate.now().minusYears(2).getYear());
		foyerFiscal.setAdresse(faker.address().fullAddress());
		declaration.setFoyerFiscal(foyerFiscal);

		return declaration;
	}

	public Beneficiaire defaultBeneficiaire(SituationUsager situationUsager) {
		Beneficiaire beneficiaire = new Beneficiaire();
		beneficiaire.setNomFamille(situationUsager.getSessionUsager().getIdentitePivot().getFamilyName());
		beneficiaire.setPrenom(situationUsager.getSessionUsager().getIdentitePivot().getGivenName());
		beneficiaire.setDateNaissance(situationUsager.getSessionUsager().getIdentitePivot().getBirthdate());
		beneficiaire.setNomUsage(situationUsager.getSessionUsager().getIdentitePivot().getPreferredUsername());
		beneficiaire.setNir(faker.number().digits(13));
		beneficiaire.setMedecinTraitantChoisi(faker.bool().bool());
		beneficiaire.setPresenceCMUCACS(faker.bool().bool());
		beneficiaire.setRangNaissance(faker.number().randomDigit());
		beneficiaire.setQualite(Beneficiaire.QualiteEnum.values()[faker.number().numberBetween(0,Beneficiaire.QualiteEnum.values().length)]);
		return beneficiaire;
	}

	public Famille defaultFamille(SessionUsager sessionUsager) {
		Famille famille = new Famille();
		PosteAdresse posteAdresse = new PosteAdresse();
		posteAdresse.setNumeroRue(faker.address().streetAddressNumber());
		posteAdresse.setIdentite(faker.address().streetAddress());
		posteAdresse.setComplementIdentite(faker.address().secondaryAddress());
		posteAdresse.setCodePostalVille(faker.address().zipCode());
		posteAdresse.setLieuDit(faker.address().city());
		posteAdresse.setPays(faker.address().country());
		famille.setAdresse(posteAdresse);

		famille.setQuotientFamilial(faker.number().numberBetween(0,9));

		Person person = new Person();
		person.setNomPrenom( sessionUsager.getIdentitePivot().getFamilyName() + " " + sessionUsager.getIdentitePivot().getGivenName());
		person.setDateDeNaissance(sessionUsager.getIdentitePivot().getBirthdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		person.setSexe(sessionUsager.getIdentitePivot().getGender());

		famille.addAllocatairesItem(person);

		if(faker.bool().bool()) {
			Person autreAllocataire = new Person();
			autreAllocataire.setNomPrenom( faker.name().fullName() );
			autreAllocataire.setDateDeNaissance(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
			autreAllocataire.setSexe(faker.bool().bool() ? "F" : "M");
			famille.addAllocatairesItem(autreAllocataire);
		}

		if(faker.bool().bool()) {
			for(int i = 0 ; i < faker.number().numberBetween(1,5) ; i++) {
				Person enfant = new Person();
				enfant.setNomPrenom( faker.name().fullName() );
				enfant.setDateDeNaissance(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
				enfant.setSexe(faker.bool().bool() ? "F" : "M");
				famille.addEnfantsItem(enfant);
			}
		}

		return famille;
	}

	public Map<String, List<DonneeUsager>> getMapDonneeUsagerForDemarche(DemarcheEnum demarcheEnum, SituationUsager situationUsager) {
		Map<String, String> donneesComplementaires = new HashMap<>();
		donneesComplementaires.put("raisonSociale","DIRECTION INTERMINISTERIELLE DU NUMERIQUE");
		donneesComplementaires.put("siret","13002526500013");

		Map<String, List<DonneeUsager>> mapDonneeUsager = fetchDataService.getDonneesUsagerForDemarche(demarcheEnum, situationUsager.getSessionUsager(), donneesComplementaires);

		mapDonneeUsager.putIfAbsent(DonneeUsagerUtils.CODE_SOURCE_SELECTION, new ArrayList<>());

		if(mapDonneeUsager.containsKey(DonneeUsagerUtils.CODE_SOURCE_ANTS) && DemarcheEnum.CARTE_STATIONNEMENT.equals(demarcheEnum)) {
			// Récupération d'un véhicule dans la liste possible
			DonneeUsager vehicule = mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_ANTS).get(0).getListeDonnees().get(0);
			//vehicule.getListeDonnees().forEach(donneeVehicule -> donneeVehicule.setName("carteStationnement-"+donneeVehicule.getName()));
			mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).addAll(vehicule.getListeDonnees());
			mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).addAll(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_ENTREPRISE));
		}

		return mapDonneeUsager;
	}
}
