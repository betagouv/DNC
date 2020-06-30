package fr.gouv.modernisation.dinum.dnc.situationusager.client;

import fr.gouv.modernisation.dinum.dnc.client.particulier.open.generated.api.client.ImpotsApi;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Declaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

/**
 * Client vers l'API non franceconnecté de la DGIP.
 *
 */
@Service
public class RestClientDgfip extends AbstractBaseClientParticulierApi<ImpotsApi> {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientDgfip.class);

	/**
	 * Initialisation des valeurs en configuration
	 */
	@Autowired
	public void setupConfiguration(@Value(value = "${api.dgfip.baseUrl}") String apiBaseUrl,
								   @Value(value = "${api.dgfip.apiKey}") String apiKey) {
		this.setBaseURL(apiBaseUrl);
		this.setApiKey(apiKey);
	}

	@Override
	public ImpotsApi getApi() {
		return new ImpotsApi(getApiClient());
	}

	/**
	 * Wrapper de la méthode {@link ImpotsApi#impotsSvairGet(String, String, String, String)} pour gérer l'API Key
	 * et l'end-user
	 * @param numeroFiscal numéro fiscal
	 * @param referenceAvis référence de l'avis fiscal
	 * @return l'objet {@link Declaration} correspondant aux paramètres, sinon {@code null}
	 */
	public Declaration getDeclaration(String numeroFiscal, String referenceAvis) {
		try {
			return getApi().impotsSvairGet(numeroFiscal, referenceAvis, apiKey, "Dossier Numérique du Citoyen");
		}
		catch (RestClientException e) {
			LOGGER.error("Une erreur est survenue lors de la récupération des informations via l'API de la DGFIP", e);
		}
		return null;
	}
}
