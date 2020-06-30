package fr.gouv.modernisation.dinum.dnc.situationusager.redis.data;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Demarche;
import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.Justificatif;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.UUID;

/**
 * Entité représentant les informations d'une démarche d'un usager.
 *
 */
@RedisHash(value = DemarcheUsager.NAMESPACE_REDIS)
public class DemarcheUsager {

	/**
	 * Namespace dans Redis
	 */
	public static final String NAMESPACE_REDIS = "DemarcheUsager";

	/**
	 * Identifiant unique du citoyen, créé à partir de son identité pivot
	 */
	@Id
	private UUID id;

	/**
	 * Les données de la démarche réalisée par l'usager
	 */
	private Demarche demarche;

	/**
	 * Identifiant de l'usager dans le système DNC.
	 */
	private String idUsager;

	/**
	 * Flag indiquant si la Démarche est modifiable.
	 */
	private boolean modifiable = false;

	/**
	 * Données brutes de la démarches
	 */
	private Map<String, String> rawData;

	/**
	 * Justificatif de la démarche
	 */
	private Justificatif justificatif;

	/**
	 * Constructeur par défaut
	 */
	private DemarcheUsager() {
		// Constructeur par défaut pour permettre des tests
	}

	/**
	 * Constructeur de la classe {@link DemarcheUsager}. L'objet doit se baser sur une démarche existante.
	 * @param demarche la session de l'usager
	 */
	public DemarcheUsager(Demarche demarche) {
		Assert.notNull(demarche, "Les données de la démarche ne peuvent pas être null");

		this.id = UUID.randomUUID();
		demarche.setId(id);
		this.demarche = demarche;
	}

	/**
	 * Getter du champ id
	 * @return {@link UUID} la valeur du champ id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Setter du champ id
	 *
	 * @param id valeur à setter
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Getter du champ demarche
	 * @return {@link Demarche} la valeur du champ demarche
	 */
	public Demarche getDemarche() {
		return demarche;
	}

	/**
	 * Setter du champ demarche
	 *
	 * @param demarche valeur à setter
	 */
	public void setDemarche(Demarche demarche) {
		this.demarche = demarche;
	}

	/**
	 * Getter du champ idUsager
	 * @return {@link String} la valeur du champ idUsager
	 */
	public String getIdUsager() {
		return idUsager;
	}

	/**
	 * Setter du champ idUsager
	 *
	 * @param idUsager valeur à setter
	 */
	public void setIdUsager(String idUsager) {
		this.idUsager = idUsager;
	}

	/**
	 * Getter du champ modifiable
	 *
	 * @return {@link boolean} la valeur du champ modifiable
	 */
	public boolean isModifiable() {
		return modifiable;
	}

	/**
	 * Setter du champ modifiable
	 *
	 * @param modifiable valeur à setter
	 */
	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}

	/**
	 * Getter du champ rawData
	 * @return {@link Map} champ rawData
	 */
	public Map<String, String> getRawData() {
		return rawData;
	}

	/**
	 * Setter du champ rawData
	 *
	 * @param rawData valeur à setter
	 */
	public void setRawData(Map<String, String> rawData) {
		this.rawData = rawData;
	}

	/**
	 * Getter du champ justificatif
	 *
	 * @return {@link Justificatif} la valeur du champ justificatif
	 */
	public Justificatif getJustificatif() {
		return justificatif;
	}

	/**
	 * Setter du champ justificatif
	 *
	 * @param justificatif valeur à setter
	 */
	public void setJustificatif(Justificatif justificatif) {
		this.justificatif = justificatif;
	}
}
