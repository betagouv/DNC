package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CommonUtils {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

	/**
	 * Surcharge de la méthode {@link #convertLocalDateToString(LocalDate, String)} pour prévenir les valeurs null.
	 * @param dateToConvert {@link LocalDate} de la date a convertir.
	 * @param pattern {@link String} pattern a utiliser pour convertir la date.
	 * @return null si la date à convertir est null sinon l'objet {@link String} correspondant
	 */
	public static String convertLocalDateToStringNullSafe(LocalDate dateToConvert, String pattern) {
		return (dateToConvert != null) ? convertLocalDateToString(dateToConvert, pattern) : null;

	}

	/**
	 * Convertir un objet {@link LocalDate} en {@link String} avec un pattern cible.
	 *
	 * @param dateToConvert {@link LocalDate} de la date a convertir.
	 * @param pattern {@link String} pattern a utiliser pour convertir la date.
	 * @return l'objet {@link String} correspondant
	 */
	public static String convertLocalDateToString(LocalDate dateToConvert, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return formatter.format(dateToConvert);

	}

	/**
	 * Convertit une date au format {@link String} d'un format source à un format cible.
	 * @param dateToConvert {@link String} date à convertir
	 * @param sourceFormat {@link String} format d'origine de la date
	 * @param targetFormat {@link String} format cible de la date
	 * @return la date au format cible, {@code null} si un des paramètres est null.
	 */
	public static String convertStringDateToNewFormat(String dateToConvert, String sourceFormat, String targetFormat) {
		if(StringUtils.isEmpty(dateToConvert) || StringUtils.isEmpty(sourceFormat) || StringUtils.isEmpty(targetFormat)) {
			return null;
		}
		LocalDate date = LocalDate.parse(dateToConvert, DateTimeFormatter.ofPattern(sourceFormat));

		return convertLocalDateToStringNullSafe(date, targetFormat);
	}

	/**
	 * Mapper de base pour les opérations de lecture/conversion des objets et JSON
	 * @return le {@link ObjectMapper} à utiliser
	 */
	public static ObjectMapper getDefaultMapper() {
		return new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
				.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false)
				;
	}

	/**
	 * Convertit un {@link Object} en JSON. Les erreurs de conversion sont loggés et la méthode renvoie {@code null}.
	 * @param objetToConvert l'objet à convertir
	 * @param fieldName nom du champ qui a affiché en cas d'erreur
	 * @return le code JSON de l'objet sinon {@code null}
	 */
	public static String convertObjectToJson(Object objetToConvert, String fieldName) {
		ObjectMapper objectMapper = getDefaultMapper();
		try {
			return objectMapper.writeValueAsString(objetToConvert);
		}
		catch (JsonProcessingException e ) {
			LOGGER.warn(String.format("Erreur de conversion en JSON pour les données du champ : %s",fieldName), e);
		}
		return null;
	}

	/**
	 * Constructeur par défaut
	 */
	private CommonUtils() {
		// Protection du constructeur par défaut
	}
}
