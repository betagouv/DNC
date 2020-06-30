package fr.gouv.modernisation.dinum.dnc.common.client;

import fr.gouv.modernisation.dinum.dnc.client.partenaire.generated.api.client.PartenaireApi;

/**
 * Client pour le Microservice Partenaire MS du DNC.
 * Le client doit être instanciée pour pouvoir être utiliser.
 */
public class PartenaireMsClientDnc extends AbstractClientDncApi<PartenaireApi> {

	/**
	 * Constructeur permettant un accès au client à partir de l'URL du microservice
	 * @param baseUrlPartenaireMs URL de base du microservice Partenaire-MS ciblée
	 * @param apiKeyForPartenaireMs l'API Key à utiliser
	 */
	public PartenaireMsClientDnc(String baseUrlPartenaireMs, String apiKeyForPartenaireMs) {
		this.setBaseURL(baseUrlPartenaireMs);
		this.setApiKey(apiKeyForPartenaireMs);
	}

	@Override
	public PartenaireApi getApi() {
		return new PartenaireApi(getApiClient());
	}
}
