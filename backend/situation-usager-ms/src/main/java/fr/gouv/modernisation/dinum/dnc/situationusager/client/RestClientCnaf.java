package fr.gouv.modernisation.dinum.dnc.situationusager.client;

import fr.gouv.modernisation.dinum.dnc.client.particulier.open.generated.api.client.CafApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Famille;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Client vers l'API non franceconnecté de la CNAF.
 *
 */
@Service
public class RestClientCnaf extends AbstractBaseClientParticulierApi<CafApi> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientCnaf.class);

	/**
	 * Initialisation des valeurs en configuration
	 */
	@Autowired
	public void setupConfiguration(@Value(value = "${api.cnaf.baseUrl}") String apiBaseUrl,
								   @Value(value = "${api.cnaf.apiKey}") String apiKey) {
		this.setBaseURL(apiBaseUrl);
		this.setApiKey(apiKey);
	}

	@Override
	public CafApi getApi() {
		return new CafApi(getApiClient());
	}

	/**
	 * Wrapper de la méthode {@link CafApi#cafFamilleGet(String, String, String, String)} pour gérer l'API Key
	 * et l'end-user
	 * @param numeroAllocataire numéro d'allocataire
	 * @param codePostal code postal
	 * @return l'objet {@link Famille} correspondant aux paramètres, sinon {@code null}
	 */
	public Famille getFamille(String numeroAllocataire, String codePostal) {
		try {
			return getApi().cafFamilleGet(numeroAllocataire, codePostal, apiKey, "Dossier Numérique du Citoyen");
		}
		catch (RestClientException e) {
			LOGGER.error("Une erreur est survenue lors de la récupération des informations via l'API de la CAF", e);
		}
		return null;
	}
}
