package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import com.github.javafaker.Faker;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientCnaf;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientDgfip;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientFCCnam;
import fr.gouv.modernisation.dinum.dnc.situationusager.factory.TestDataFactory;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests pour {@link FetchDataService}
 */
public class FetchDataServiceTest {

	private final Faker faker = new Faker(new Locale("fr-FR"));

	@InjectMocks
	private FetchDataService fetchDataService;

	private TestDataFactory testDataFactory = new TestDataFactory();


	@Mock
	private SituationUsagerService situationUsagerService;

	/**
	 * Client vers API CNAM (Franceconenctée)
	 */
	@Mock
	private RestClientFCCnam restClientFCCnam;

	/**
	 * Client sur API CNAF (api.particuliers)
	 */
	@Mock
	private RestClientCnaf restClientCnaf;

	/**
	 * Client sur API DGFIP (api.particuliers)
	 */
	@Mock
	private RestClientDgfip restClientDgfip;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getDonnneesFranceconnecteesForStandAloneTest() {
		SituationUsager situationUsager = testDataFactory.defaultSituationUsager("test");
		Beneficiaire beneficiaire = testDataFactory.defaultBeneficiaire(situationUsager);

		Mockito.when(restClientFCCnam.getBeneficiaire(ArgumentMatchers.any()))
				.thenReturn(
						CompletableFuture.completedFuture(beneficiaire));

		fetchDataService.getDonnneesFranceconnecteesForStandAlone(situationUsager);

		assertNotNull(situationUsager.getBeneficiaireCnam());
		assertEquals(beneficiaire, situationUsager.getBeneficiaireCnam());

	}

	@Test
	public void getDonneesForDemarcheCarteStationnementTest() {
		String siretPartenaire= "13002526500013";
		Map<String, String> secretsSupplementaires = new HashMap<>();
		secretsSupplementaires.put("siretPartenaire", siretPartenaire);
		secretsSupplementaires.put("siret", siretPartenaire);
		secretsSupplementaires.put("raisonSociale", "DINUM");

		String credentialsPartenaire = Base64.getEncoder().encodeToString("username:password".getBytes(StandardCharsets.UTF_8));
		SessionUsager sessionUsager = testDataFactory.defaultSessionUsager("test");
		// Création de la démarche
		Map<String, String> rawData = fetchDataService.getDonneesForDemarche(DemarcheEnum.CARTE_STATIONNEMENT, sessionUsager, secretsSupplementaires);

		assertNotNull(rawData);
		assertNotNull(rawData.get("vehicules"));
		assertNotNull(rawData.get("siret"));
		assertNotNull(rawData.get("raisonSociale"));
	}

	@Test
	public void getDonneesForDemarcheRestaurationScolaireTest() {
		String siretPartenaire= "21972209700017";
		Map<String, String> secretsSupplementaires = new HashMap<>();
		secretsSupplementaires.put("siretPartenaire", siretPartenaire);
		secretsSupplementaires.put("codePostal","97200");
		secretsSupplementaires.put("numeroAllocataire", faker.number().digits(7) );
		secretsSupplementaires.put("numeroFiscal",faker.number().digits(13) );
		secretsSupplementaires.put("referenceAvisFiscal", faker.number().digits(13) );

		SessionUsager sessionUsager = testDataFactory.defaultSessionUsager("test");

		Mockito.when(restClientCnaf.getFamille(secretsSupplementaires.get("numeroAllocataire"),
				secretsSupplementaires.get("codePostal"))
		).thenReturn(testDataFactory.defaultFamille(sessionUsager));

		Mockito.when(restClientDgfip.getDeclaration(secretsSupplementaires.get("numeroFiscal"),
				secretsSupplementaires.get("referenceAvisFiscal"))
		).thenReturn(testDataFactory.defaultDeclaration(sessionUsager.getIdentitePivot()));

		// Création de la démarche
		Map<String, String> rawData = fetchDataService.getDonneesForDemarche(DemarcheEnum.RESTAURATION_SCOLAIRE, sessionUsager, secretsSupplementaires);

		assertNotNull(rawData);
		assertNotNull(rawData.get("revenuImposable"));
		assertNotNull(rawData.get("revenuFiscalReference"));
		assertNotNull(rawData.get("montantImpot"));
		assertNotNull(rawData.get("nombrePart"));
		assertNotNull(rawData.get("quotientFamilial"));
		assertNotNull(rawData.get("nombreEnfantACharge"));
		assertNotNull(rawData.get("enfants"));
		assertNotNull(rawData.get("allocataires"));
	}
}
