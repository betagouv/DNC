package fr.gouv.modernisation.dinum.dnc.common.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests simples pour passer la couverture
 */
public class PartenaireMsClientTest {

	@Test
	public void instanceAndGetterSetterClientTest() {
		String testValue = "Test";
		PartenaireMsClientDnc partenaireMsClientDnc = new PartenaireMsClientDnc(testValue, testValue);
		assertNotNull(partenaireMsClientDnc.getApi());
		assertNotNull(partenaireMsClientDnc.getApiClient());
		assertEquals(testValue, partenaireMsClientDnc.getApiKey());
		assertEquals(testValue, partenaireMsClientDnc.getBaseURL());


		partenaireMsClientDnc.setApiHeaderName(testValue);
		assertEquals(testValue, partenaireMsClientDnc.getApiHeaderName());
		partenaireMsClientDnc.setApiKeyAuthName(testValue);
		assertEquals(testValue, partenaireMsClientDnc.getApiKeyAuthName());
		partenaireMsClientDnc.setFranceconnectAuthName(testValue);
		assertEquals(testValue, partenaireMsClientDnc.getFranceconnectAuthName());
		partenaireMsClientDnc.setTokenFranceconnect(testValue);
		assertEquals(testValue, partenaireMsClientDnc.getTokenFranceconnect());
	}
}
