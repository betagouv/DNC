package fr.gouv.modernisation.dinum.dnc.situationusager.config;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;
import fr.gouv.modernisation.dinum.dnc.situationusager.utils.ModeleJustificatif;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Configurations pour les justificatifs.
 */
@ConfigurationProperties(prefix = "justificatif")
@Configuration
public class JustificatifConfig {

	/**
	 * Liste de la défintions des modèles de Justificatifs
	 */
	List<ModeleJustificatif> modeles = new ArrayList<>();

	/**
	 * Map des sources par champs apparaissant dans les justificatifs
	 */
	Map<String, String> sources = new HashMap<>();

	@Bean
	public MessageSource messageSource() {

		ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
		source.setBasenames("classpath:messages/justificatifs");
		source.setDefaultEncoding("UTF-8");

		return source;
	}

	/**
	 * Constructeur par défaut
	 */
	public JustificatifConfig() {
		// Constructeur par défaut
	}

	/**
	 * Renvoie un {@link Optional} avec le {@link ModeleJustificatif} correspondant au type en paramètre si il existe.
	 * @param typeJustificatif {@link TypeJustificatif} type à rechercher
	 * @return {@link Optional} avec le {@link ModeleJustificatif} correspondant sinon {@link Optional#EMPTY}
	 */
	public Optional<ModeleJustificatif> getModeleFromTypeJustificatif(TypeJustificatif typeJustificatif){
		return modeles.stream()
				.filter(modele -> Objects.equals(typeJustificatif, modele.getType()))
				.findFirst();
	}

	/**
	 * Renvoie la source par défaut configurée pour un champ du justificatif
	 * @param field le code du champ recherché
	 * @return la source configurée sinon {@code null}
	 */
	public String getSourceForField(String field) {
		return sources.getOrDefault(field, null);
	}

	/**
	 * Getter du champ modelesJustificatif
	 *
	 * @return {@link List<ModeleJustificatif>} la valeur du champ modelesJustificatif
	 */
	public List<ModeleJustificatif> getModeles() {
		return modeles;
	}

	/**
	 * Setter du champ modelesJustificatif
	 *
	 * @param modeles valeur à setter
	 */
	public void setModeles(List<ModeleJustificatif> modeles) {
		this.modeles = modeles;
	}

	/**
	 * Getter du champ sourcesFields
	 *
	 * @return {@link Map< String, String>} la valeur du champ sourcesFields
	 */
	public Map<String, String> getSources() {
		return sources;
	}

	/**
	 * Setter du champ sourcesFields
	 *
	 * @param sources valeur à setter
	 */
	public void setSources(Map<String, String> sources) {
		this.sources = sources;
	}
}
