package fr.gouv.modernisation.dinum.dnc.franceconnect.controller;

import fr.gouv.modernisation.dinum.dnc.franceconnect.client.RestClientFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.IdentitePivotFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.TokenFranceconnectFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.AuthorizationRequest;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import fr.gouv.modernisation.dinum.dnc.franceconnect.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitaires pour {@link FcApiImpl}
 */
public class FcApiImplTest {

	@InjectMocks
	private FcApiImpl fcApi;

	@Mock
	private SessionService sessionService;

	@Mock
	RestClientFranceconnect restClientFranceconnect;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getAuthorizationCodeTokenUnknownTest() {
		String authorizationCode = "test";
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setAuthorizationCode(authorizationCode);

		Mockito.when(restClientFranceconnect.getAccessToken(authorizationRequest)).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fcApi.getOrRefreshTokenFranceconnect(authorizationRequest));

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());

	}

	@Test
	public void getAuthorizationCodeIdentitePivotUnknownTest() {
		String authorizationCode = "test";
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setAuthorizationCode(authorizationCode);

		TokenFranceconnect token = new TokenFranceconnectFactory().create();

		Mockito.when(restClientFranceconnect.getAccessToken(authorizationRequest)).thenReturn(token);

		Mockito.when(restClientFranceconnect.getIdPivot(token)).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fcApi.getOrRefreshTokenFranceconnect(authorizationRequest));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

	}

	@Test
	public void getAuthorizationCodeCreationSessionOKTest() {
		String authorizationCode = "test";
		AuthorizationRequest authorizationRequest = new AuthorizationRequest();
		authorizationRequest.setAuthorizationCode(authorizationCode);

		TokenFranceconnect token = new TokenFranceconnectFactory().create();
		IdentitePivot identitePivot = new IdentitePivotFactory().create();
		Session session = new Session(token, identitePivot);

		Mockito.when(restClientFranceconnect.getAccessToken(authorizationRequest)).thenReturn(token);
		Mockito.when(restClientFranceconnect.getIdPivot(token)).thenReturn(identitePivot);
		Mockito.when(sessionService.save(token, identitePivot)).thenReturn(session);

		ResponseEntity<SessionUsager> response = fcApi.getOrRefreshTokenFranceconnect(authorizationRequest);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(identitePivot, response.getBody().getIdentitePivot());
		assertEquals(session.getId(), response.getBody().getIdSession());
	}

	@Test
	public void getCodeCheckSessionOKTest() {
		String idSession = "test";
		Session session = new Session( new TokenFranceconnectFactory().create(), new IdentitePivotFactory().create());

		Mockito.when(sessionService.getSession(idSession)).thenReturn(session);

		ResponseEntity<SessionUsager> response = fcApi.checkSession(idSession);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(session.getIdentitePivot(), response.getBody().getIdentitePivot());
		assertEquals(session.getId(), response.getBody().getIdSession());
	}

	@Test
	public void getCheckSessionSessionInconnueTest() {
		String idSession = "test";

		Mockito.when(sessionService.getSession(idSession)).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fcApi.checkSession(idSession));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	public void getDeleteSessionOKTest() {
		Session session = new Session( new TokenFranceconnectFactory().create(), new IdentitePivotFactory().create());

		Mockito.when(sessionService.getSession(session.getId())).thenReturn(session);

		ResponseEntity<Void> response = fcApi.deleteSession(session.getId());

		assertEquals(HttpStatus.OK, response.getStatusCode());
	}

	@Test
	public void getDeleteSessionSessionInconnueTest() {
		String idSession = "test";

		Mockito.when(sessionService.getSession(idSession)).thenReturn(null);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> fcApi.deleteSession(idSession));

		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
	}

	@Test
	public void getDeleteSessionBadRequestTest() {
		ResponseEntity<Void> response = fcApi.deleteSession(null);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}
