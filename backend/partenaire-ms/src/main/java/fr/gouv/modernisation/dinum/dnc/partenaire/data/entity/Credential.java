package fr.gouv.modernisation.dinum.dnc.partenaire.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;

/**
 * Entité Credential, représentant les identifiants/secrets d'un compte partenaire
 */
@Entity
@Table(name = "credential",
		uniqueConstraints = @UniqueConstraint(
				name = "credential_uq",
				columnNames = {"partenaire_id","client_id"}
				)
)
@SequenceGenerator(name = "credential_id_seq", sequenceName = "credential_id_seq", allocationSize = 50)
public class Credential {

	/**
	 * ID technique des credentials
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credential_id_seq")
	private Long id;

	/**
	 * Partenaire propriétaire des Credentials
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "partenaire_id")
	private Partenaire partenaire;

	/**
	 * Client ID
	 */
	@Column(name = "client_id")
	private String clientId;

	/**
	 * Client Secret, valeur hashé via le PasswordEncoder
	 */
	@Column(name = "client_secret")
	private String clientSecret;

	/**
	 * Date de création
	 */
	@Column(name = "date_creation")
	private LocalDateTime dateCreation;

	/**
	 * Date de mise à jour
	 */
	@Column(name = "date_maj")
	private LocalDateTime dateMAJ;

	/**
	 * Getter du champ id
	 * return {@link Long} la valeur du champ id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Setter du champ id
	 *
	 * @param id valeur à setter
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Getter du champ partenaire
	 * return {@link Partenaire} la valeur du champ partenaire
	 */
	public Partenaire getPartenaire() {
		return partenaire;
	}

	/**
	 * Setter du champ partenaire
	 *
	 * @param partenaire valeur à setter
	 */
	public void setPartenaire(Partenaire partenaire) {
		this.partenaire = partenaire;
	}

	/**
	 * Getter du champ clientId
	 * return {@link String} la valeur du champ clientId
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Setter du champ clientId
	 *
	 * @param clientId valeur à setter
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Getter du champ clientSecret
	 * return {@link String} la valeur du champ clientSecret
	 */
	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * Setter du champ clientSecret
	 *
	 * @param clientSecret valeur à setter
	 */
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * Getter du champ dateCreation
	 * return {@link LocalDateTime} la valeur du champ dateCreation
	 */
	public LocalDateTime getDateCreation() {
		return dateCreation;
	}

	/**
	 * Setter du champ dateCreation
	 *
	 * @param dateCreation valeur à setter
	 */
	public void setDateCreation(LocalDateTime dateCreation) {
		this.dateCreation = dateCreation;
	}

	/**
	 * Getter du champ dateMAJ
	 * return {@link LocalDateTime} la valeur du champ dateMAJ
	 */
	public LocalDateTime getDateMAJ() {
		return dateMAJ;
	}

	/**
	 * Setter du champ dateMAJ
	 *
	 * @param dateMAJ valeur à setter
	 */
	public void setDateMAJ(LocalDateTime dateMAJ) {
		this.dateMAJ = dateMAJ;
	}
}
