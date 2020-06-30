package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import fr.gouv.modernisation.dinum.dnc.common.exception.UnknownException;
import fr.gouv.modernisation.dinum.dnc.franceconnect.generated.api.model.SessionUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientCnaf;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientDgfip;
import fr.gouv.modernisation.dinum.dnc.situationusager.client.RestClientFCCnam;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Beneficiaire;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Service permettant de récupérer des données Franceconnectées
 */
@Service
public class FetchDataService {

	/**
	 * Code du champ du numéro d'allocataire.
	 */
	private static final String NUMERO_ALLOCATAIRE = "numeroAllocataire";

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FetchDataService.class);

	/**
	 * Constantes pour les accès aux Map
	 */
	public static final String MAP_KEY_FIELD_ENFANT_NOM_PRENOM = "enfant-nomPrenom";
	public static final String MAP_KEY_FIELD_SIRET_PARTENAIRE = "siretPartenaire";
	public static final String MAP_KEY_FIELD_SIRET = "siret";
	public static final String MAP_KEY_FIELD_RAISON_SOCIALE = "raisonSociale";
	public static final String MAP_KEY_FIELD_CODE_POSTAL = "codePostal";

	/**
	 * Service des {@link fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager}
	 */
	@Autowired
	private SituationUsagerService situationUsagerService;

	/**
	 * Client vers API CNAM (Franceconenctée)
	 */
	@Autowired
	private RestClientFCCnam restClientFCCnam;

	/**
	 * Client sur API CNAF (api.particuliers)
	 */
	@Autowired
	private RestClientCnaf restClientCnaf;

	/**
	 * Client sur API DGFIP (api.particuliers)
	 */
	@Autowired
	private RestClientDgfip restClientDgfip;

	/**
	 * Utilise les différents clients des API Franceconnectées de manières Asynchrone pour récupérer les données
	 * de l'usager.
	 * @param situationUsager l'objet {@link SituationUsager} à mettre à jour.
	 */
	public void getDonnneesFranceconnecteesForStandAlone(@NotNull SituationUsager situationUsager) {
		try {
			// API CNAM
			CompletableFuture<Beneficiaire> futureBeneficiaire = restClientFCCnam.getBeneficiaire(situationUsager.getSessionUsager().getCurrentToken());
			// Autres API Franceconnectées..


			// Initialisation des champs avec les futures obtenues
			situationUsager.setBeneficiaireCnam(futureBeneficiaire.get());
		}
		catch( ExecutionException e) {
			// Une erreur est survenu, la session n'est plus viable vis à vis des données
			situationUsagerService.delete(situationUsager.getId());
			throw new UnknownException("ErreurAppelAPIsFranceConnectees",
					String.format("Erreur lors de la récupération des données Franceconnectée pour l'idUsager : %s et le token : %s",
							situationUsager.getSessionUsager().getUserId(), situationUsager.getSessionUsager().getCurrentToken()),
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		catch (InterruptedException e) {
			LOGGER.error("L'exécution de la récupération des données Franceconnectées a été intérrompue");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Renvoie les données brutes pour un type de démarche données et une {@link SessionUsager} avec un token valide.
	 * Si le token est invalide, aucune données ne pourront être récupérées.
	 * @param typeDemarche {@link DemarcheEnum} type de la démarche en cours de traitement.
	 * @param sessionUsager {@link SessionUsager} de l'usager liées aux données à récupérer
	 * @param secretsSupplementaires {@link Map} contenant les secrets supplémentaires de l'usager pour la démarche
	 * @return la {@link Map} de {@link String} contenant les données brutes de l'usaqger pour le type de démarche donné
	 */
	public Map<String, String> getDonneesForDemarche(@NotNull DemarcheEnum typeDemarche,
													 @NotNull SessionUsager sessionUsager,
													 Map<String, String> secretsSupplementaires) {

		try {
			Map<String, String> rawData = DemarcheUtils.getDataFromIdentitePivot(sessionUsager.getIdentitePivot());
			rawData.put(MAP_KEY_FIELD_SIRET_PARTENAIRE, secretsSupplementaires.get(MAP_KEY_FIELD_SIRET_PARTENAIRE));

			switch (typeDemarche) {
				case CARTE_STATIONNEMENT:
					rawData.put(MAP_KEY_FIELD_SIRET, secretsSupplementaires.get(MAP_KEY_FIELD_SIRET));
					rawData.put(MAP_KEY_FIELD_RAISON_SOCIALE, secretsSupplementaires.get(MAP_KEY_FIELD_RAISON_SOCIALE));
					rawData.put("vehicules", getMockDataFromSIV());
					break;
				case RESTAURATION_SCOLAIRE:
				case INSCRIPTION_ECOLE:
					// Partie non FranceConnectee - nécéssite des données supplémentaires
					String codePostal = secretsSupplementaires.get(MAP_KEY_FIELD_CODE_POSTAL);
					String numeroAllocataire = secretsSupplementaires.get(NUMERO_ALLOCATAIRE);
					if(StringUtils.isNotBlank(codePostal) && StringUtils.isNotBlank(numeroAllocataire)) {
						DemarcheUtils.updateDataFromFamille(rawData, restClientCnaf.getFamille(numeroAllocataire, codePostal));
					}

					String numeroFiscal = secretsSupplementaires.get("numeroFiscal");
					String referenceAvisFiscal = secretsSupplementaires.get("referenceAvisFiscal");
					if(StringUtils.isNotBlank(numeroFiscal) && StringUtils.isNotBlank(referenceAvisFiscal)) {
						DemarcheUtils.updateDataFromDeclaration(rawData, restClientDgfip.getDeclaration(numeroFiscal, referenceAvisFiscal));
					}

					// Données Bouchonnées
					if(!secretsSupplementaires.containsKey(MAP_KEY_FIELD_CODE_POSTAL) || !secretsSupplementaires.containsKey(NUMERO_ALLOCATAIRE)) {
						rawData.put(MAP_KEY_FIELD_ENFANT_NOM_PRENOM,
								String.join(" ", sessionUsager.getIdentitePivot().getFamilyName(),
										sessionUsager.getIdentitePivot().getGivenName(),
										"II")
						);
						rawData.put("adresseEnfant", rawData.getOrDefault("adresseComplete", ""));
						rawData.put("nomParent",sessionUsager.getIdentitePivot().getFamilyName());
						rawData.put("prenomParent",sessionUsager.getIdentitePivot().getGivenName());
						rawData.put("adresseParent",rawData.getOrDefault("adresseComplete", ""));
					}

					break;
				case INSCRIPTION_CRECHE:
					// Données Bouchonnées
					rawData.put(NUMERO_ALLOCATAIRE,"1234567890123");
					rawData.put("revenuImposable","15245");
					rawData.put("creche-nomEtablissement","Crèche - Le Jardin");
					break;
				case DEMANDE_TRANSPORT_SCOLAIRE:
					// Données Bouchonnées
					rawData.put(MAP_KEY_FIELD_ENFANT_NOM_PRENOM,
							String.join(" ", sessionUsager.getIdentitePivot().getFamilyName(),
									sessionUsager.getIdentitePivot().getGivenName(),
									"II")
					);
					rawData.put("transportScolaire-nomEtablissement", "Ecole Primaire Jules Verne");
					break;
				case DOSSIER_MARIAGE:
					// Données Bouchonnées
					rawData.put("nationalite","Française");
					break;
				case DEMANDE_AIDE_PONCTUELLE:
				default:
					LOGGER.warn("Aucune donnée supplémentaire récupérée pour la démarche {}", typeDemarche);
			}

			//Check pour les données potentiellements multiples
			DemarcheUtils.updateDataForListValues(rawData);

			return rawData;
		}
		catch( ExecutionException e) {
			throw new UnknownException("ErreurAppelAPIsFranceConnectees",
					String.format("Erreur lors de la récupération des données Franceconnectée pour l'idUsager : %s et le token : %s",
							sessionUsager.getUserId(), sessionUsager.getCurrentToken()),
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
		catch (InterruptedException e) {
			LOGGER.error("L'exécution de la récupération des données Franceconnectées a été intérrompue");
			Thread.currentThread().interrupt();
		}

		return Collections.emptyMap();

	}

	/**
	 * Renvoie les données brutes pour un type de démarche données et une {@link SessionUsager} avec un token valide.
	 * Si le token est invalide, aucune données ne pourront être récupérées.
	 * @param typeDemarche {@link DemarcheEnum} type de la démarche en cours de traitement.
	 * @param sessionUsager {@link SessionUsager} de l'usager liées aux données à récupérer
	 * @param secretsSupplementaires {@link Map} contenant les secrets supplémentaires de l'usager pour la démarche
	 * @return la {@link Map} de {@link String} contenant les données brutes de l'usaqger pour le type de démarche donné
	 */
	public Map<String, List<DonneeUsager>> getDonneesUsagerForDemarche(@NotNull DemarcheEnum typeDemarche,
																	   @NotNull SessionUsager sessionUsager,
																	   Map<String, String> secretsSupplementaires) {

		try {
			Map<String, List<DonneeUsager>> result = DonneeUsagerUtils.getMapFromSituationUsager(new SituationUsager(sessionUsager));
			Map<String, String> rawData = DemarcheUtils.getDataFromIdentitePivot(sessionUsager.getIdentitePivot());
			rawData.put(MAP_KEY_FIELD_SIRET_PARTENAIRE, secretsSupplementaires.get(MAP_KEY_FIELD_SIRET_PARTENAIRE));

			switch (typeDemarche) {
				case CARTE_STATIONNEMENT:
					result.put(DonneeUsagerUtils.CODE_SOURCE_ENTREPRISE,
							DonneeUsagerUtils.getDonneesUsagerFromEntreprise(secretsSupplementaires.get(MAP_KEY_FIELD_SIRET), secretsSupplementaires.get(MAP_KEY_FIELD_RAISON_SOCIALE)));
					result.put(DonneeUsagerUtils.CODE_SOURCE_ANTS,
							DonneeUsagerUtils.getDonneesUsagerFromAnts());
					break;
				case RESTAURATION_SCOLAIRE:
				case INSCRIPTION_ECOLE:
					// Partie non FranceConnectee - nécéssite des données supplémentaires
					String codePostal = secretsSupplementaires.get(MAP_KEY_FIELD_CODE_POSTAL);
					String numeroAllocataire = secretsSupplementaires.get(NUMERO_ALLOCATAIRE);
					if(StringUtils.isNotBlank(codePostal) && StringUtils.isNotBlank(numeroAllocataire)) {
						result.put(DonneeUsagerUtils.CODE_SOURCE_CNAM,
								DonneeUsagerUtils.getDonneeUsagerFromFamille(restClientCnaf.getFamille(numeroAllocataire, codePostal)));
					}

					String numeroFiscal = secretsSupplementaires.get("numeroFiscal");
					String referenceAvisFiscal = secretsSupplementaires.get("referenceAvisFiscal");
					if(StringUtils.isNotBlank(numeroFiscal) && StringUtils.isNotBlank(referenceAvisFiscal)) {
						result.put(DonneeUsagerUtils.CODE_SOURCE_CNAM,
								DonneeUsagerUtils.getDonneeUsagerFromDeclaration(restClientDgfip.getDeclaration(numeroFiscal, referenceAvisFiscal)));
					}

					// Données Bouchonnées
					if(!secretsSupplementaires.containsKey(MAP_KEY_FIELD_CODE_POSTAL) || !secretsSupplementaires.containsKey(NUMERO_ALLOCATAIRE)) {
						result.put(DonneeUsagerUtils.CODE_SOURCE_SELECTION, new ArrayList<>());

						result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
								DonneeUsagerUtils.createDonneeUsager(MAP_KEY_FIELD_ENFANT_NOM_PRENOM,DonneeUsagerUtils.CODE_SOURCE_CNAM,
										String.join(" ", sessionUsager.getIdentitePivot().getFamilyName(),
												sessionUsager.getIdentitePivot().getGivenName(),
												"II"))
						);

						result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
								DonneeUsagerUtils.createDonneeUsager("adresseEnfant",null,
										DemarcheUtils.getAdresseCompleteFromAdresse(sessionUsager.getIdentitePivot().getAddress()))
						);
					}

					break;
				case INSCRIPTION_CRECHE:
					// Données Bouchonnées
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager(NUMERO_ALLOCATAIRE,DonneeUsagerUtils.CODE_SOURCE_CNAM,"1234567890123")
					);
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager("revenuImposable",DonneeUsagerUtils.CODE_SOURCE_DGFIP, "15245")
					);
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager("creche-nomEtablissement",null, "Crèche - Le Jardin")
					);
					break;
				case DEMANDE_TRANSPORT_SCOLAIRE:
					// Données Bouchonnées
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager(MAP_KEY_FIELD_ENFANT_NOM_PRENOM,DonneeUsagerUtils.CODE_SOURCE_CNAM,
									String.join(" ", sessionUsager.getIdentitePivot().getFamilyName(),
											sessionUsager.getIdentitePivot().getGivenName(),
											"II"))
					);
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager("transportScolaire-nomEtablissement",null, "Ecole Primaire Jules Verne")
					);
					break;
				case DOSSIER_MARIAGE:
					// Données Bouchonnées
					result.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION).add(
							DonneeUsagerUtils.createDonneeUsager("nationalite",null, "Française")
					);
					break;
				case DEMANDE_AIDE_PONCTUELLE:
				default:
					LOGGER.warn("Aucune donnée supplémentaire récupérée pour la démarche {}", typeDemarche);
			}

			return result;
		}
		catch( Exception e) {
			throw new UnknownException("ErreurRecuperationDonneesUsager",
					String.format("Erreur lors de la récupération des données de l'usager pour l'idUsager : %s et le token : %s",
							sessionUsager.getUserId(), sessionUsager.getCurrentToken()),
					HttpStatus.INTERNAL_SERVER_ERROR, e);
		}
	}

	/**
	 * Renvoie les données venant de l'API SIV.
	 */
	public String getMockDataFromSIV() throws InterruptedException, ExecutionException {
		CompletableFuture<String> futureDonneesSIV = CompletableFuture.completedFuture("[" +
				"{\"id\": \"vehicule_1\",\"immatriculation\": \"AA-123-AA\",\"modele\": \"Kia Soul\",\"electrique\": false}," +
				"{\"id\": \"vehicule_2\",\"immatriculation\": \"BB-456-BB\",\"modele\": \"Peugeot 208\",\"electrique\": false}," +
				"{\"id\": \"vehicule_3\",\"immatriculation\": \"CC-789-CC\",\"modele\": \"Opel Corsa\",\"electrique\": true}" +
				"]");

		return futureDonneesSIV.get();
	}
}
