package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import fr.gouv.modernisation.dinum.dnc.common.exception.BadRequestException;
import fr.gouv.modernisation.dinum.dnc.common.utils.UsagerUtils;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.factory.TestDataFactory;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.CarteStationnement;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declarant;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.FoyerFiscal;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DemarcheUtilsTest {

	private final Faker faker = new Faker(new Locale("fr-FR"));

	private TestDataFactory testDataFactory = new TestDataFactory();

	@Test
	public void getDefaultMapperTest() {
		Map<String, String> rawData = new HashMap<>();
		rawData.put("nom", faker.name().lastName());
		rawData.put("prenoms", faker.name().firstName());
		rawData.put("adresseComplete", faker.address().fullAddress());
		rawData.put("telephone", faker.phoneNumber().phoneNumber());
		rawData.put("email", faker.internet().emailAddress());
		rawData.put("siretPartenaire", "13002526500013");

		Demarche demarche = DemarcheUtils.getDemarcheFromRawData(DemarcheEnum.DEMANDE_AIDE_PONCTUELLE,
				rawData);

		assertEquals(DemarcheEnum.DEMANDE_AIDE_PONCTUELLE,demarche.getCode());
		assertEquals(rawData.get("siretPartenaire"),demarche.getSiretPartenaire());
		assertEquals(rawData.get("nom"),demarche.getDemandeur().getNom());
		assertEquals(rawData.get("prenoms"),demarche.getDemandeur().getPrenoms());
		assertEquals(rawData.get("adresseComplete"),demarche.getDemandeur().getAdresseComplete());
		assertEquals(rawData.get("telephone"),demarche.getDemandeur().getTelephone());
		assertEquals(rawData.get("email"),demarche.getDemandeur().getEmail());
	}

	@Test
	public void updateDemarcheUsagerWithRawDataForCarteStationnementTest() {
		Map<String, String> rawData = new HashMap<>();
		rawData.put("nom", faker.name().lastName());
		rawData.put("prenoms", faker.name().firstName());
		rawData.put("adresseComplete", faker.address().fullAddress());
		rawData.put("telephone", faker.phoneNumber().phoneNumber());
		rawData.put("email", faker.internet().emailAddress());
		rawData.put("siretPartenaire", "13002526500013");
		rawData.put("siret", "13002526500013");
		rawData.put("raisonSociale", "DINUM");
		rawData.put("vehiculeSelectionne", "{\"id\": \"vehicule_3\",\"immatriculation\": \"CC-789-CC\",\"modele\": \"Mazda RX-7\",\"electrique\": false}");

		Demarche demarche = DemarcheUtils.getDemarcheFromRawData(DemarcheEnum.CARTE_STATIONNEMENT,
				rawData);

		DemarcheUsager demarcheUsager = new DemarcheUsager(demarche);
		demarcheUsager.setRawData(rawData);


		DemarcheUtils.updateDemarcheUsagerWithRawData(demarcheUsager);

		assertNotNull(demarcheUsager.getDemarche().getCarteStationnement());
		assertEquals("13002526500013", demarcheUsager.getDemarche().getCarteStationnement().getSiret());
		assertEquals("DINUM", demarcheUsager.getDemarche().getCarteStationnement().getRaisonSociale());
		assertEquals("CC-789-CC", demarcheUsager.getDemarche().getCarteStationnement().getImmatriculation());
		assertEquals("Mazda RX-7", demarcheUsager.getDemarche().getCarteStationnement().getModele());
		assertEquals(false, demarcheUsager.getDemarche().getCarteStationnement().isElectrique());
	}

	@Test
	public void updateDemarcheUsagerWithRawDataForCarteStationnementMauvaisJsonTest() {
		Map<String, String> rawData = new HashMap<>();
		rawData.put("nom", faker.name().lastName());
		rawData.put("prenoms", faker.name().firstName());
		rawData.put("adresseComplete", faker.address().fullAddress());
		rawData.put("telephone", faker.phoneNumber().phoneNumber());
		rawData.put("email", faker.internet().emailAddress());
		rawData.put("siretPartenaire", "13002526500013");
		rawData.put("siret", "13002526500013");
		rawData.put("raisonSociale", "DINUM");
		//Json mal formé
		rawData.put("vehiculeSelectionne", "ceciNestPasDuJson");

		Demarche demarche = DemarcheUtils.getDemarcheFromRawData(DemarcheEnum.CARTE_STATIONNEMENT,
				rawData);

		DemarcheUsager demarcheUsager = new DemarcheUsager(demarche);
		demarcheUsager.setRawData(rawData);


		assertThrows(BadRequestException.class, () -> DemarcheUtils.updateDemarcheUsagerWithRawData(demarcheUsager));
	}

	@Test
	public void conversionLocalDateTest() {
		assertDoesNotThrow(() -> CommonUtils.convertLocalDateToStringNullSafe(null, "dd/MM/yyyy"));
		assertEquals("16/03/1996", CommonUtils.convertLocalDateToStringNullSafe(LocalDate.of(1996,3,16), "dd/MM/yyyy"));
	}

	@Test
	public void getDataFromIdentitePivotTest() {
		IdentitePivot identitePivot = testDataFactory.defaultIdentitePivot();

		Map<String, String> mapIdentitePivot = DemarcheUtils.getDataFromIdentitePivot(identitePivot);

		assertEquals(identitePivot.getFamilyName(), mapIdentitePivot.get("nom"));
		assertEquals(identitePivot.getGivenName(), mapIdentitePivot.get("prenoms"));
		assertEquals(identitePivot.getBirthdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), mapIdentitePivot.get("dateNaissance"));
		assertEquals(identitePivot.getBirthcountry(), mapIdentitePivot.get("paysNaissance"));
		assertEquals(identitePivot.getBirthplace(), mapIdentitePivot.get("villeNaissance"));
		assertEquals(identitePivot.getPhoneNumber(), mapIdentitePivot.get("telephone"));
		assertEquals(identitePivot.getPreferredUsername(), mapIdentitePivot.get("nomUsage"));


		assertEquals(DemarcheUtils.getAdresseCompleteFromAdresse(identitePivot.getAddress()), mapIdentitePivot.get("adressesConnues"));

		identitePivot.setAddress(null);
		mapIdentitePivot = DemarcheUtils.getDataFromIdentitePivot(identitePivot);

		assertNull(mapIdentitePivot.get("adressesConnues"));
	}

	@Test
	public void updateDataFromDeclarationTest() {
		IdentitePivot identitePivot = testDataFactory.defaultIdentitePivot();
		Map<String, String> mapDonnees = DemarcheUtils.getDataFromIdentitePivot(identitePivot);

		Declaration declaration = testDataFactory.defaultDeclaration(identitePivot);
		DemarcheUtils.updateDataFromDeclaration(mapDonnees, declaration);

		assertEquals(Objects.toString(declaration.getRevenuImposable()), mapDonnees.get("revenuImposable"));
		assertEquals(Objects.toString(declaration.getRevenuFiscalReference()), mapDonnees.get("revenuFiscalReference"));
		assertEquals(Objects.toString(declaration.getNombrePersonnesCharge()), mapDonnees.get("nombrePersonnesCharge"));
		assertEquals(Objects.toString(declaration.getMontantImpot()), mapDonnees.get("montantImpot"));
		assertEquals(""+declaration.getNombreParts().intValue(), mapDonnees.get("nombrePart"));

		assertEquals(StringUtils.joinWith("|",
				DemarcheUtils.getAdresseCompleteFromAdresse(identitePivot.getAddress()),
				declaration.getFoyerFiscal().getAdresse()), mapDonnees.get("adressesConnues"));
	}

	@Test
	public void updateDataFromDeclarationWithoutExistingAdresseTest() {
		IdentitePivot identitePivot = testDataFactory.defaultIdentitePivot();
		Map<String, String> mapDonnees = DemarcheUtils.getDataFromIdentitePivot(identitePivot);
		mapDonnees.remove("adressesConnues");

		Declaration declaration = testDataFactory.defaultDeclaration(identitePivot);
		DemarcheUtils.updateDataFromDeclaration(mapDonnees, declaration);

		assertEquals(declaration.getFoyerFiscal().getAdresse(), mapDonnees.get("adressesConnues"));
	}

	@Test
	public void updateDataFromDeclarationWithSameAdresseTest() {
		IdentitePivot identitePivot = testDataFactory.defaultIdentitePivot();
		Map<String, String> mapDonnees = DemarcheUtils.getDataFromIdentitePivot(identitePivot);

		Declaration declaration = testDataFactory.defaultDeclaration(identitePivot);
		declaration.getFoyerFiscal().setAdresse(DemarcheUtils.getAdresseCompleteFromAdresse(identitePivot.getAddress()));
		DemarcheUtils.updateDataFromDeclaration(mapDonnees, declaration);

		assertEquals(DemarcheUtils.getAdresseCompleteFromAdresse(identitePivot.getAddress()), mapDonnees.get("adressesConnues"));
		assertFalse(mapDonnees.get("adressesConnues").contains("|"));
	}



	@Test
	public void updateDataForListValuesTest() {
		IdentitePivot identitePivot = testDataFactory.defaultIdentitePivot();
		Map<String, String> mapDonnees = DemarcheUtils.getDataFromIdentitePivot(identitePivot);

		Declaration declaration = testDataFactory.defaultDeclaration(identitePivot);
		DemarcheUtils.updateDataFromDeclaration(mapDonnees, declaration);

		mapDonnees.put("vehicules", "[" +
				"{\"id\": \"vehicule_1\",\"immatriculation\": \"AA-123-AA\",\"modele\": \"Kia Soul\",\"electrique\": false}," +
				"{\"id\": \"vehicule_2\",\"immatriculation\": \"BB-456-BB\",\"modele\": \"Peugeot 208\",\"electrique\": false}," +
				"{\"id\": \"vehicule_3\",\"immatriculation\": \"CC-789-CC\",\"modele\": \"Opel Corsa\",\"electrique\": true}" +
				"]");

		// Toutes les listes présentes au moins 2 valeurs, pas de changement sur la Map
		DemarcheUtils.updateDataForListValues(mapDonnees);

		assertFalse(mapDonnees.containsKey("adresseComplete"));
		assertFalse(mapDonnees.containsKey("vehiculeSelectionne"));

		// Les listes contiennent une seule valeur, elles sont ajoutés en tant que valeur définitive
		mapDonnees.put("vehicules", "[" +
				"{\"id\": \"vehicule_1\",\"immatriculation\": \"AA-123-AA\",\"modele\": \"Kia Soul\",\"electrique\": false}" +
				"]");
		mapDonnees.put("adressesConnues", faker.address().fullAddress());

		DemarcheUtils.updateDataForListValues(mapDonnees);

		assertTrue(mapDonnees.containsKey("adresseComplete"));
		assertEquals(mapDonnees.get("adressesConnues"), mapDonnees.get("adresseComplete") );
		assertTrue(mapDonnees.containsKey("vehiculeSelectionne"));

		CarteStationnement carteStationnement = assertDoesNotThrow( () -> new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
				.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
				.readValue(mapDonnees.get("vehiculeSelectionne"), CarteStationnement.class));
		assertEquals("AA-123-AA",carteStationnement.getImmatriculation());
		assertEquals("Kia Soul",carteStationnement.getModele());
		assertEquals(false,carteStationnement.isElectrique());
	}

}
