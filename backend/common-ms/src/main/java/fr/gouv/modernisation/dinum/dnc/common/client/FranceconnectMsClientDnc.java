package fr.gouv.modernisation.dinum.dnc.common.client;

import fr.gouv.modernisation.dinum.dnc.client.franceconnect.generated.api.client.UserSessionApi;

/**
 * Client pour le Microservice Franceconnect MS du DNC.
 * Le client doit être instanciée pour pouvoir être utiliser.
 */
public class FranceconnectMsClientDnc extends AbstractClientDncApi<UserSessionApi> {

	/**
	 * Constructeur permettant un accès au client à partir de l'URL du microservice
	 * @param baseUrlFranceconnectMs URL de base du microservice Franceconnect ciblée
	 * @param apiKey l'API Key à utiliser
	 */
	public FranceconnectMsClientDnc(String baseUrlFranceconnectMs, String apiKey) {
		this.setBaseURL(baseUrlFranceconnectMs);
		this.setApiKey(apiKey);
	}

	@Override
	public UserSessionApi getApi() {
		return new UserSessionApi(getApiClient());
	}
}
