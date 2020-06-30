package fr.gouv.modernisation.dinum.dnc.franceconnect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import fr.gouv.modernisation.dinum.dnc.common.client.FranceconnectMsClientDnc;
import fr.gouv.modernisation.dinum.dnc.common.filter.ApiKeyFilter;
import fr.gouv.modernisation.dinum.dnc.franceconnect.config.FranceconnectMsSecurityConfig;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.AuthorizationRequest;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.server.FcApi;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Pour le moment le test ne fonctionne qu'avec une base Redis externe démarrée.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
public class FranceConnectMSTest {

	/**
	 * Format pour générer une réponse à l'API Franceconnect qui renvoie le token
	 */
	private static final String JSON_TOKEN_FORMAT = "{ \"access_token\": \"%s\", \"token_type\": \"Bearer\", \"expires_in\": 60, \"id_token\": \"%s\" }";
	private static final String JSON_IDENTITE_PIVOT_FORMAT = "{ \"openid\": \"%s\", \"given_name\": \"Jill\", \"family_name\": \"VALENTINE\", \"gender\": \"female\", \"birthplace\": \"Raccoon City\", \"birthcountry\": \"USA\", \"email\": \"jill.valentine@stars.com\", \"preferred_username\": \"jvalentine\" }";

	private static WireMockServer wireMockServer;

	private FranceconnectMsClientDnc franceconnectMsClient;

	@Autowired
	private FranceconnectMsSecurityConfig franceconnectMsSecurityConfig;

	@BeforeAll
	public static void init(@Autowired Environment environment) {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Integer.valueOf(environment.getProperty("server.port"));

		// Wiremock pour Franceconnect
		if (wireMockServer == null) {
			wireMockServer = new WireMockServer(
					WireMockConfiguration.options().port(18080).notifier(new ConsoleNotifier(true)));
		}
		wireMockServer.start();
		configureFor(18080);
	}

	@BeforeEach
	public void setupTest(@Autowired Environment environment) {
		// Reset du serveur Wiremock
		wireMockServer.resetAll();

		franceconnectMsClient = new FranceconnectMsClientDnc(
				"http://localhost:"+Integer.valueOf(environment.getProperty("server.port")),
				franceconnectMsSecurityConfig.getApiKeys().values().stream().findAny().get()
				);
	}

	@AfterAll
	public static void stop() {
		wireMockServer.stop();
	}

	@Test
	@DisplayName("Test du client FranceconnectMsClient")
	public void clientTest() {
		Assertions.assertThrows(HttpClientErrorException.class,
				() -> franceconnectMsClient.getApi().checkSession("test"));
	}

	@Test
	@DisplayName("Test de checkSession - Mauvaise API Key")
	public void checkSessionWrongApiKeyTest() throws NoSuchMethodException {
		// Récupération de l'URI de la méthode testée
		String requestUri = FcApi.class.getMethod("checkSession", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response response = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("idSession", "testToken")
				.header(ApiKeyFilter.HEADER_NAME, "Test")
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		response.then().statusCode(HttpStatus.FORBIDDEN.value());
	}

	@Test
	@DisplayName("Test de checkSession - Session inconnue")
	public void checkSessionUnknownSessionTest() throws NoSuchMethodException {
		// Récupération de l'URI de la méthode testée
		String requestUri = FcApi.class.getMethod("checkSession", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response response = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("idSession", "testToken")
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsSecurityConfig.getApiKeys().values().stream().findAny().get())
				.when()
				.get(requestUri);


		// Vérification du code de réponses
		response.then().statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	@DisplayName("Test de checkSession - Session OK")
	public void checkSessionOKSessionTest() throws NoSuchMethodException, JsonProcessingException {
		// Ajout d'un stub pour Franceconnect
		stubForFranceconnect();

		// Création de la session
		String createSessionUri = FcApi.class.getMethod("getOrRefreshTokenFranceconnect", AuthorizationRequest.class)
				.getAnnotation(RequestMapping.class).value()[0];
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setAuthorizationCode(UUID.randomUUID().toString());
		Response responseCreate = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.body(authorizationRequest)
				.when()
				.post(createSessionUri);
		SessionUsager SessionUsager = responseCreate.getBody().as(SessionUsager.class);
		assertNotNull(SessionUsager.getIdSession());
		assertNotNull(SessionUsager.getIdentitePivot());


		// Récupération de l'URI de la méthode testée
		String requestUri = FcApi.class.getMethod("checkSession", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode (RestAssured)
		Response response = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("idSession", SessionUsager.getIdSession())
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsSecurityConfig.getApiKeys().values().stream().findAny().get())
				.when()
				.get(requestUri);

		// Vérification du code de réponses
		response.then().statusCode(HttpStatus.OK.value());

		// Appel via le client Java
		SessionUsager userClient = franceconnectMsClient.getApi().checkSession(SessionUsager.getIdSession());
		assertNotNull(userClient);
		assertEquals(SessionUsager.getIdSession(), userClient.getIdSession());
		assertEquals(SessionUsager.getIdentitePivot(), userClient.getIdentitePivot());
		assertEquals(SessionUsager.getUserId(), userClient.getUserId());
	}

	@Test
	@DisplayName("Test de deleteSession - Session inconnue")
	public void deleteSessionTest() throws NoSuchMethodException {
		// Récupération de l'URI de la méthode testée
		String requestUri = FcApi.class.getMethod("deleteSession", String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response response = given().contentType(ContentType.APPLICATION_JSON.getMimeType())
				.pathParam("idSession", "testToken")
				.header(ApiKeyFilter.HEADER_NAME, franceconnectMsSecurityConfig.getApiKeys().values().stream().findAny().get())
				.when()
				.delete(requestUri);

		// Vérification du code de réponses
		response.then().statusCode(HttpStatus.NOT_FOUND.value());
	}

	private void stubForFranceconnect() throws JsonProcessingException {
		stubFor(WireMock.post(urlEqualTo("/api/v1/token"))

				.willReturn(
					aResponse()
						.withBody(
								String.format(JSON_TOKEN_FORMAT, UUID.randomUUID().toString(), UUID.randomUUID().toString())
						)
						.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
		stubFor(WireMock.get(urlEqualTo("/api/v1/userinfo?schema=openid"))
				.willReturn(
					aResponse()
						.withBody(
								String.format(JSON_IDENTITE_PIVOT_FORMAT, UUID.randomUUID().toString())
						)
						.withHeader(CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
				)
		);
	}


}
