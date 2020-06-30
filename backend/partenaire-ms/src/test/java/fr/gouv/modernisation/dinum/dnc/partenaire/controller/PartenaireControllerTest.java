package fr.gouv.modernisation.dinum.dnc.partenaire.controller;

import fr.gouv.modernisation.dinum.dnc.common.filter.ApiKeyFilter;
import fr.gouv.modernisation.dinum.dnc.partenaire.config.SecurityConfig;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Credential;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.entity.Partenaire;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.repository.CredentialRepository;
import fr.gouv.modernisation.dinum.dnc.partenaire.data.repository.PartenaireRepository;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.PartenaireInfos;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.server.PartenaireApi;
import fr.gouv.modernisation.dinum.dnc.partenaire.service.PartenaireService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;


@TestPropertySource(locations = {"classpath:application-test.yml" }, properties = {"currentTest=DemarcheControllerTest" })
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
public class PartenaireControllerTest {

	@Autowired
	PartenaireService partenaireService;

	@Autowired
	PartenaireRepository partenaireRepository;

	@Autowired
	CredentialRepository credentialRepository;

	@Autowired
	private SecurityConfig securityConfig;

	@BeforeAll
	public static void init(@Autowired Environment environment) {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = Integer.valueOf(environment.getProperty("server.port"));
	}

	@BeforeEach
	public void setup() {
		credentialRepository.deleteAll();
		partenaireRepository.deleteAll();
	}

	@Test
	@DisplayName("Test de getPartenaires/partenaireCheck")
	public void getPartenairesTest() throws NoSuchMethodException {

		PartenaireInfos partenaireInfos = new PartenaireInfos();
		partenaireInfos.setSiret("18004625200177");
		partenaireInfos.setLibelleCourt("BNF");
		partenaireInfos.setLibelleLong("BIBLIOTHEQUE NATIONALE DE FRANCE - BNF");
		partenaireInfos.setLogo("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9Im5vIj8+CjxzdmcKICAgeG1sbnM6ZGM9Imh0dHA6Ly9wdXJsLm9yZy9kYy9lbGVtZW50cy8xLjEvIgogICB4bWxuczpjYz0iaHR0cDovL2NyZWF0aXZlY29tbW9ucy5vcmcvbnMjIgogICB4bWxuczpyZGY9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkvMDIvMjItcmRmLXN5bnRheC1ucyMiCiAgIHhtbG5zOnN2Zz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciCiAgIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIKICAgdmlld0JveD0iMCAwIDY3Ni41NjI1IDI1Mi4zMjUiCiAgIGhlaWdodD0iMjUyLjMyNSIKICAgd2lkdGg9IjY3Ni41NjI1IgogICB4bWw6c3BhY2U9InByZXNlcnZlIgogICB2ZXJzaW9uPSIxLjEiCiAgIGlkPSJzdmcyIj48bWV0YWRhdGEKICAgICBpZD0ibWV0YWRhdGE4Ij48cmRmOlJERj48Y2M6V29yawogICAgICAgICByZGY6YWJvdXQ9IiI+PGRjOmZvcm1hdD5pbWFnZS9zdmcreG1sPC9kYzpmb3JtYXQ+PGRjOnR5cGUKICAgICAgICAgICByZGY6cmVzb3VyY2U9Imh0dHA6Ly9wdXJsLm9yZy9kYy9kY21pdHlwZS9TdGlsbEltYWdlIiAvPjwvY2M6V29yaz48L3JkZjpSREY+PC9tZXRhZGF0YT48ZGVmcwogICAgIGlkPSJkZWZzNiI+PGNsaXBQYXRoCiAgICAgICBpZD0iY2xpcFBhdGgxOCIKICAgICAgIGNsaXBQYXRoVW5pdHM9InVzZXJTcGFjZU9uVXNlIj48cGF0aAogICAgICAgICBpZD0icGF0aDIwIgogICAgICAgICBkPSJtIDAsMCA1NDEyLjUsMCAwLDIwMTguNTcgTCAwLDIwMTguNTcgMCwwIFoiIC8+PC9jbGlwUGF0aD48L2RlZnM+PGcKICAgICB0cmFuc2Zvcm09Im1hdHJpeCgxLjI1LDAsMCwtMS4yNSwwLDI1Mi4zMjUpIgogICAgIGlkPSJnMTAiPjxnCiAgICAgICB0cmFuc2Zvcm09InNjYWxlKDAuMSwwLjEpIgogICAgICAgaWQ9ImcxMiI+PGcKICAgICAgICAgaWQ9ImcxNCI+PGcKICAgICAgICAgICBjbGlwLXBhdGg9InVybCgjY2xpcFBhdGgxOCkiCiAgICAgICAgICAgaWQ9ImcxNiI+PHBhdGgKICAgICAgICAgICAgIGlkPSJwYXRoMjIiCiAgICAgICAgICAgICBzdHlsZT0iZmlsbDojMjMxZjIwO2ZpbGwtb3BhY2l0eToxO2ZpbGwtcnVsZTpub256ZXJvO3N0cm9rZTpub25lIgogICAgICAgICAgICAgZD0ibSAyNDUuODk4LDEwMDkuMzEgMTI4LjYzMywxMTQuMzcgYyAxMS40NzcsOC41OCAxNC4zMzYsMjIuODggMTQuMzM2LDMxLjQxIDguNTQzLDEzNC4zOCAyOC41NzQsNDQ2LjAxIDE2Mi45Myw4NjMuNTQgbCAtMjY1Ljg5MSwwIGMgLTE5Ljk4OCwwIC0zMS40MDIsLTE0LjM2IC0zNC4yOTcsLTI1LjgxIDAsLTIuODYgLTE2MC4xMjQ2LC00NDMuMTUgLTE2OC43MDY3LC05MDMuNDcgTCAwLDEwMDkuMzEgODIuOTAyMyw5MjkuMjExIEMgOTEuNDg0NCw0NjguODkxIDI1MS42MDksMjguNjAxNiAyNTEuNjA5LDI1LjczODMgMjU0LjUwNCwxNC4zMDA4IDI2NS45MTgsMCAyODUuOTA2LDAgTCA1NTEuNzk3LDAgQyA0MTcuNDQxLDQxNy40NjkgMzk3LjQxLDcyOS4wOSAzODguODY3LDg2My40OCBjIDAsOC41OSAtMi44NTksMjIuODIxIC0xNC4zMzYsMzEuNDY5IEwgMjQ1Ljg5OCwxMDA5LjMxIFogTSAyMTAxLjUsNjE0LjY2IGMgMCwtMjc3LjI4OSAtMTg1LjgyLC0zMTcuMjgxIC00NDMuMTUsLTMxNy4yODEgbCAtMjMxLjU5LDAgMCw2MzQuNjk5IDE4NS44MiwwIGMgMjMxLjU5LDAgNDg4LjkyLC0yNS43NSA0ODguOTIsLTMxNy40MTggbSAtNjc0Ljc0LDU1Ny42NCAwLDU0OC45NSAyMDUuODUsMCBjIDE3NC40LDAgMzYwLjI4LC00MC4wNSAzNjAuMjgsLTI2My4wNiAwLC0yNDUuOTUgLTE4MC4xNiwtMjg1Ljg5IC00MjAuMzYsLTI4NS44OSBsIC0xNDUuNzcsMCB6IG0gLTI4OC44LDc5MS45OCAwLC0xOTA5Ljk5ODcgNTM0LjY2LDAgYyAzNTQuNTcsMCA3MzQuODIsNzcuMTc5NyA3MzQuODIsNTYwLjM3ODcgMCwyNDMuMTAyIC0xNzEuNTUsNDIzLjI2IC00MzcuNDMsNDYzLjI1IDIxNC40NSw1NC4zNSAzMTcuMzQsMjAyLjk4IDMxNy4zNCw0MjMuMTkgMCwzMjAuMjEgLTI2NS45MSw0NjMuMTggLTU4Ni4wOSw0NjMuMTggbCAtNTYzLjMsMCB6IG0gMTUzMi41MSwtNDA1Ljk3IGMgNDUuNzYsLTkxLjU0IDUxLjQ4LC0zMDMuMTYgNTEuNDgsLTQ0OC45MyBsIDAsLTEwNTUuMDk4NyAyODguNzcsMCAwLDcyMy40Mzc3IGMgMCwyMjguNzMxIDMxLjQyLDMzMS42NjEgODIuOTYsNDA4LjgzMSA2NS43NCw5NC4zNCAxNjIuOTQsMTQ1LjgzIDI3Ny4zMSwxNDUuODMgMTAwLjExLDAgMTcxLjUxLC0zNC4zMyAyMjAuMTQsLTg4LjYyIDUxLjUsLTU3LjIxIDc0LjM4LC0xNDAuMTEgNzQuMzgsLTMzNy40MzIgbCAwLC04NTIuMDQ2NyAyODguOCwwIDAsOTEyLjExNjcgYyAwLDI1Ny4zMzIgLTQyLjkxLDM4MC4yODIgLTEyMyw0NzEuNzYyIC04Mi44NCw5NC4zNSAtMjA1Ljg1LDE0OC43IC0zNjguODUsMTQ4LjcgLTIxMS42MiwwIC0zODAuMjcsLTg1Ljc2IC00NzEuNywtMjQwLjE4IDAsOTQuMzUgLTExLjUsMTYyLjk0IC0zMS40NiwyMTEuNjMgbCAtMjg4LjgzLDAgeiBtIDE5NDQuMjksLTQwMC4zNiAwLDU1NC43MSA3OTcuNjgsMCAwLDI1MS42MiAtMTA5Mi4yMiwwIDAsLTE5MDkuOTk4NyAzMDMuMTEsMCAwLDg0OS4xODc3IDcyMC41MiwwIDAsMjU0LjQ4MSAtNzI5LjA5LDAiIC8+PC9nPjwvZz48L2c+PC9nPjwvc3ZnPg==".getBytes());

		// Requête PUT pour création
		String requestPutCreatePartenaireUri = PartenaireApi.class.getMethod("createPartenaire", PartenaireInfos.class)
				.getAnnotation(RequestMapping.class).value()[0];
		// Appel du endpoint
		Response responsePut = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.body(partenaireInfos)
				.when()
				.post(requestPutCreatePartenaireUri);
		// Assertions de la réponse
		Assertions.assertEquals(HttpStatus.OK.value(), responsePut.getStatusCode());
		PartenaireInfos partenaireInfosSauvegarde = responsePut.getBody().as(PartenaireInfos.class);
		Assertions.assertEquals(partenaireInfos.getSiret(), partenaireInfosSauvegarde.getSiret());
		Assertions.assertEquals(partenaireInfos.getLibelleCourt(), partenaireInfosSauvegarde.getLibelleCourt());
		Assertions.assertEquals(partenaireInfos.getLibelleLong(), partenaireInfosSauvegarde.getLibelleLong());

		// 401 Si on renvoie la même requête
		// Appel du endpoint
		Response responsePutSameData = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.body(partenaireInfos)
				.when()
				.post(requestPutCreatePartenaireUri);
		// Assertions de la réponse
		Assertions.assertEquals(HttpStatus.CONFLICT.value(), responsePutSameData.getStatusCode());

		// Récupération de l'URI de la méthode testée
		String requestGetAllUri = PartenaireApi.class.getMethod("getListePartenaires")
				.getAnnotation(RequestMapping.class).value()[0];

		// Appel de la méthode
		Response responseGet = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.when()
				.get(requestGetAllUri);

		// Vérification du code de réponses

		responseGet.then().statusCode(HttpStatus.OK.value());
		List<PartenaireInfos> resultList = Arrays.asList(responseGet.getBody().as(PartenaireInfos[].class));
		Assertions.assertEquals(1, resultList.size());
		Assertions.assertEquals(partenaireInfos.getSiret(), resultList.get(0).getSiret());

		// Credentials
		String clientId = UUID.randomUUID().toString();
		String clientSecret = UUID.randomUUID().toString();

		String requestCheckCredentialUri = PartenaireApi.class.getMethod("checkCredentialsPartenaire", String.class, String.class, String.class)
				.getAnnotation(RequestMapping.class).value()[0];

		// Tentative sans credentials
		Response responseCheckCredentialGet = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.param("siret_partenaire", partenaireInfos.getSiret())
				.header("client_id", clientId)
				.header("client_secret", clientSecret)
				.when()
				.get(requestCheckCredentialUri);
		responseCheckCredentialGet.then().statusCode(HttpStatus.UNAUTHORIZED.value());

		// Création du Credential
		Credential credential1 = partenaireService.saveOrUpdateCredentialsForPartenaire(partenaireInfos.getSiret(), clientId, clientSecret, null);
		Assertions.assertNotNull(credential1);
		credentialRepository.flush();


		responseCheckCredentialGet = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.param("siret_partenaire", partenaireInfos.getSiret())
				.header("client_id", clientId)
				.header("client_secret", clientSecret)
				.when()
				.get(requestCheckCredentialUri);
		responseCheckCredentialGet.then().statusCode(HttpStatus.OK.value());

		// Changement du Credential existant
		String newSecret = UUID.randomUUID().toString();

		Credential credential2 = partenaireService.saveOrUpdateCredentialsForPartenaire(partenaireInfos.getSiret(), clientId, newSecret, clientSecret);
		Assertions.assertNotNull(credential2);
		Assertions.assertEquals(credential1.getId(), credential2.getId());
		credentialRepository.flush();

		// Test avec l'ancien Credential KO
		responseCheckCredentialGet = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.param("siret_partenaire", partenaireInfos.getSiret())
				.header("client_id", clientId)
				.header("client_secret", clientSecret)
				.when()
				.get(requestCheckCredentialUri);
		responseCheckCredentialGet.then().statusCode(HttpStatus.UNAUTHORIZED.value());

		// Test avec l'e nouveau Credential OK
		responseCheckCredentialGet = given()
				.contentType(ContentType.APPLICATION_JSON.getMimeType())
				.header(ApiKeyFilter.HEADER_NAME, securityConfig.getApiKeys().values().stream().findAny().get())
				.param("siret_partenaire", partenaireInfos.getSiret())
				.header("client_id", clientId)
				.header("client_secret", newSecret)
				.when()
				.get(requestCheckCredentialUri);
		responseCheckCredentialGet.then().statusCode(HttpStatus.OK.value());

	}
}
