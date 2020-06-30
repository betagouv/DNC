package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import fr.gouv.modernisation.dinum.dnc.situationusager.config.JustificatifConfig;
import fr.gouv.modernisation.dinum.dnc.situationusager.factory.TestDataFactory;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DemarcheEnum;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.SituationUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.ModeleJustificatif;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JustificatifServiceTest {

	private JustificatifService justificatifService = new JustificatifService();

	private TestDataFactory testDataFactory = new TestDataFactory();

	private FetchDataService fetchDataService = new FetchDataService();

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(justificatifService, "frontBaseUrl", "http://dnc-dev.cloudapps.dfp.ovh");
		ReflectionTestUtils.setField(justificatifService, "queryVerificationUrl", "/verif-justificatif?data=");
		ReflectionTestUtils.setField(justificatifService, "secretKey", "02PX05TCRO36HLB9");
		ReflectionTestUtils.setField(justificatifService, "justificatifConfig", getJustificatifConfig());

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames("classpath:messages/justificatifs");
		source.setDefaultEncoding("UTF-8");
		ReflectionTestUtils.setField(justificatifService, "messageSource", source);

	}

	@Test
	public void test(){
		Assertions.assertNotNull(LocalDateTime.parse("16/05/2007T08:30:00", DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")));

	}

	@Test
	public void generationJustificatifEtSortiePDF() throws IOException{

		for(DemarcheEnum demarcheEnum : DemarcheEnum.values() ) {
			DemarcheUsager demarcheUsager = testDataFactory.defaultDemarcheUsager(demarcheEnum, "13002526500013");
			byte[] report = justificatifService.generateJustificatifJasper(demarcheUsager);
			assertNotNull(report);
			FileOutputStream fileOutputStream = new FileOutputStream("target/test-ut-"+demarcheEnum.toString()+".pdf");
			fileOutputStream.write(report);
			fileOutputStream.flush();
			fileOutputStream.close();
		}
	}

	@Test
	public void generationJustificatifAvecDonneeUsagerEtSortiePDF() throws IOException{

		SituationUsager situationUsager = testDataFactory.defaultSituationUsager("openid");

		for(DemarcheEnum demarcheEnum : DemarcheEnum.values() ) {
			DemarcheUsager demarcheUsager = testDataFactory.defaultDemarcheUsager(demarcheEnum, "13002526500013");
			Map<String, List<DonneeUsager>> mapDonneeUsager = testDataFactory.getMapDonneeUsagerForDemarche(demarcheEnum, situationUsager);

			Map<String, String> rawDataFromDU = new HashMap<>();
			mapDonneeUsager.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION)
					.forEach(donneeUsager -> {
						if(donneeUsager.getValeur() != null) {
							rawDataFromDU.put(donneeUsager.getName(), donneeUsager.getValeur());
						}
						else if(CollectionUtils.isNotEmpty(donneeUsager.getListeDonnees())) {
							donneeUsager.getListeDonnees()
									.forEach(donneeObjet ->
											rawDataFromDU.put(donneeUsager.getName(), donneeUsager.getValeur())
									);
						}
					});
			mapDonneeUsager.entrySet().stream()
					.filter(entry -> !StringUtils.equals(DonneeUsagerUtils.CODE_SOURCE_SELECTION, entry.getKey()))
					.map(Map.Entry::getValue)
					.forEach(listDonneeUsager ->
							listDonneeUsager.forEach(donneeUsager -> {
								if(donneeUsager.getValeur() != null) {
									rawDataFromDU.putIfAbsent(donneeUsager.getName(), donneeUsager.getValeur());
								}
								else if(CollectionUtils.isNotEmpty(donneeUsager.getListeDonnees())) {
									donneeUsager.getListeDonnees()
											.forEach(donneeObjet ->
													rawDataFromDU.putIfAbsent(donneeUsager.getName(), donneeUsager.getValeur())
											);
								}
							})
					);
			demarcheUsager.setRawData(rawDataFromDU);

			// Récupération des données

			byte[] report = justificatifService.generateJustificatifJasperWithDonneeUsager(DemarcheUtils.getTypeJustificatifFromDemarcheEnum(demarcheEnum), mapDonneeUsager);
			assertNotNull(report);
			FileOutputStream fileOutputStream = new FileOutputStream("target/test-ut-du-"+demarcheEnum.toString()+".pdf");
			fileOutputStream.write(report);
			fileOutputStream.flush();
			fileOutputStream.close();

			report = justificatifService.generateJustificatifJasper(demarcheUsager);
			assertNotNull(report);
			fileOutputStream = new FileOutputStream("target/test-ut-du-raw-"+demarcheEnum.toString()+".pdf");
			fileOutputStream.write(report);
			fileOutputStream.flush();
			fileOutputStream.close();
		}
	}

	@Test
	public void generationJustificatifTest() {

		DemarcheUsager demarcheUsager = testDataFactory.defaultDemarcheUsager(DemarcheEnum.CARTE_STATIONNEMENT, "13002526500013");

		Justificatif justificatif = justificatifService.generateJustificatif(demarcheUsager);
		assertNotNull(justificatif);
		assertNotNull(justificatif.getContenu());

		demarcheUsager.getDemarche().setCode(DemarcheEnum.DEMANDE_AIDE_PONCTUELLE);
		justificatif = justificatifService.generateJustificatif(demarcheUsager);
		assertNotNull(justificatif);
		assertNotNull(justificatif.getContenu());

		demarcheUsager.getDemarche().setCode(DemarcheEnum.RESTAURATION_SCOLAIRE);
		justificatif = justificatifService.generateJustificatif(demarcheUsager);
		assertNotNull(justificatif);
		assertNotNull(justificatif.getContenu());
	}


	private JustificatifConfig getJustificatifConfig() {
		JustificatifConfig justificatifConfig = new JustificatifConfig();

		ModeleJustificatif modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.IDENTITE_FRANCECONNECT);
		modele.setNomDemarche("identité FranceConnect");
		modele.setLabel("Identité FranceConnect");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"adresseComplete",
				"email",
				"telephone"));
		modele.setFirstAddon(false);
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);

		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.CARTE_STATIONNEMENT);
		modele.setNomDemarche("demande de carte de stationnement résident");
		modele.setLabel("Carte de stationnement résident");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"adresseComplete",
				"telephone",
				"immatriculation",
				"modele",
				"electrique"));
		modele.setChampsListe(Collections.singletonList("vehicules"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Informations complémentaires");
		modele.setChampsFirtsAddon(Arrays.asList("siret","raisonSociale"));
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);


		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.INSCRIPTION_CRECHE);
		modele.setNomDemarche("inscription à la crèche");
		modele.setLabel("Inscription à la Crèche");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"revenuImposable",
				"numeroAllocataire",
				"adresseComplete",
				"telephone"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Informations complémentaires");
		modele.setChampsFirtsAddon(Arrays.asList("email","creche-nomEtablissement"));
		modele.setSecondAddon(true);
		modele.setLabelSecondAddon("Documents à joindres");
		modele.setChampsSecondAddon(Collections.singletonList("documentsAJoindre"));
		modele.setValeurDocumentsAJoindre("Certificat de grossesse, carnet de santé de l'enfant et copie des 3 derniers salaires");
		justificatifConfig.getModeles().add(modele);


		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.INSCRIPTION_ECOLE);
		modele.setNomDemarche("inscription à l'école");
		modele.setLabel("Inscription à l'école");
		modele.setChamps(Arrays.asList(
				"enfant-nomPrenom",
				"adresseEnfant",
				"nomParent",
				"prenomParent",
				"adresseParent",
				"telephone"));
		modele.setChampsListe(Collections.singletonList("enfants"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Documents à joindres");
		modele.setChampsFirtsAddon(Collections.singletonList("documentsAJoindre"));
		modele.setValeurDocumentsAJoindre("Historique des vaccins");
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);

		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.DOSSIER_MARIAGE);
		modele.setNomDemarche("dossier de mariage");
		modele.setLabel("Dossier de Mariage");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"adresseComplete",
				"nationalite"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Documents à joindres");
		modele.setChampsFirtsAddon(Collections.singletonList("documentsAJoindre"));
		modele.setValeurDocumentsAJoindre("Acte de naissance");
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);


		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.DEMANDE_TRANSPORT_SCOLAIRE);
		modele.setNomDemarche("demande de transport scolaire");
		modele.setLabel("Demande de transport scolaire");
		modele.setChamps(Arrays.asList(
				"enfant-nomPrenom",
				"quotientFamilial",
				"adresseComplete"));
		modele.setChampsListe(Collections.singletonList("enfants"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Informations complémentaires");
		modele.setChampsFirtsAddon(Arrays.asList("transportScolaire-nomEtablissement","email"));
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);

		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.AUTORISATION_STATIONNEMENT_DEMENAGEMENT);
		modele.setNomDemarche("autorisation de stationnement (déménagement)");
		modele.setLabel("Autorisation de Stationnemnet (Déménagement)");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"adresseComplete"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Informations complémentaires");
		modele.setChampsFirtsAddon(Arrays.asList("email","telephone"));
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);

		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.OPERATION_TRANQUILITE_VACANCES);
		modele.setNomDemarche("inscription à l'opération Tranquilité Vacances");
		modele.setLabel("Opération Tranquilité Vacances");
		modele.setChamps(Arrays.asList(
				"nom",
				"prenoms",
				"adresseComplete",
				"email",
				"telephone"));
		modele.setFirstAddon(false);
		modele.setSecondAddon(false);
		justificatifConfig.getModeles().add(modele);


		modele = new ModeleJustificatif();
		modele.setType(TypeJustificatif.RESTAURATION_SCOLAIRE);
		modele.setNomDemarche("inscription à la restauration scolaire");
		modele.setLabel("Inscription à la restauration scolaire");
		modele.setChamps(Arrays.asList(
				"enfant-nomPrenom",
				"adresseEnfant",
				"nomParent",
				"prenomParent",
				"adresseParent",
				"telephone"));
		modele.setChampsListe(Collections.singletonList("enfants"));
		modele.setFirstAddon(true);
		modele.setLabelFirstAddon("Documents à joindres");
		modele.setChampsFirtsAddon(Collections.singletonList("documentsAJoindre"));
		modele.setValeurDocumentsAJoindre("Attestation d'inscription à l'établissement scolaire");
		modele.setSecondAddon(false);

		// Pas de Déclaration de mini moto ou de demande d'aide ponctuelle

		// Sources configurées
		justificatifConfig.getSources().put("nom", "FC");
		justificatifConfig.getSources().put("prenoms", "FC");
		justificatifConfig.getSources().put("adresseComplete", "FC");
		justificatifConfig.getSources().put("email", "FC");
		justificatifConfig.getSources().put("telephone", "FC");
		justificatifConfig.getSources().put("immatriculation", "ANTS");
		justificatifConfig.getSources().put("modele", "ANTS");
		justificatifConfig.getSources().put("electrique", "ANTS");
		justificatifConfig.getSources().put("siret", "ENTREPRISE");
		justificatifConfig.getSources().put("raisonSociale", "ENTREPRISE");
		justificatifConfig.getSources().put("numeroAllocataire", "CNAM");
		justificatifConfig.getSources().put("quotientFamilial", "CNAM");
		justificatifConfig.getSources().put("nombreEnfantACharge", "CNAM");
		justificatifConfig.getSources().put("enfants", "CNAM");
		justificatifConfig.getSources().put("enfant-nomPrenom", "CNAM");
		justificatifConfig.getSources().put("allocataires", "CNAM");
		justificatifConfig.getSources().put("revenuImposable", "DGFIP");
		justificatifConfig.getSources().put("revenuFiscalReference", "DGFIP");
		justificatifConfig.getSources().put("nombrePersonnesCharge", "DGFIP");
		justificatifConfig.getSources().put("montantImpot", "DGFIP");
		justificatifConfig.getSources().put("nombrePart", "DGFIP");
		justificatifConfig.getSources().put("nomParent", "FC");
		justificatifConfig.getSources().put("prenomParent", "FC");
		justificatifConfig.getSources().put("adresseEnfant", "FC");
		justificatifConfig.getSources().put("adresseParent", "FC");

		return justificatifConfig;
	}
}
