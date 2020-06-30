package fr.gouv.modernisation.dinum.dnc.demarche.data.enumeration;

import java.util.Arrays;

/**
 * Enumération des status
 */
public enum Statut {

	CREEE("CREEE", "Créée"),
	MISE_A_JOUR("MISE_A_JOUR", "Mise à jour"),
	SUPPRIMEE("SUPPRIMEE", "Supprimée"),
	EN_COURS_TRAITEMENT("EN_COURS_TRAITEMENT", "En cours de traitement"),
	REFUSEE("REFUSEE", "Refusée")
	;

	/**
	 * Code de l'énumération
	 */
	private String code;

	/**
	 * Libellé de l'énumération
	 */
	private String libelle;

	/**
	 * Constructeur de l'énumération
	 * @param code code du statut
	 * @param libelle libellé du statut
	 */
	private Statut(String code, String libelle) {
		this.code = code;
		this.libelle = libelle;
	}



	/**
	 * Récupération du {@link Statut} correspondant à un code.
	 * @param code code du statut
	 * @return le {@link Statut} correspondant sinon {@code null}
	 */
	public static Statut fromCode(String code) {
		return Arrays.stream(Statut.values())
				.filter(statut -> statut.getCode().equals(code))
				.findFirst().orElse(null);
	}

	/**
	 * Getter du champ code
	 * return {@link String} la valeur du champ code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Getter du champ libelle
	 * return {@link String} la valeur du champ libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * Surcharge de la méthode {@link #toString()} pour faciliter les échanges.
	 * Utilise le {@link #code}
	 * @return le {@link #code} du {@link Statut}
	 */
	@Override
	public String toString() {
		return code;
	}
}
