package fr.gouv.modernisation.dinum.dnc.demarche.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import fr.gouv.modernisation.dinum.dnc.common.constante.DemarcheQueueConstantes;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.CorrelationIdInterceptor;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.SessionInterceptor;
import fr.gouv.modernisation.dinum.dnc.demarche.data.enumeration.Statut;
import fr.gouv.modernisation.dinum.dnc.demarche.data.repository.DemarcheRepository;
import fr.gouv.modernisation.dinum.dnc.demarche.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.demarche.generated.api.server.DemarcheApi;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@TestPropertySource(locations = {"classpath:application-test.yml" }, properties = {"currentTest=DemarcheControllerTest" })
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
public class DemarcheControllerTest {

	/**
	 * Format pour générer une réponse de Franceconnect-MS
	 */
	private static final String JSON_IDENTITE_PIVOT_FORMAT = "{ \"user_id\": \"%s\", \"idSession\": \"%s\", \"identitePivot\": { \"given_name\": \"Jill\", \"family_name\": \"Valentine\" } }";

	/**
	 * Wiremock pour Franceconnect-ms
	 */
	private static WireMockServer wireMockServer;

	/**
	 * {@link JmsTemplate} pour les tests de la lecture de la fil JMS
	 */
	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * {@link DemarcheRepository} poru les {@link Demarche}
	 */
	@Autowired
	private DemarcheRepository demarcheRepository;

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
	@DisplayName("Test de getDemarche - Aucun ID de session dans le Header")
	public void getDemarcheBadRequestTest() throws NoSuchMethodException {
		// Récupération de l'URI de la méthode testée
		String requestUri = DemarcheApi.class.getMethod("getDemarche", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("id","20")
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		responseGet.then().statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	@DisplayName("Test de getDemarche - session inexistante")
	public void getDemarcheSessionNotFoundTest() throws NoSuchMethodException {
		// Récupération de l'URI de la méthode testée
		String requestUri = DemarcheApi.class.getMethod("getDemarche", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		stubFor(WireMock.get(urlPathMatching("/fc/user-session/(.+)"))

				.willReturn(
						aResponse()
								.withStatus(404)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);

		// Appel de la méthode
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("id","20")
				.header(SessionInterceptor.HEADER_NAME, UUID.randomUUID())
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		responseGet.then().statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	@DisplayName("Test de getDemarche")
	public void getDemarcheTest() throws NoSuchMethodException, JsonProcessingException {
		stubForFranceconnectMs();
		String idSession = UUID.randomUUID().toString();

		// Récupération de l'URI de la méthode testée
		String requestUri = DemarcheApi.class.getMethod("getDemarche", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("id","20")
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		responseGet.then().statusCode(HttpStatus.NOT_FOUND.value());

		String requestCreateUri = DemarcheApi.class.getMethod("createDemarche", Demarche.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Création d'un objet Démarche avec un statut incompatible
		Demarche demarche = new Demarche()
				.libelle("Test de sauvegarde de démarche")
				.partenaireID("partenaireId")
				.statut("OPEN");
		// Appel de la méthode
		Response responseCreate = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(demarche)
				.when()
				.put(requestCreateUri);

		// Vérification du code de réponses
		responseCreate.then().statusCode(HttpStatus.CREATED.value());
		//Récupération de l'ID de la démarche
		String idDemarche = responseCreate.getBody().asString();

		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.pathParam("id",idDemarche)
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		responseGet.then().statusCode(HttpStatus.OK.value());
		Demarche demarcheRecue = responseGet.as(Demarche.class);

		Assertions.assertEquals(demarche.getLibelle(), demarcheRecue.getLibelle());
		Assertions.assertEquals(demarche.getPartenaireID(), demarcheRecue.getPartenaireID());
		// le Statut correspond au statut de la source des données de la démarche
		Assertions.assertEquals(demarche.getStatut(), demarcheRecue.getStatut());
	}

	@Test
	@DisplayName("Test de updateDemarche")
	public void updateDemarcheTest() throws NoSuchMethodException, JsonProcessingException {
		stubForFranceconnectMs();
		String idSession = UUID.randomUUID().toString();

		// Création d'un objet Démarche avec un statut compatible
		Demarche demarche = new Demarche()
				.libelle("Test de sauvegarde de démarche")
				.partenaireID("partenaireId")
				.statut("EN_COURS_TRAITEMENT");

		// récupération de l'URI pour préparer le JDD
		String requestCreateUri = DemarcheApi.class.getMethod("createDemarche", Demarche.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseCreate = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(demarche)
				.when()
				.put(requestCreateUri);

		// Vérification du code de réponses
		responseCreate.then().statusCode(HttpStatus.CREATED.value());
		//Récupération de l'ID de la démarche
		String idDemarche = responseCreate.getBody().asString();

		// Nouvelle session pour le même utilisateur
		idSession = UUID.randomUUID().toString();

		// Récupération de l'URI de la méthode testée
		String requestUpdateUri = DemarcheApi.class.getMethod("updateDemarche", Demarche.class)
				.getAnnotation(RequestMapping.class).value()[0];

		demarche.setId(idDemarche);
		demarche.setStatut("AUTRE_MISE_A_JOUR");
		Response responseUpdate = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.body(demarche)
				.when()
				.post(requestUpdateUri);

		// Vérification du code de réponses
		responseUpdate.then().statusCode(HttpStatus.OK.value());

		// Vérification de l'état de la démarche
		String requestGetUri = DemarcheApi.class.getMethod("getDemarche", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(SessionInterceptor.HEADER_NAME, idSession)
				.pathParam("id",idDemarche)
				.when()
				.get(requestGetUri);

		Demarche demarcheCourante = responseGet.as(Demarche.class);

		Assertions.assertEquals(demarche.getLibelle(), demarcheCourante.getLibelle());
		Assertions.assertEquals(demarche.getPartenaireID(), demarcheCourante.getPartenaireID());
		// Le Statut est différent du statut d'origine
		Assertions.assertNotEquals("EN_COURS_TRAITEMENT", demarcheCourante.getStatut());
		// Le statut correspond au statut envoyé par le fournisseur de données
		Assertions.assertEquals("AUTRE_MISE_A_JOUR", demarcheCourante.getStatut());
	}

	@DisplayName("Test du lecteur de la fil JMS Demarche-Queue - Cas d'erreurs")
	@Test
	public void demarcheQueueErreursTest() {
		// Nombre de démarche avant le test
		long originalCount = demarcheRepository.count();


		Map<String, String> mapDemarches = new HashMap<>();

		// Map vide
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		// Map de Création sans les clés nécessaires
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_CREATION);
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		await().atMost(5, TimeUnit.SECONDS);

		long newCount = demarcheRepository.count();
		Assertions.assertEquals(originalCount,newCount);

		// Map de mise à jour sans les clés nécéssaires
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_MISE_A_JOUR);
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		await().atMost(5, TimeUnit.SECONDS);

		newCount = demarcheRepository.count();
		Assertions.assertEquals(originalCount,newCount);
	}

	@DisplayName("Test du lecteur de la fil JMS Demarche-Queue - Mode Création")
	@Test
	public void demarcheQueueCreationTest() throws InterruptedException {
		// Nombre de démarche avant le test
		long originalCount = demarcheRepository.count();

		String idDemarche = UUID.randomUUID().toString();
		String idUsager = UUID.randomUUID().toString();

		// Envoie des données dans la Fil
		Map<String, String> mapDemarches = new HashMap<>();
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_CREATION);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE, idDemarche);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER, idUsager);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE, "13002526500013");
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE, "CARTE_STATIONNEMENT");

		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		await().atMost(60, TimeUnit.SECONDS).until(checkNewDemarcheCreated(originalCount));

		long newCount = demarcheRepository.count();

		Assertions.assertNotEquals(originalCount, newCount);
		Optional<fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche> optional = demarcheRepository.findAll().stream().max(Comparator.comparing(fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche::getId));
		Assertions.assertTrue(optional.isPresent());
		Assertions.assertEquals(idUsager, optional.get().getIdUsager());
		Assertions.assertEquals(idDemarche, optional.get().getIdDemarche());
		Assertions.assertEquals("CARTE_STATIONNEMENT", optional.get().getCodeDemarche());
	}

	@DisplayName("Test du lecteur de la fil JMS Demarche-Queue - Mode Mise à jour")
	@Test
	public void demarcheQueueMiseAjourTest() throws InterruptedException {
		// Nombre de démarche avant le test
		long originalCount = demarcheRepository.count();

		String idDemarche = UUID.randomUUID().toString();
		String idUsager = UUID.randomUUID().toString();

		// Envoie des données dans la Fil
		Map<String, String> mapDemarches = new HashMap<>();
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_CREATION);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE, idDemarche);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER, idUsager);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE, "13002526500013");
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE, "CARTE_STATIONNEMENT");

		// Message de création
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		await().atMost(60, TimeUnit.SECONDS).until(checkNewDemarcheCreated(originalCount));

		// Check de création
		long newCount = demarcheRepository.count();
		Assertions.assertNotEquals(originalCount, newCount);
		Optional<fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche> optional = demarcheRepository.findAll().stream().max(Comparator.comparing(fr.gouv.modernisation.dinum.dnc.demarche.data.entity.Demarche::getId));
		Assertions.assertTrue(optional.isPresent());
		Long idEntityDemarche = optional.get().getId();
		Integer oldVersion = optional.get().getVersion();

		// Message de mise à jour de statut
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION, DemarcheQueueConstantes.VALUE_OPERATION_MISE_A_JOUR);
		mapDemarches.put(DemarcheQueueConstantes.MAP_MESSAGE_KEY_STATUT, "Traitée");
		mapDemarches.remove(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE);
		mapDemarches.remove(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER);
		jmsTemplate.convertAndSend(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME, mapDemarches,
				message -> {
					message.setJMSCorrelationID(UUID.randomUUID().toString());
					return message;
				});

		// Attente de la mise à jour
		await().atMost(60, TimeUnit.SECONDS).until(checkDemarcheUpdated(oldVersion, idEntityDemarche));

		// Check de la mise à jour
		optional = demarcheRepository.findById(idEntityDemarche);

		Assertions.assertTrue(optional.isPresent());
		Assertions.assertNotEquals(oldVersion, optional.get().getVersion());
		Assertions.assertNotNull(optional.get().getDateMiseAJour());
		Assertions.assertEquals("Traitée", optional.get().getStatut());
	}

	private Callable<Boolean> checkNewDemarcheCreated(long originalCount) {
		return new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return demarcheRepository.count() != originalCount;
			}
		};
	}

	private Callable<Boolean> checkDemarcheUpdated(Integer oldVersion, Long idDemarche) {
		return new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return demarcheRepository.findById(idDemarche).get().getVersion() != oldVersion;
			}
		};
	}

	/**
	 * Stub pour la réponse du Franceconnect-MS
	 * @throws JsonProcessingException
	 */
	private void stubForFranceconnectMs() throws JsonProcessingException {
		stubFor(WireMock.get(urlPathMatching("/fc/user-session/(.+)"))

				.willReturn(
						aResponse()
								.withBody(
										String.format(JSON_IDENTITE_PIVOT_FORMAT, UUID.randomUUID().toString(), UUID.randomUUID().toString())
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}
}
