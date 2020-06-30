package fr.gouv.modernisation.dinum.dnc.partenaire.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un partenaire de DNC (Fournisseur de service, Fournisseur de données,....)
 */
@Entity
@Table(name = "partenaire",
		uniqueConstraints = {
			@UniqueConstraint(name = "part_siret_uq", columnNames = "siret")
		}
)
@SequenceGenerator(name = "partenaire_id_seq", sequenceName = "partenaire_id_seq", allocationSize = 50)
public class Partenaire {

	/**
	 * Id technique du partenaire.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partenaire_id_seq")
	private Long id;

	/**
	 * Libellé court du partenaire
	 */
	@Column(name = "libelle_court")
	private String libelleCourt;

	/**
	 * Libellé long du partenaire
	 */
	@Column(name = "libelle_long")
	private String libelleLong;

	/**
	 * SIREN ou SIRET du partenaire
	 */
	@Column(name = "siret", length = 14)
	private String siret;

	/**
	 * Logo
	 */
	@Column(name = "logo")
	@Lob
	private byte[] logo;

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
	 * Liste des credentials du partenaire
	 */
	@OneToMany(mappedBy="partenaire", fetch= FetchType.EAGER)
	private List<Credential> credentials = new ArrayList<>();

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
	 * Getter du champ libelleCourt
	 * return {@link String} la valeur du champ libelleCourt
	 */
	public String getLibelleCourt() {
		return libelleCourt;
	}

	/**
	 * Setter du champ libelleCourt
	 *
	 * @param libelleCourt valeur à setter
	 */
	public void setLibelleCourt(String libelleCourt) {
		this.libelleCourt = libelleCourt;
	}

	/**
	 * Getter du champ libelleLong
	 * return {@link String} la valeur du champ libelleLong
	 */
	public String getLibelleLong() {
		return libelleLong;
	}

	/**
	 * Setter du champ libelleLong
	 *
	 * @param libelleLong valeur à setter
	 */
	public void setLibelleLong(String libelleLong) {
		this.libelleLong = libelleLong;
	}

	/**
	 * Getter du champ siret
	 * return {@link String} la valeur du champ siret
	 */
	public String getSiret() {
		return siret;
	}

	/**
	 * Setter du champ siret
	 *
	 * @param siret valeur à setter
	 */
	public void setSiret(String siret) {
		this.siret = siret;
	}

	/**
	 * Getter du champ logo
	 * return {@link byte[]} la valeur du champ logo
	 */
	public byte[] getLogo() {
		return logo;
	}

	/**
	 * Setter du champ logo
	 *
	 * @param logo valeur à setter
	 */
	public void setLogo(byte[] logo) {
		this.logo = logo;
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

	/**
	 * Getter du champ credentials
	 * return {@link List< Credential>} la valeur du champ credentials
	 */
	public List<Credential> getCredentials() {
		return credentials;
	}

	/**
	 * Setter du champ credentials
	 *
	 * @param credentials valeur à setter
	 */
	public void setCredentials(List<Credential> credentials) {
		this.credentials = credentials;
	}
}
