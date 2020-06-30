package fr.gouv.modernisation.dinum.dnc.situationusager.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import fr.gouv.modernisation.dinum.dnc.situationusager.config.JustificatifConfig;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.DonneeUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.redis.data.DemarcheUsager;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.CipherUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DemarcheUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.DonneeUsagerUtils;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.ModeleJustificatif;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.svg.SVGGraphics2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JustificatifService {

	/**
	 * Logger {@link Logger}
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(JustificatifService.class);

	/**
	 * Répertoire des rapports Jasper
	 */
	private static final String REPORT_JASPER_DIRECTORY="justificatifs/reports";

	/**
	 * Sous-Rapport Jasper permettant d'afficher des champs du justificatifs
	 */
	public static final String SUBREPORT_ADDON_JASPER = REPORT_JASPER_DIRECTORY + "/champ_texte_avec_logo.jasper";

	/**
	 * Clé de la valeur dans la Map d'un champ du justificatif
	 */
	public static final String MAP_FIELD_KEY_VALEUR = "valeur";

	/**
	 * Clé du libellé dans la Map d'un champ du justificatif
	 */
	public static final String MAP_FIELD_KEY_LIBELLE = "libelle";

	/**
	 * Clé de la source dans la Map d'un champ du justificatif.
	 * Correspond à un des codes de la Map des logos.
	 */
	public static final String MAP_FIELD_KEY_CODE_SOURCE = "codeSource";

	/**
	 * Format des dates dans le rapport
	 */
	public static final String FORMAT_DATE = "dd/MM/yyyy";

	/**
	 * Libellé par défaut des champs inconnus
	 */
	public static final String CHAMP_INCONNU = "CHAMP INCONNU";

	/**
	 * Clé de l'entête dans la Map d'un champ du justificatif
	 */
	public static final String MAP_FIELD_KEY_ENTETE = "entete";

	/**
	 * Clé du paramètre pour le 1er Sous rapport supplémentaire.
	 */
	public static final String PARAMETER_KEY_SUBREPORT_ADDONS = "SUBREPORT_ADDONS";

	/**
	 * Clé du paramètre pour les données des champs du 1er Sous rapport supplémentaire.
	 */
	public static final String PARAMETER_KEY_DATA_ADDONS = "DATA_ADDONS";

	/**
	 * URL pour la vérification des données du  QR_CODE
	 */
	@Value("${dnc.front.url}")
	private String frontBaseUrl;

	/**
	 * URL pour la vérification des données du  QR_CODE
	 */
	@Value("${dnc.front.queryVerificationUrl}")
	private String queryVerificationUrl;

	/**
	 * Nombre de mois de validité du justificatif après génération
	 */
	@Value("${dnc.justificatif.nbrMoisValidite}")
	private Integer justificatifNbrMoisValidite = 3;

	/**
	 * Secret d'encryptage des données dans le QR Code
	 */
	@Value("${dnc.secret}")
	private String secretKey;

	/**
	 * Configuration des Justificatifs
	 */
	@Autowired
	private JustificatifConfig justificatifConfig;

	/**
	 * Sources des Messages dans les justificatifs
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Génère un {@link Justificatif} à partir d'un objet {@link DemarcheUsager}
	 * @param demarcheUsager l'objet {@link DemarcheUsager} contenant les données à utiliser dans la génération
	 * @return l'objet {@link Justificatif} correspondant
	 */
	public Justificatif generateJustificatif(DemarcheUsager demarcheUsager) {
		Justificatif justificatif = new Justificatif();

		byte[] justificatifBinaire = generateJustificatifJasper(demarcheUsager);
		justificatif.setContenu(Base64.getEncoder().encodeToString(justificatifBinaire));

		justificatif.setFilename(getFilenameFromTypeDemarche(DemarcheUtils.getTypeJustificatifFromDemarcheEnum(demarcheUsager.getDemarche().getCode())));
		justificatif.setFiletype("pdf");
		justificatif.setId(UUID.randomUUID().toString());
		justificatif.setIdPartenaire(demarcheUsager.getDemarche().getSiretPartenaire());
		justificatif.setSize(FileUtils.byteCountToDisplaySize(justificatifBinaire.length));

		return justificatif;
	}

	/**
	 * Génère un {@link Justificatif} à partir d'un objet {@link DemarcheUsager}
	 * @return l'objet {@link Justificatif} correspondant
	 */
	public Justificatif generateJustificatif(TypeJustificatif typeJustificatif, Map<String, List<DonneeUsager>> donnees) {
		Justificatif justificatif = new Justificatif();

		byte[] justificatifBinaire = generateJustificatifJasperWithDonneeUsager(typeJustificatif, donnees);
		justificatif.setContenu(Base64.getEncoder().encodeToString(justificatifBinaire));

		justificatif.setFilename(getFilenameFromTypeDemarche(typeJustificatif));
		justificatif.setFiletype("pdf");
		justificatif.setId(UUID.randomUUID().toString());
		justificatif.setSize(FileUtils.byteCountToDisplaySize(justificatifBinaire.length));

		return justificatif;
	}

	/**
	 * Génère un justificatif à partir de
	 * @param demarcheUsager l'objet {@link DemarcheUsager} contenant les données à utiliser dans la génération
	 * @return les données binaires du justificatifs au format PDF
	 */
	public byte[] generateJustificatifJasper(DemarcheUsager demarcheUsager) {
		TypeJustificatif typeJustificatif = DemarcheUtils.getTypeJustificatifFromDemarcheEnum(demarcheUsager.getDemarche().getCode());
		Optional<ModeleJustificatif> optional = justificatifConfig.getModeleFromTypeJustificatif(typeJustificatif);
		if(optional.isEmpty()) {
			LOGGER.error("Aucun modèle de justificatif trouvé pour le type {} ", typeJustificatif);
			return new byte[0];
		}

		try {
			return generateJustificatifPDF(optional.get(), demarcheUsager.getRawData());
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors de la génération du Justificatif", e);
		}

		return new byte[0];
	}

	/**
	 * Génère les données binaires d'un Justificatif à partir des paramètres.
	 * Renvoie un binaire vide si les données nécessaires ne sont pas présentes.
	 * @param typeJustificatif {@link TypeJustificatif} type du justificatif à générer
	 * @param donnees {@link Map} de {@link List} de {@link DonneeUsager} représentant les données et la sélection de l'usager
	 * @return les données binaires du justificatif généré
	 */
	public byte[] generateJustificatifJasperWithDonneeUsager(TypeJustificatif typeJustificatif, Map<String, List<DonneeUsager>> donnees) {
		Optional<ModeleJustificatif> optional = justificatifConfig.getModeleFromTypeJustificatif(typeJustificatif);
		if(optional.isEmpty()) {
			LOGGER.error("Aucun modèle de justificatif trouvé pour le type {} ", typeJustificatif);
			return new byte[0];
		}

		try {
			return generateJustificatifPDFWithMapDonneeUsager(optional.get(), donnees);
		}
		catch (Exception e) {
			LOGGER.error(String.format("Erreur lors de la génération du Justificatif : Type de Justificatif : %s",typeJustificatif.toString()), e);
		}

		return new byte[0];
	}

	/**
	 * Génère un justificatif PDF à partir des données en paramètre.
	 * @param modeleJustificatif {@link ModeleJustificatif} modèle du justificatif à générer
	 * @param donneesBrutes {@link Map} des données à utiliser pour le justificatif
	 * @return les données binaires du PDF du justificatif
	 * @throws FileNotFoundException levée si le fichier Jasper n'est pas trouvé
	 * @throws WriterException levée si il y a une erreur
	 * @throws JRException levée en cas d'erreur sur Jasperreport
	 */
	private byte[] generateJustificatifPDF(ModeleJustificatif modeleJustificatif, Map<String, String> donneesBrutes) throws FileNotFoundException, WriterException, JRException {
		String jasperFile = REPORT_JASPER_DIRECTORY+"/justificatif_plus_subreport_fields.jasper";
		InputStream inputStreamTemplate = this.getClass()
				.getClassLoader().getResourceAsStream(jasperFile);
		if(inputStreamTemplate == null) {
			throw new FileNotFoundException("Le fichier template n'a pas pu être lu : "+jasperFile );
		}

		//Création de la Map des données de remplacements
		Map<String, String> mapSources = initMapJustificatif(modeleJustificatif);

		// Ajout de toutes les clés nécessaires aux justificatifs à partir des données brutes.
		modeleJustificatif.getAllChampsForGeneration()
				.forEach(keyInMap -> mapSources.putIfAbsent(keyInMap, StringUtils.defaultIfEmpty(donneesBrutes.get(keyInMap), "")));

		// Génération du QR Code
		LOGGER.debug("Champs demandés pour le modèles {} : {}", modeleJustificatif.getType(), modeleJustificatif.getAllChampsForGeneration());
		String urlQrCode = frontBaseUrl +
				queryVerificationUrl +
				modeleJustificatif.getAllChampsForGeneration()
						.stream()
						.filter(keyInMap -> StringUtils.isNotEmpty(mapSources.get(keyInMap)))
						.map(keyInMap -> CipherUtils.encrypt(mapSources.get(keyInMap),secretKey))
						.collect(Collectors.joining(","));
		LOGGER.debug("URL du QR Code généré : Type Justificaitf : {}, Taille URL : {}, URL : {}", modeleJustificatif.getType(), urlQrCode.length(), urlQrCode);

		//Documents à joindres
		if(StringUtils.isNotBlank(modeleJustificatif.getValeurDocumentsAJoindre())) {
			mapSources.put("documentsAJoindre",modeleJustificatif.getValeurDocumentsAJoindre());
		}

		// Paramètres du Jasper
		Map<String, Object> parameters = initMapParameters(mapSources, urlQrCode);

		List<Map<String,String>> listDataFields = new ArrayList<>();
		modeleJustificatif.getChamps()
				.forEach(keyInMap -> listDataFields.add(getMapForFieldFromRawData(
						mapSources, justificatifConfig.getSourceForField(keyInMap), keyInMap)) );
		parameters.put("DATA_FIELDS",  new JRBeanCollectionDataSource(listDataFields));

		// liste des données des suppléments
		List<Map<String,String>> listDataFirstAddon = new ArrayList<>();
		List<Map<String,String>> listDataSecondAddon = new ArrayList<>();
		if(modeleJustificatif.isFirstAddon()) {
			modeleJustificatif.getChampsFirtsAddon()
					.forEach(keyInMap -> listDataFirstAddon.add(getMapForFieldFromRawData(
							mapSources, justificatifConfig.getSourceForField(keyInMap), keyInMap)) );
		}
		if(modeleJustificatif.isSecondAddon()) {
			// Récupération des données du 2nd Addon
			modeleJustificatif.getChampsSecondAddon()
					.forEach(keyInMap -> listDataSecondAddon.add(getMapForFieldFromRawData(
							mapSources, justificatifConfig.getSourceForField(keyInMap), keyInMap)) );
		}

		// Gestion des suppléments
		handleAddons(modeleJustificatif, parameters, listDataFirstAddon, listDataSecondAddon);

		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(Collections.singletonList(mapSources));

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStreamTemplate);
		return fillAndExportJasperReport(jasperReport, parameters, datasource);
	}

	/**
	 * Génère un justificatif PDF à partir des données en paramètre.
	 * @param modeleJustificatif {@link ModeleJustificatif} modèle du justificatif à générer
	 * @param donnees {@link Map} de {@link List} de {@link DonneeUsager} représentant les données et la sélection de l'usager
	 * @return les données binaires du PDF du justificatif
	 * @throws FileNotFoundException levée si le fichier Jasper n'est pas trouvé
	 * @throws WriterException levée si il y a une erreur
	 * @throws JRException levée en cas d'erreur sur Jasperreport
	 */
	private byte[] generateJustificatifPDFWithMapDonneeUsager(ModeleJustificatif modeleJustificatif, Map<String, List<DonneeUsager>> donnees) throws FileNotFoundException, WriterException, JRException {
		String jasperFile = REPORT_JASPER_DIRECTORY+"/justificatif_plus_subreport_fields.jasper";
		InputStream inputStreamTemplate = this.getClass()
				.getClassLoader().getResourceAsStream(jasperFile);
		if(inputStreamTemplate == null) {
			throw new FileNotFoundException("Le fichier template n'a pas pu être lu : "+jasperFile );
		}

		//Création de la Map des données basiques du rapports
		Map<String, String> mapSources = initMapJustificatif(modeleJustificatif);
		JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(Collections.singletonList(mapSources));

		// Création de la liste des données pour la zone principale du rapport à partir des éléments sélectionnés
		List<DonneeUsager> listDonneesSource = new ArrayList<>(donnees.get(DonneeUsagerUtils.CODE_SOURCE_SELECTION));

		// Ajout des champs autres que les sélections et nécessaire au justificatif
		donnees.entrySet().stream()
				.filter(entry -> !StringUtils.equals(DonneeUsagerUtils.CODE_SOURCE_SELECTION, entry.getKey()))
				.map(Map.Entry::getValue)
				// Pour chaque List de DonneeUsager : Filtre puis ajout à listDonneesSource
				.forEach(listDonneeUsager ->
					listDonneeUsager.stream()
							.filter(donneeUsager ->
									modeleJustificatif.getAllChampsForGeneration().contains(donneeUsager.getName())
											&& listDonneesSource.stream().map(DonneeUsager::getName).noneMatch(existingName -> StringUtils.equals(existingName, donneeUsager.getName()))
							)
							.forEach(listDonneesSource::add)
				);

		// Génération du QR Code à partir de la liste des données source
		LOGGER.debug("Champs demandés pour le modèles {} : {}", modeleJustificatif.getType(), modeleJustificatif.getAllChampsForGeneration());
		String urlQrCode = frontBaseUrl +
				queryVerificationUrl +
				modeleJustificatif.getAllChampsForGeneration()
						.stream()
						.filter(keyInMap -> getDonneeUsagerFromName(listDonneesSource, keyInMap) != null)
						.filter(keyInMap -> StringUtils.isNotEmpty(getDonneeUsagerFromName(listDonneesSource, keyInMap).getValeur()))
						.map(keyInMap ->
							CipherUtils.encrypt(StringUtils.trimToEmpty(getDonneeUsagerFromName(listDonneesSource, keyInMap).getValeur()), secretKey)
							)
						.collect(Collectors.joining(","));
		LOGGER.debug("URL du QR Code généré : Type Justificaitf : {}, Taille URL : {}, URL : {}", modeleJustificatif.getType(), urlQrCode.length(), urlQrCode);

		// Ajout des Documents à joindres si ils existent
		if(StringUtils.isNotBlank(modeleJustificatif.getValeurDocumentsAJoindre())) {
			listDonneesSource.add(DonneeUsagerUtils.createDonneeUsager("documentsAJoindre", null, modeleJustificatif.getValeurDocumentsAJoindre()));
		}

		// Paramètres du Jasper
		Map<String, Object> parameters = initMapParameters(mapSources, urlQrCode);

		List<Map<String,String>> listDataFields = new ArrayList<>();
		modeleJustificatif.getChamps()
				.forEach(keyInMap ->
						listDataFields.add(getDonnneeUsagerAsMap(getDonneeUsagerFromName(listDonneesSource, keyInMap)))
				);
		parameters.put("DATA_FIELDS",  new JRBeanCollectionDataSource(listDataFields));

		// liste des données des suppléments
		List<Map<String,String>> listDataFirstAddon = new ArrayList<>();
		List<Map<String,String>> listDataSecondAddon = new ArrayList<>();
		if(modeleJustificatif.isFirstAddon()) {
			modeleJustificatif.getChampsFirtsAddon()
					.forEach(keyInMap ->
							listDataFirstAddon.add(getDonnneeUsagerAsMap(getDonneeUsagerFromName(listDonneesSource, keyInMap)))
					);
		}
		if(modeleJustificatif.isSecondAddon()) {
			// Récupération des données du 2nd Addon
			modeleJustificatif.getChampsSecondAddon()
					.forEach(keyInMap ->
							listDataSecondAddon.add(getDonnneeUsagerAsMap(getDonneeUsagerFromName(listDonneesSource, keyInMap)))
					);
		}

		// Gestion des suppléments
		handleAddons(modeleJustificatif, parameters, listDataFirstAddon, listDataSecondAddon);

		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStreamTemplate);
		return fillAndExportJasperReport(jasperReport, parameters, datasource);
	}

	/**
	 * Initialise la {@link Map} des paramètres du rapport Jasper.
	 * @param mapSources {@link Map} des données de bases du rapport Jasper
	 * @param urlQrCode {@link String} URL à encoder dans le QR Code
	 * @return la {@link Map} des paramètres du rapport Jasper à utiliser
	 * @throws WriterException levée en cas d'erreur à la construction du QR Code
	 */
	private Map<String, Object> initMapParameters(Map<String, String> mapSources, String urlQrCode) throws WriterException {
		Map<String, Object> parameters = new HashMap<>();
		// Génération du SVG représentant le QR Code et ajout dans les paramètre du rapport
		parameters.put("QR_CODE", getQrCodeAsSvgString(urlQrCode));
		parameters.put("SUBREPORT_TO_USE", SUBREPORT_ADDON_JASPER);
		parameters.put("MAP_LOGOS", getMapLogos());

		parameters.put("DATA", new JRBeanCollectionDataSource(Collections.singletonList(mapSources)));
		return parameters;
	}

	/**
	 * Initialise la {@link Map} d'un justificatif avec les champs de base présent dans le rapport.
	 * @param modeleJustificatif {@link ModeleJustificatif} modèle du justificatif à générer
	 * @return la {@link Map} avec les champs basiques d'un justificatif
	 */
	private Map<String, String> initMapJustificatif(ModeleJustificatif modeleJustificatif) {
		Map<String, String> mapSources = new HashMap<>();
		mapSources.put("dateCreation", LocalDate.now().format(DateTimeFormatter.ofPattern(FORMAT_DATE)));
		mapSources.put("dateFinValidite", LocalDate.now().plusMonths(justificatifNbrMoisValidite).format(DateTimeFormatter.ofPattern(FORMAT_DATE)));
		mapSources.put("nomDemarche", modeleJustificatif.getNomDemarche());
		return mapSources;
	}

	/**
	 * Renvoie l'objet {@link DonneeUsager} correspondant à un nom donné sinon {@code null}
	 * @param list {@link List} liste des {@link DonneeUsager} connus
	 * @param name {@link String} nom de la donnée recherchée
	 * @return l'objet {@link DonneeUsager} correspondant sinon {@code null}
	 */
	private DonneeUsager getDonneeUsagerFromName(List<DonneeUsager> list, String name) {
		return CollectionUtils.emptyIfNull(list).stream()
				.filter(Objects::nonNull)
				.filter(donneeUsager -> StringUtils.equals(name, donneeUsager.getName()))
				.findFirst().orElse(null);
	}

	/**
	 * Renvoie la {@link Map} correspondant à un objet {@link DonneeUsager}.
	 * La map obtenue est une map compatible avec le sous rapport des justificatifs.
	 * Si donneeUsager vaut {@code null}, renvoie une Map avec une valeur et une source vides et un libellé par défaut.
	 * @param donneeUsager {@link DonneeUsager} objet à convertir
	 * @return la {@link Map} correspondante
	 */
	private Map<String,String> getDonnneeUsagerAsMap(DonneeUsager donneeUsager) {
		Map<String, String> mapField = new HashMap<>();

		if (Objects.isNull(donneeUsager)) {
			mapField.put(MAP_FIELD_KEY_CODE_SOURCE, StringUtils.EMPTY);
			mapField.put(MAP_FIELD_KEY_LIBELLE, messageSource.getMessage("champInconnu",null, CHAMP_INCONNU, Locale.getDefault()));
			mapField.put(MAP_FIELD_KEY_VALEUR, StringUtils.EMPTY);
			return mapField;
		}

		if(StringUtils.isNotBlank(donneeUsager.getCodeSource())) {
			mapField.put(MAP_FIELD_KEY_CODE_SOURCE, donneeUsager.getCodeSource());
		}
		mapField.put(MAP_FIELD_KEY_LIBELLE, messageSource.getMessage(donneeUsager.getName(),null, CHAMP_INCONNU, Locale.getDefault()));
		mapField.put(MAP_FIELD_KEY_VALEUR, donneeUsager.getValeur());

		return mapField;
	}

	/**
	 * Renvoie une {@link Map} compatible avec le sous rapport des champs Jasper.
	 * La map contient 3 clés : codeSource, libelle et valeur.
	 * Si {@code keyInMap} n'existe pas dans la {@link Map} {@code rawData}, alors la chaîne vide est ajouté dans la
	 * {@link Map} finale.
	 * @param rawData {@link Map} des données sources du rapport
	 * @param codeSource {@link String} code de la source de la valeur
	 * @param keyInMap {@link String} clé dans la map des données, permet de retrouver le libellédu champ
	 * @return la {@link Map} correspondant au champ demandé.
	 */
	private Map<String, String> getMapForFieldFromRawData(Map<String, String> rawData, String codeSource, String keyInMap) {
		Map<String, String> mapField = new HashMap<>();

		if(StringUtils.isNotBlank(codeSource)) {
			mapField.put(MAP_FIELD_KEY_CODE_SOURCE, codeSource);
		}
		mapField.put(MAP_FIELD_KEY_LIBELLE, messageSource.getMessage(keyInMap,null, CHAMP_INCONNU, Locale.getDefault()));
		mapField.put(MAP_FIELD_KEY_VALEUR, rawData.getOrDefault(keyInMap,""));

		return mapField;
	}

	/**
	 * Génère le {@link String} représentant l'image SVG du QR Code avec pour valeur l'URL en paramètre.
	 * @param urlQrCode url à encoder dans le QR Code
	 * @return la chaîne {@link String} de l'image SVG correpsondante.
	 * @throws WriterException levée lors d'une
	 */
	private String getQrCodeAsSvgString(String urlQrCode) throws WriterException {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
		hints.put(EncodeHintType.MARGIN, 4);

		BitMatrix bitMatrix = qrCodeWriter.encode(urlQrCode, BarcodeFormat.QR_CODE, 156, 156, hints);

		int crunchifyWidth = bitMatrix.getWidth();
		int crunchifyHeight = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(crunchifyWidth,
				crunchifyHeight, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, crunchifyWidth, crunchifyHeight);
		graphics.setColor(Color.BLACK);
		for (int i = 0; i < crunchifyWidth; i++) {
			for (int j = 0; j < crunchifyHeight; j++) {
				if (bitMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		SVGGraphics2D g2 = new SVGGraphics2D(156, 156);
		g2.drawImage(image, 0,0, 156, 156, null);
		return g2.getSVGElement(null, true, null, null, null);
	}

	/**
	 * Renseigne et exporte le JasperReport au format PDF et renvoie les données binaires du PDF.
	 * @param jasperReport {@link JasperReport} a utilisé pour la génération
	 * @param parameters {@link Map} des paramètres du rapport Jasper
	 * @param datasource {@link JRBeanCollectionDataSource} datasource des données du rapport Jasper
	 * @return données binaire du rapport Jasper
	 * @throws JRException levée en cas d'exception au remplissage du rapport Jasper
	 */
	private byte[] fillAndExportJasperReport(JasperReport jasperReport, Map<String, Object> parameters, JRBeanCollectionDataSource datasource) throws JRException {
		JasperPrint jasperPrint = JasperFillManager.fillReport(
				jasperReport, parameters, datasource);
		JRPdfExporter exporter = new JRPdfExporter();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(
				new SimpleOutputStreamExporterOutput(byteArrayOutputStream));

		SimplePdfReportConfiguration reportConfig
				= new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);

		SimplePdfExporterConfiguration exportConfig
				= new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("DINUM - DNC");
		exportConfig.setEncrypted(false);
		exportConfig.setAllowedPermissionsHint("PRINTING");

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Renvoie le nom de fichier pour le justificatif généré
	 * @param type {@link TypeJustificatif} type de justificatif.
	 * @return le nom du fichier
	 */
	private String getFilenameFromTypeDemarche(TypeJustificatif type) {
		String prefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String suffix = ".pdf";
		switch (type) {
			case CARTE_STATIONNEMENT:
				return prefix + "_Demande_Carte_Stationnement" + suffix;
			case DECLARATION_MINI_MOTO:
				return prefix + "_Declaration_Mini_moto" + suffix;
			case OPERATION_TRANQUILITE_VACANCES:
				return prefix + "_Inscription_Tranquilite_Vacances" + suffix;
			case DEMANDE_AIDE_PONCTUELLE:
				return prefix + "_Demande_Aide_Ponctuelle" + suffix;
			case RESTAURATION_SCOLAIRE:
				return prefix + "_Inscription_Restauration_Scolaire" + suffix;
			default:
				return prefix + "_justificatif_DNC" + suffix;
		}
	}

	/**
	 * Renvoie la {@link Map} des Logos.
	 * La clé correspond au code de la source et la valeur correspond au chemin vers le logo.
	 * @return {@link Map} avec les codes de sources connus.
	 */
	private Map<String, String> getMapLogos() {
		Map<String, String> mapLogos = new HashMap<>();
		mapLogos.put(DonneeUsagerUtils.CODE_SOURCE_FRANCECONNECT, "justificatifs/images/logo_franceconnet.png");
		mapLogos.put(DonneeUsagerUtils.CODE_SOURCE_ANTS, "justificatifs/images/logo_ants.png");
		mapLogos.put(DonneeUsagerUtils.CODE_SOURCE_ENTREPRISE, "justificatifs/images/logo-api_entreprise.svg");
		mapLogos.put(DonneeUsagerUtils.CODE_SOURCE_CNAM, "justificatifs/images/logo_cnam.jpg");
		mapLogos.put(DonneeUsagerUtils.CODE_SOURCE_DGFIP, "justificatifs/images/logo-dgfip.jpg");
		return mapLogos;
	}

	/**
	 * Gère l'ajout du 2nd supplément de champs aux paramètres du rapport Jasper.
	 * @param modeleJustificatif {@link ModeleJustificatif} modèle du justificatif à générer
	 * @param parameters {@link Map} des paramètres du rapport Jasper
	 * @param listDataFirstAddon {@link List} liste des {@link Map} représentant les champs du 1er supplément
	 * @param listDataSecondAddon {@link List} liste des {@link Map} représentant les champs du 2nd supplément
	 */
	private void handleAddons(ModeleJustificatif modeleJustificatif, Map<String, Object> parameters, List<Map<String, String>> listDataFirstAddon, List<Map<String, String>> listDataSecondAddon) {
		// Si jamais le 1er Addon est vide on réutilise le 1er
		String secondAddonReportParameter = "SUBREPORT_ADDONS_2";
		String secondAddonDataParameter = "DATA_ADDONS_2";
		if(modeleJustificatif.isFirstAddon()) {

			// Check pour vérifier qu'il y a bien des données dans l'addon
			boolean firstAddonIsNotEmpty = listDataFirstAddon.stream().anyMatch(mapField -> StringUtils.isNotBlank(mapField.get(MAP_FIELD_KEY_VALEUR)));

			if(firstAddonIsNotEmpty) {
				// Ajout de l'entête pour le 1er élément
				if(!listDataFirstAddon.isEmpty()) {
					listDataFirstAddon.get(0).put(MAP_FIELD_KEY_ENTETE, modeleJustificatif.getLabelFirstAddon());
				}

				// Ajout des paramètres
				parameters.put(PARAMETER_KEY_SUBREPORT_ADDONS, SUBREPORT_ADDON_JASPER);
				parameters.put(PARAMETER_KEY_DATA_ADDONS, new JRBeanCollectionDataSource(listDataFirstAddon));
			}
			else {
				// Réutilisation des paramètres pour le 2nd Addon
				secondAddonReportParameter = PARAMETER_KEY_SUBREPORT_ADDONS;
				secondAddonDataParameter = PARAMETER_KEY_DATA_ADDONS;
			}
		}
		if(modeleJustificatif.isSecondAddon()) {
			// Check pour vérifier qu'il y a bien des données dans l'addon
			boolean secondAddonIsNotEmpty = listDataSecondAddon.stream().anyMatch(mapField -> StringUtils.isNotBlank(mapField.get(MAP_FIELD_KEY_VALEUR)));

			// Si la liste des champs n'est pas vide et qu'il existe bien des valeurs dans au moins 1 champ
			if (secondAddonIsNotEmpty) {
				// Ajout de l'entête pour le 1er élément
				listDataSecondAddon.get(0).put(MAP_FIELD_KEY_ENTETE, modeleJustificatif.getLabelSecondAddon());
				// Ajout des paramètres
				parameters.put(secondAddonReportParameter, SUBREPORT_ADDON_JASPER);
				parameters.put(secondAddonDataParameter, new JRBeanCollectionDataSource(listDataSecondAddon));
			}

		}
	}

}
