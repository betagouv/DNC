package fr.gouv.modernisation.dinum.dnc.situationusager.controller.partenaire;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import fr.gouv.modernisation.dinum.dnc.common.constante.DemarcheQueueConstantes;
import fr.gouv.modernisation.dinum.dnc.common.filter.ApiKeyFilter;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.constant.ResponseFormatsForTestApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.factory.TestDataFactory;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.server.partenaire.DemarcheApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.DemarcheUsagerService;
import fr.gouv.modernisation.dinum.dnc.situationusager.service.FetchDataService;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.logging.Log;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@TestPropertySource(locations = {"classpath:application-test.yml" }, properties = {
		"currentTest=DemarcheApiImplTest", "api.dnc.partenaire-ms.url=http://localhost:18181",
		"spring.redis.port=6882", "server.port=18185", "management.server.port=18185"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
public class DemarcheApiImplTest {

	/**
	 * Logger {@link Log}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DemarcheApiImplTest.class);

	/**
	 * Wiremock pour Franceconnect-ms,Partenaire-MS, les API Franceconnecté et les API non franceconneté
	 */
	private static WireMockServer wireMockServer;

	/**
	 * API Key pour les appels à Partenaire-MS
	 */
	@Value("${api.dnc.partenaire-ms.apiKey}")
	private String partenaireMsApiKey;

	/**
	 * Service des DemarcheUsager
	 */
	@Autowired
	private DemarcheUsagerService demarcheUsagerService;

	/**
	 * Service de récupérations des données
	 */
	@Autowired
	private FetchDataService fetchDataService;

	private TestDataFactory testDataFactory = new TestDataFactory();

	private Map<String,String> resultMessage = null;

	@Autowired
	private JmsTemplate jmsTemplate;

	@BeforeAll
	public static void init(@Autowired Environment environment) throws Exception,Throwable {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Integer.valueOf(environment.getProperty("server.port"));

		// Wiremock pour Franceconnect
		if (wireMockServer == null) {
			wireMockServer = new WireMockServer(
					WireMockConfiguration.options().port(18181).notifier(new ConsoleNotifier(true)));
		}
		wireMockServer.start();
		configureFor(18181);
	}

	@BeforeEach
	public void setupTest(@Autowired Environment environment) {
		// Reset du serveur Wiremock
		wireMockServer.resetAll();

		resultMessage = null;
	}

	@Test
	public void getDemarcheTest() throws NoSuchMethodException, JMSException, JsonProcessingException {

		String siretPartenaire= "13002526500013";
		Map<String, String> secretsSupplementaires = new HashMap<>();
		secretsSupplementaires.put("siretPartenaire", siretPartenaire);
		secretsSupplementaires.put("siret", siretPartenaire);
		secretsSupplementaires.put("raisonSociale", "DINUM");

		String credentialsPartenaire = Base64.getEncoder().encodeToString("username:password".getBytes(StandardCharsets.UTF_8));
		SessionUsager sessionUsager = testDataFactory.defaultSessionUsager("test");
		// Création de la démarche
		Map<String, String> rawData = fetchDataService.getDonneesForDemarche(DemarcheEnum.CARTE_STATIONNEMENT, sessionUsager, secretsSupplementaires);
		DemarcheUsager demarcheUsager = demarcheUsagerService.saveFromRawData(sessionUsager,DemarcheEnum.CARTE_STATIONNEMENT, rawData);

		// Mise à jour de la démarche pour sélection
		Map<String, String> dataUpdate = new HashMap<>();
		dataUpdate.put("vehiculeSelectionne", "{\"id\": \"vehicule_3\",\"immatriculation\": \"CC-789-CC\",\"modele\": \"Opel Corsa\",\"electrique\": true}");
		demarcheUsager = demarcheUsagerService.updateFromRawData(demarcheUsager, dataUpdate,sessionUsager,1 );

		// Finalization de la démarche pour le Partenaire
		demarcheUsager = demarcheUsagerService.finalizeDemarche(demarcheUsager);


		stubForPartenaireMs(siretPartenaire);

		// Tests des Infos Franceconnectées
		// Récupération de l'URI de la méthode testée
		String requestUri = DemarcheApi.class.getMethod("getDemarcheData", UUID.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode - Header Authorization manquant
		Response responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, partenaireMsApiKey)
				.pathParam("demarcheToken",demarcheUsager.getId())
				.param("siretPartenaire","12345678901234")
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseGet.getStatusCode());

		// Appel de la méthode - Parameter SiretPartenaire manquant
		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, partenaireMsApiKey)
				.header("Authorization","12345678901234")
				.pathParam("demarcheToken",demarcheUsager.getId())
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseGet.getStatusCode());

		// Appel de la méthode - Header Authorization au mauvais format
		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, partenaireMsApiKey)
				.header("Authorization",credentialsPartenaire)
				.pathParam("demarcheToken",demarcheUsager.getId())
				.param("siretPartenaire","12345678901234")
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), responseGet.getStatusCode());

		// Appel de la méthode - SIRET non authorisé pour ces données
		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, partenaireMsApiKey)
				.header("Authorization", "Basic "+credentialsPartenaire)
				.pathParam("demarcheToken",demarcheUsager.getId())
				.param("siretPartenaire","12345678901234")
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), responseGet.getStatusCode());

		// Appel de la méthode - Siret du partenaire
		responseGet = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, partenaireMsApiKey)
				.header("Authorization", "Basic "+credentialsPartenaire)
				.pathParam("demarcheToken",demarcheUsager.getId())
				.param("siretPartenaire",demarcheUsager.getDemarche().getSiretPartenaire())
				.when()
				.get(requestUri);

		Assertions.assertEquals(HttpStatus.OK.value(), responseGet.getStatusCode());
		Demarche donneesDemarche = responseGet.getBody().as(Demarche.class);

		Assertions.assertNotNull(donneesDemarche);
		Assertions.assertEquals(demarcheUsager.getDemarche(), donneesDemarche);
		Assertions.assertEquals(demarcheUsager.getJustificatif().getFilename(), donneesDemarche.getJustificatif().getFilename());
		Assertions.assertEquals(demarcheUsager.getJustificatif().getContenu(), donneesDemarche.getJustificatif().getContenu());

		Message message = jmsTemplate.receive(DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME);
		LOGGER.info("Message : {}",message);
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
				.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
				;
		TypeReference<Map<String,String>> typeRef
				= new TypeReference<Map<String,String>>() {};

		Assertions.assertNotNull(message);
		Map<String, String> resultMessage = objectMapper.readValue(((ActiveMQTextMessage) message).getText(), typeRef);
		Assertions.assertEquals(DemarcheQueueConstantes.VALUE_OPERATION_CREATION, resultMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_OPERATION));
		Assertions.assertEquals(siretPartenaire, resultMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_SIRET_PARTENAIRE));
		Assertions.assertEquals(sessionUsager.getUserId(), resultMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_USAGER));
		Assertions.assertEquals(demarcheUsager.getId().toString(), resultMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_ID_DEMARCHE));
		Assertions.assertEquals(DemarcheEnum.CARTE_STATIONNEMENT.toString(), resultMessage.get(DemarcheQueueConstantes.MAP_MESSAGE_KEY_CODE_DEMARCHE));

	}

	//@JmsListener(destination = DemarcheQueueConstantes.DEMARCHE_QUEUE_NAME)
	public void messageSendTest(Message message, Session session) throws JMSException {
		LOGGER.info("Message : {}",message);
		Map<String, String> mapMessage = (Map<String, String>) message.getBody(Map.class);
	}

	/**
	 * Stub pour la réponse du Partenaire-MS
	 * @throws JsonProcessingException
	 */
	private void stubForPartenaireMs(String siret) {
		stubFor(WireMock.get(urlPathMatching("/partenaire/credentials/check"))

				.willReturn(
						aResponse()
								.withBody(
										String.format(
												ResponseFormatsForTestApi.JSON_GRANTED_ACCES_PARTENAIRE_MS_FORMAT,
												siret)
								)
								.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}


}
