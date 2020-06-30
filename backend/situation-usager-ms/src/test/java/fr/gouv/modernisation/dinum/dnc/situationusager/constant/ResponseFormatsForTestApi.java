package fr.gouv.modernisation.dinum.dnc.situationusager.constant;

/**
 * Classe avec des formats de réponses pour toutes les API pour permettre des mocks via Wiremock
 */
public class ResponseFormatsForTestApi {

	/**
	 * Format pour générer une réponse de Franceconnect-MS
	 */
	public static final String JSON_IDENTITE_PIVOT_FORMAT = "{ \"user_id\": \"%s\", \"idSession\": \"%s\", \"currentToken\": \"%s\", \"identitePivot\": { \"given_name\": \"Jill\", \"family_name\": \"Valentine\" } }";


	public static final String JSON_GRANTED_ACCES_PARTENAIRE_MS_FORMAT = "{ \"id\": \"%s\" }";

	/**
	 * Format pour générer une réponse de la CNAM/Amelie.fr
	 */
	public static final String CNAM_RESPONSE_FORMAT = "{\n" +
			"  \"date_naissance\": \"1997-09-07\",\n" +
			"  \"medecin_traitant_choisi\": true,\n" +
			"  \"nir\": \"2530647001069\",\n" +
			"  \"nom_famille\": \"VALENTINE\",\n" +
			"  \"nom_usage\": \"VALENTINE\",\n" +
			"  \"prenom\": \"Jill\",\n" +
			"  \"presence_CMUC_ACS\": false,\n" +
			"  \"qualite\": \"assure\",\n" +
			"  \"rang_naissance\": 0\n" +
			"}";

	/**
	 * Format pour une réponse de l'API /me de la DGFIP (https://api.gouv.fr/api/api-particulier#doc_tech)
	 */
	public static final String DGFIP_IMPOT_SVAIR_RESPONSE_FORMAT = "{\n" +
			"  \"declarant1\": {\n" +
			"    \"nom\": \"VALENTINE\",\n" +
			"    \"nomNaissance\": \"VALENTINE\",\n" +
			"    \"prenoms\": \"Jill\",\n" +
			"    \"dateNaissance\": \"07/09/1997\"\n" +
			"  },\n" +
			"  \"foyerFiscal\": {\n" +
			"    \"annee\": 2019,\n" +
			"    \"adresse\": \"56 Stars Road, 99148 Raccoon City\"\n" +
			"  },\n" +
			"  \"declarant2\": {\n" +
			"    \"nom\": \"REDFIELD\",\n" +
			"    \"nomNaissance\": \"REDFIELD\",\n" +
			"    \"prenoms\": \"Chris\",\n" +
			"    \"dateNaissance\": \"26/01/1997\"\n" +
			"  },\n" +
			"  \"dateRecouvrement\": \"10/10/2019\",\n" +
			"  \"dateEtablissement\": \"08/07/2019\",\n" +
			"  \"nombreParts\": 2,\n" +
			"  \"situationFamille\": \"Marié(e)s\",\n" +
			"  \"nombrePersonnesCharge\": 2,\n" +
			"  \"revenuBrutGlobal\": 26922,\n" +
			"  \"revenuImposable\": 26922,\n" +
			"  \"impotRevenuNetAvantCorrections\": 2165,\n" +
			"  \"montantImpot\": 2165,\n" +
			"  \"revenuFiscalReference\": 26922,\n" +
			"  \"anneeImpots\": \"2019\",\n" +
			"  \"anneeRevenus\": \"2018\",\n" +
			"  \"erreurCorrectif\": \"string\",\n" +
			"  \"situationPartielle\": \"(*) Situation  2018  partielle\"\n" +
			"}";

	/**
	 * Format pour une réponse de l'API /caf/famille de la CNAF (https://api.gouv.fr/api/api-particulier#doc_tech)
	 */
	public static final String CNAF_RESPONSE_FORMAT = "{\n" +
			"  \"allocataires\": [\n" +
			"    {\n" +
			"      \"nomPrenom\": \"JILL VALENTINE\",\n" +
			"      \"dateDeNaissance\": \"07091997\",\n" +
			"      \"sexe\": \"F\"\n" +
			"    },\n" +
			"    {\n" +
			"      \"nomPrenom\": \"CHRIS REDFIELD\",\n" +
			"      \"dateDeNaissance\": \"26012019\",\n" +
			"      \"sexe\": \"M\"\n" +
			"    }\n" +
			"  ],\n" +
			"  \"enfants\": [\n" +
			"    {\n" +
			"      \"nomPrenom\": \"LANA VALENTINE\",\n" +
			"      \"dateDeNaissance\": \"02052018\",\n" +
			"      \"sexe\": \"F\"\n" +
			"    }\n" +
			"  ],\n" +
			"  \"adresse\": {\n" +
			"    \"identite\": \"Madame JILL VALENTINE\",\n" +
			"    \"complementIdentiteGeo\": \"\",\n" +
			"    \"numeroRue\": \"56 Stars Road\",\n" +
			"    \"codePostalVille\": \"99148 Raccoon City\",\n" +
			"    \"pays\": \"FRANCE\"\n" +
			"  },\n" +
			"  \"quotientFamilial\": 1754,\n" +
			"  \"annee\": 2017,\n" +
			"  \"mois\": 4\n" +
			"}";

	/**
	 * Constructeur par défaut privé pour cacher son exposition
	 */
	private ResponseFormatsForTestApi() {
		// Constructeur par défaut privé pour cacher son exposition
	}

}
