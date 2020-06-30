package fr.gouv.modernisation.dinum.dnc.franceconnect.service;

import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.IdentitePivotFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.factory.TokenFranceconnectFactory;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.IdentitePivot;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.TokenFranceconnect;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.data.Session;
import fr.gouv.modernisation.dinum.dnc.franceconnect.redis.repository.SessionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests Unitaires de {@linhk SessionDncServiceTest}
 */
public class SessionServiceTest {

	@InjectMocks
	private SessionService sessionService;

	@Mock
	private RedisTemplate redisTemplate;

	@Mock
	private SessionRepository sessionRepository;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(sessionService, "sessionTimeToLive", 60);
	}

	@Test
	public void saveTest() {
		TokenFranceconnect token = new TokenFranceconnectFactory().create();

		IdentitePivot identitePivot = new IdentitePivotFactory().create();

		// Return value
		Session session = new Session(token, identitePivot);
		session.setId(UUID.randomUUID().toString());

		Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(session);
		Mockito.when(redisTemplate.expire(Session.NAMESPACE_REDIS + ":" + session.getId(), 60, TimeUnit.SECONDS)).thenReturn(Boolean.TRUE);

		Session sessionCree = sessionService.save(token, identitePivot);

		assertEquals(session, sessionCree);

	}

	@Test
	public void getSessionTest() {
		TokenFranceconnect token = new TokenFranceconnectFactory().create();

		IdentitePivot identitePivot = new IdentitePivotFactory().create();

		// Return value
		Session session = new Session(token, identitePivot);
		session.setId(UUID.randomUUID().toString());

		Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.of(session));
		Mockito.when(redisTemplate.expire(Session.NAMESPACE_REDIS + ":" + session.getId(), 60, TimeUnit.SECONDS)).thenReturn(Boolean.TRUE);

		Session sessionCree = sessionService.getSession(session.getId());

		assertEquals(session, sessionCree);
	}

	@Test
	public void getSessionKOSessionInconnueTest() {
		Mockito.when(sessionRepository.findById(Mockito.any())).thenReturn(Optional.empty());

		Session sessionCree = sessionService.getSession(UUID.randomUUID().toString());

		assertNull(sessionCree);
	}

	@Test
	public void refreshTokenTest() {
		TokenFranceconnect token = new TokenFranceconnectFactory().create();
		TokenFranceconnect token2 = new TokenFranceconnectFactory().create();

		IdentitePivot identitePivot = new IdentitePivotFactory().create();

		// Return value
		Session session = new Session(token, identitePivot);
		session.setId(UUID.randomUUID().toString());
		Session session2 = new Session(token2, identitePivot);
		session2.setId(session.getId());

		Mockito.when(sessionRepository.save(Mockito.any())).thenReturn(session2);
		Mockito.when(redisTemplate.expire(Session.NAMESPACE_REDIS + ":" + session.getId(), 60, TimeUnit.SECONDS)).thenReturn(Boolean.TRUE);

		Session sessionMiseAJour = sessionService.refreshToken(session,token2);

		assertNotEquals(session, sessionMiseAJour);
		assertEquals(session2, sessionMiseAJour);

	}

	@Test
	public void deleteSessionTest() {
		Assertions.assertDoesNotThrow(() -> sessionService.delete(UUID.randomUUID().toString()));
	}
}
