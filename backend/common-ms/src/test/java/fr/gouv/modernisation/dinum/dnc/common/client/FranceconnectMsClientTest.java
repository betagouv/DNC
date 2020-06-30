package fr.gouv.modernisation.dinum.dnc.common.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests simples pour passer la couverture
 */
public class FranceconnectMsClientTest {

	@Test
	public void instanceAndGetterSetterClientTest() {
		String testValue = "Test";
		FranceconnectMsClientDnc franceconnectMsClient = new FranceconnectMsClientDnc(testValue, testValue);
		assertNotNull(franceconnectMsClient.getApi());
		assertNotNull(franceconnectMsClient.getApiClient());
		assertEquals(testValue, franceconnectMsClient.getApiKey());
		assertEquals(testValue, franceconnectMsClient.getBaseURL());


		franceconnectMsClient.setApiHeaderName(testValue);
		assertEquals(testValue, franceconnectMsClient.getApiHeaderName());
		franceconnectMsClient.setApiKeyAuthName(testValue);
		assertEquals(testValue, franceconnectMsClient.getApiKeyAuthName());
		franceconnectMsClient.setFranceconnectAuthName(testValue);
		assertEquals(testValue, franceconnectMsClient.getFranceconnectAuthName());
		franceconnectMsClient.setTokenFranceconnect(testValue);
		assertEquals(testValue, franceconnectMsClient.getTokenFranceconnect());
	}
}
