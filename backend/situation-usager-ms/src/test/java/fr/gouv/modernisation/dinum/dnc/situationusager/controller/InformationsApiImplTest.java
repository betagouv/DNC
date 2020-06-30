package fr.gouv.modernisation.dinum.dnc.situationusager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import fr.gouv.modernisation.dinum.dnc.common.filter.ApiKeyFilter;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.SessionInterceptor;
import fr.gouv.modernisation.dinum.dnc.situationusager.constant.ResponseFormatsForTestApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.factory.TestDataFactory;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemandeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.InfosUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.InformationsApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.JustificatifApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.JustificatifsApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.JustificatifService;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@TestPropertySource(locations = {"classpath:application-test.yml" }, properties = {
		"currentTest=InformationsApiImplTest", "api.dnc.partenaire-ms.url=http://localhost:18081",
		"spring.redis.port=6881", "server.port=18184", "management.server.port=18184"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
public class InformationsApiImplTest {

	/**
	 * Wiremock pour Franceconnect-ms, les API Franceconnecté et les API non franceconneté
	 */
	private static WireMockServer wireMockServer;

	@Value("${api.dnc.franceconnect-ms.apiKey}")
	private String franceconnectMsApiKey;

	@Autowired
	private JustificatifService justificatifService;

	@BeforeAll
	public static void init(@Autowired Environment environment) {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Integer.valueOf(environment.getProperty("server.port"));

		// Wiremock pour Franceconnect
		if (wireMockServer == null) {
			wireMockServer = new WireMockServer(
					WireMockConfiguration.options().port(18081).notifier(new ConsoleNotifier(true)));
		}
		wireMockServer.start();
		configureFor(18081);
	}

	@BeforeEach
	public void setupTest(@Autowired Environment environment) {
		// Reset du serveur Wiremock
		wireMockServer.resetAll();
	}

	@Test
	public void generationJustificatifEtSortiePDF() throws NoSuchMethodException, IOException {
		TestDataFactory testDataFactory = new TestDataFactory();
		SituationUsager situationUsager = testDataFactory.defaultSituationUsager("openid");

		for(DemarcheEnum demarcheEnum : DemarcheEnum.values() ) {
			Map<String, List<DonneeUsager>> mapDonneeUsager = testDataFactory.getMapDonneeUsagerForDemarche(demarcheEnum, situationUsager);

			// Récupération des données
			byte[] report = justificatifService.generateJustificatifJasperWithDonneeUsager(DemarcheUtils.getTypeJustificatifFromDemarcheEnum(demarcheEnum), mapDonneeUsager);
			assertNotNull(report);
			FileOutputStream fileOutputStream = new FileOutputStream("target/test-it-"+demarcheEnum.toString()+".pdf");
			fileOutputStream.write(report);
			fileOutputStream.flush();
			fileOutputStream.close();
		}
	}

	@Test
	public void generationDemoPDF() throws NoSuchMethodException, IOException {
		TestDataFactory testDataFactory = new TestDataFactory();
		DemarcheUsager demarcheUsager = testDataFactory.defaultDemarcheUsager(DemarcheEnum.RESTAURATION_SCOLAIRE, "13002526500013");
		demarcheUsager.getRawData().put("enfant-nomPrenom","Paul Etienne DUBOIS");
		demarcheUsager.getRawData().put("enfant-enfant-dateNaissance","25/09/1995");
		demarcheUsager.getRawData().put("adresseEnfant","BAT 3 APT 93, RUE DU CENTRE NOISY LE GRAND");
		demarcheUsager.getRawData().put("nomParent","DUBOIS");
		demarcheUsager.getRawData().put("prenomParent","Angela Claire Louise");
		demarcheUsager.getRawData().put("adresseParent","");
		demarcheUsager.getRawData().put("telephone","");
		demarcheUsager.getRawData().put("quotientFamilial","123");
		demarcheUsager.getRawData().put("numeroAllocataire","123456789");
		demarcheUsager.getRawData().put("codePostal","75000");
		demarcheUsager.getRawData().put("revenuFiscalReference","12345");


		byte[] report = justificatifService.generateJustificatifJasper(demarcheUsager);
		assertNotNull(report);
		FileOutputStream fileOutputStream = new FileOutputStream("target/test-it-valeur-demo.pdf");
		fileOutputStream.write(report);
		fileOutputStream.flush();
		fileOutputStream.close();
	}

	@Test
	public void accessInfosNotFoundSessionTest() throws NoSuchMethodException{
		String idSession = UUID.randomUUID().toString();
		String idUsager = UUID.randomUUID().toString();
		String currentToken = "b1108267-e5f2-462a-a17c-a2b8ba1af352";


		stubForFranceconnectMs(idSession, idUsager, currentToken);

		// Tests des Infos CNAF
		// Récupération de l'URI de la méthode testée
		String requestInfoCnafUri = InformationsApi.class.getMethod("getInfoCNAF", String.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseInfoCnafGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.param("numeroAllocataire","0000354")
				.param("codePostal","99148")
				.when()
				.get(requestInfoCnafUri);

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseInfoCnafGet.getStatusCode());

		// Tests des Infos DGFIP
		// Récupération de l'URI de la méthode testée
		String requestInfoDgfipUri = InformationsApi.class.getMethod("getInfoDGFIP", String.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseInfoDgfipGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.param("numeroFiscal","1902599999001")
				.param("referenceAvisFiscal","1902599999001")
				.when()
				.get(requestInfoDgfipUri);

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseInfoDgfipGet.getStatusCode());

		// Tests des Infos CNAF
		// Appel de la méthode
		responseInfoCnafGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.param("numeroAllocataire","0000354")
				.param("codePostal","99148")
				.when()
				.get(requestInfoCnafUri);

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseInfoCnafGet.getStatusCode());

		// Tests des Infos DGFIP
		// Appel de la méthode
		responseInfoDgfipGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.param("numeroFiscal","1902599999001")
				.param("referenceAvisFiscal","1902599999001")
				.when()
				.get(requestInfoDgfipUri);

		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseInfoDgfipGet.getStatusCode());

		// Tests des infos de Justificatifs
		// Récupération de l'URI de la méthode testée
		String requestInfoJustificatifUri = InformationsApi.class.getMethod("getInfosJustificatifs", TypeJustificatif.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode (sans paramètre)
		Response responseInfoJustificatifGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.when()
				.get(requestInfoJustificatifUri);
		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseInfoJustificatifGet.getStatusCode());

		// Tests de la génération de Justificatifs (Identite FranceConnect)
		// Récupération de l'URI de la méthode testée
		String requestGenerationJustificatifUri = JustificatifApi.class.getMethod("createJustificatif", DemandeJustificatif.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGenerationJustificatifPost = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(new DemandeJustificatif().type(TypeJustificatif.IDENTITE_FRANCECONNECT))
				.when()
				.post(requestGenerationJustificatifUri);
		Assertions.assertEquals(HttpStatus.NOT_FOUND.value(), responseGenerationJustificatifPost.getStatusCode());

	}

	@Test
	public void accessToutesInfosUsagerTest() throws NoSuchMethodException{
		String idSession = UUID.randomUUID().toString();
		String idUsager = UUID.randomUUID().toString();
		String currentToken = "b1108267-e5f2-462a-a17c-a2b8ba1af352";


		stubForFranceconnectMs(idSession, idUsager, currentToken);
		stubForCnam();
		stubForCnaf();
		stubForDgfip();


		// Tests des Infos Franceconnectées
		// Récupération de l'URI de la méthode testée
		String requestUri = InformationsApi.class.getMethod("getInfosUsager")
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseGet.getStatusCode());
		InfosUsager infosUsagerAvecDonneesFranceconnectees = responseGet.getBody().as(InfosUsager.class);

		Assertions.assertNotNull(infosUsagerAvecDonneesFranceconnectees);
		Assertions.assertNotNull(infosUsagerAvecDonneesFranceconnectees.getBeneficiareCnam());

		// Tests des Infos CNAF
		// Récupération de l'URI de la méthode testée
		String requestInfoCnafUri = InformationsApi.class.getMethod("getInfoCNAF", String.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseInfoCnafGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.param("numeroAllocataire","0000354")
				.param("codePostal","99148")
				.when()
				.get(requestInfoCnafUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoCnafGet.getStatusCode());
		Famille donneesCnaf = responseInfoCnafGet.getBody().as(Famille.class);

		Assertions.assertNotNull(donneesCnaf);
		Assertions.assertNotNull(donneesCnaf.getAllocataires().get(0).getDateDeNaissance());

		// Tests des Infos DGFIP
		// Récupération de l'URI de la méthode testée
		String requestInfoDgfipUri = InformationsApi.class.getMethod("getInfoDGFIP", String.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseInfoDgfipGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.param("numeroFiscal","1902599999001")
				.param("referenceAvisFiscal","1902599999001")
				.when()
				.get(requestInfoDgfipUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoDgfipGet.getStatusCode());
		Declaration donneesDgfip = responseInfoDgfipGet.getBody().as(Declaration.class);

		Assertions.assertNotNull(donneesDgfip);
		Assertions.assertNotNull(donneesDgfip.getDateRecouvrement());


		// Vérification que les données ne sont pas doublement requêté en cas d'existance préalable pour la session
		wireMockServer.resetAll();

		// On remet le stub pour franceconnect
		stubForFranceconnectMs(idSession, idUsager, currentToken);

		// Tests des Infos Franceconnectées
		// Appel de la méthode
		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseGet.getStatusCode());
		InfosUsager infosUsagerAvecDonneesFranceconnectees2 = responseGet.getBody().as(InfosUsager.class);

		Assertions.assertEquals(infosUsagerAvecDonneesFranceconnectees, infosUsagerAvecDonneesFranceconnectees2);

		// Tests des Infos CNAF
		// Appel de la méthode
		responseInfoCnafGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.param("numeroAllocataire","0000354")
				.param("codePostal","99148")
				.when()
				.get(requestInfoCnafUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoCnafGet.getStatusCode());
		Famille donneesCnaf2 = responseInfoCnafGet.getBody().as(Famille.class);

		Assertions.assertEquals(donneesCnaf, donneesCnaf2);

		// Tests des Infos DGFIP
		// Appel de la méthode
		responseInfoDgfipGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsApiKey)
				.param("numeroFiscal","1902599999001")
				.param("referenceAvisFiscal","1902599999001")
				.when()
				.get(requestInfoDgfipUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoDgfipGet.getStatusCode());
		Declaration donneesDgfip2 = responseInfoDgfipGet.getBody().as(Declaration.class);

		Assertions.assertEquals(donneesDgfip, donneesDgfip2);

		// Tests des infos de Justificatifs
		// Récupération de l'URI de la méthode testée
		String requestInfoJustificatifUri = InformationsApi.class.getMethod("getInfosJustificatifs", TypeJustificatif.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode (sans paramètre)
		Response responseInfoJustificatifGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.when()
				.get(requestInfoJustificatifUri);
		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoJustificatifGet.getStatusCode());

		TypeRef<Map<String,List<DonneeUsager>>> typeRef = new TypeRef<>() {};
		Map<String, List<DonneeUsager>> mapDonneeUsager = responseInfoJustificatifGet.getBody().as(typeRef);

		Assertions.assertFalse(mapDonneeUsager.isEmpty());
		Assertions.assertEquals(5, mapDonneeUsager.size());
		// A part Selection, toutes les maps sont remplis
		Assertions.assertFalse(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_FRANCECONNECT).isEmpty());
		Assertions.assertFalse(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_CNAM).isEmpty());
		Assertions.assertFalse(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_DGFIP).isEmpty());
		Assertions.assertFalse(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_ANTS).isEmpty());
		Assertions.assertTrue(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).isEmpty());

		// Appel de la méthode (avec paramètre)
		Response responseInfoJustificatifFiltreGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.queryParam("typeJustificatif", TypeJustificatif.IDENTITE_FRANCECONNECT)
				.when()
				.get(requestInfoJustificatifUri);
		Assertions.assertEquals(HttpStatus.OK.value(), responseInfoJustificatifFiltreGet.getStatusCode());

		Map<String, List<DonneeUsager>> mapDonneeUsagerFiltree = responseInfoJustificatifFiltreGet.getBody().as(typeRef);

		Assertions.assertFalse(mapDonneeUsagerFiltree.isEmpty());
		Assertions.assertEquals(5, mapDonneeUsagerFiltree.size());
		// La Map FranceConnect est rempli
		Assertions.assertFalse(mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_FRANCECONNECT).isEmpty());
		// Les Maps DGFIP et CNAM sont rempli avec un champ, l'adresse complète
		Assertions.assertFalse(mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_CNAM).isEmpty());
		Assertions.assertEquals(1, mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_CNAM).size());
		Assertions.assertFalse(mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_DGFIP).isEmpty());
		Assertions.assertEquals(1, mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_DGFIP).size());
		// Les autres Map sont vides
		Assertions.assertTrue(mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_ANTS).isEmpty());
		Assertions.assertTrue(mapDonneeUsagerFiltree.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).isEmpty());

		// Tests de la génération de Justificatifs (Identite FranceConnect)
		// Récupération de l'URI de la méthode testée
		String requestGenerationJustificatifUri = JustificatifApi.class.getMethod("createJustificatif", DemandeJustificatif.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGenerationJustificatifPost = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(new DemandeJustificatif().type(TypeJustificatif.IDENTITE_FRANCECONNECT))
				.when()
				.post(requestGenerationJustificatifUri);
		Assertions.assertEquals(HttpStatus.OK.value(), responseGenerationJustificatifPost.getStatusCode());

		Justificatif justificatifIdentite = responseGenerationJustificatifPost.getBody().as(Justificatif.class);
		Assertions.assertNotNull(justificatifIdentite);
		Assertions.assertNull(justificatifIdentite.getIdPartenaire());

		// Tests de la génération de Justificatifs (Carte de stationnement)
		// Préparation de la sélection
		DonneeUsager vehicule = mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_ANTS).get(0).getListeDonnees().get(0);
		mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).addAll(vehicule.getListeDonnees());

		DemandeJustificatif demandeJustificatif = new DemandeJustificatif()
				.type(TypeJustificatif.CARTE_STATIONNEMENT)
				.donneesSelectionnees(mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION));

		// Appel de la méthode
		responseGenerationJustificatifPost = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(demandeJustificatif)
				.when()
				.post(requestGenerationJustificatifUri);
		Assertions.assertEquals(HttpStatus.OK.value(), responseGenerationJustificatifPost.getStatusCode());

		Justificatif justificatifCarteStaionnement = responseGenerationJustificatifPost.getBody().as(Justificatif.class);
		Assertions.assertNotNull(justificatifCarteStaionnement);
		Assertions.assertNull(justificatifCarteStaionnement.getIdPartenaire());


		// Comparaison entre les justificatifs
		Assertions.assertNotEquals(justificatifIdentite.getId(), justificatifCarteStaionnement.getId());
		Assertions.assertNotEquals(justificatifIdentite.getSize(), justificatifCarteStaionnement.getSize());
		Assertions.assertNotEquals(justificatifIdentite.getContenu(), justificatifCarteStaionnement.getContenu());
	}

	/**
	 * Stub pour la réponse du Franceconnect-MS
	 * @throws JsonProcessingException
	 */
	private void stubForFranceconnectMs(String idSession, String idUsager, String currentToken) {
		stubFor(WireMock.get(urlPathMatching("/fc/user-session/(.+)"))

				.willReturn(
						aResponse()
								.withBody(
										String.format(ResponseFormatsForTestApi.JSON_IDENTITE_PIVOT_FORMAT,
												StringUtils.defaultIfBlank(idSession, UUID.randomUUID().toString()),
												StringUtils.defaultIfBlank(idUsager, UUID.randomUUID().toString()),
												StringUtils.defaultIfBlank(currentToken, UUID.randomUUID().toString()))
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}

	/**
	 * Stub pour la réponse du Franceconnect-MS
	 * @throws JsonProcessingException
	 */
	private void stubForFranceconnectMsSessionNotFound(String idSession, String idUsager, String currentToken) {
		stubFor(WireMock.get(urlPathMatching("/fc/user-session/(.+)"))

				.willReturn(
						aResponse()
								.withStatus(404)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}

	/**
	 * Stub pour la réponse de la CNAM
	 * @throws JsonProcessingException
	 */
	private void stubForCnam() {
		stubFor(WireMock.get(urlPathMatching("/me"))
				.willReturn(
						aResponse()
								.withBody(
										ResponseFormatsForTestApi.CNAM_RESPONSE_FORMAT
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}

	/**
	 * Stub pour la réponse de la DGFIP
	 * @throws JsonProcessingException
	 */
	private void stubForDgfip() {
		stubFor(WireMock.get(urlPathMatching("/impots/svair"))
				.willReturn(
						aResponse()
								.withBody(
										ResponseFormatsForTestApi.DGFIP_IMPOT_SVAIR_RESPONSE_FORMAT
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}

	/**
	 * Stub pour la réponse de la CNAF
	 * @throws JsonProcessingException
	 */
	private void stubForCnaf() {
		stubFor(WireMock.get(urlPathMatching("/caf/famille"))
				.willReturn(
						aResponse()
								.withBody(
										ResponseFormatsForTestApi.CNAF_RESPONSE_FORMAT
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}
}
