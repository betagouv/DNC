package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import fr.gouv.modernisation.dinum.dnc.situationusager.generated.api.model.TypeJustificatif;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un modèle de Justificatif
 */
public class ModeleJustificatif {

	/**
	 * Nom de la démarche associé au justificatif
	 */
	private String nomDemarche;

	/**
	 * Texte associé au Justificatif
	 */
	private String label;

	/**
	 * Flag indiquant si le justificatif a un 1er supplément
	 */
	private boolean firstAddon;

	/**
	 * Texte du 1er supplément
	 */
	private String labelFirstAddon;

	/**
	 * Flag indiquant si le justificatif a un 2nd supplément
	 */
	private boolean secondAddon;

	/**
	 * Texte du 2nd supplément
	 */
	private String labelSecondAddon;

	/**
	 * Type du Justificatif
	 */
	private TypeJustificatif type;

	/**
	 * Liste des champs de la partie principale du Justificatif
	 */
	private List<String> champs = new ArrayList<>();

	/**
	 * Liste des champs de type Liste autorisé pour le justificatif
	 */
	private List<String> champsListe = new ArrayList<>();

	/**
	 * Liste des champs du 1er supplément
	 */
	private List<String> champsFirtsAddon = new ArrayList<>();

	/**
	 * Liste des champs du 2nd supplément
	 */
	private List<String> champsSecondAddon = new ArrayList<>();

	/**
	 * Valeur pour le champ "documentsAJoindre".
	 * Contient la liste des documents à joindre pour le type de justificatif.
	 * Utiliser uniquement le champ "documentsAjoindre" est utilisé dans la liste des champs.
	 */
	private String valeurDocumentsAJoindre;

	/**
	 * Constructeur par défaut
	 */
	public ModeleJustificatif() {
		// Constructeur par défaut
	}

	/**
	 * Renvoie l'intégralité des champs pour la génération du justificatif.
	 * @return {@link List} des champs pour la génération du justificatif
	 */
	public List<String> getAllChampsForGeneration() {
		List<String> allChamps = new ArrayList<>();
		allChamps.addAll(champs);
		if(firstAddon) {
			allChamps.addAll(champsFirtsAddon);
		}
		if(secondAddon) {
			allChamps.addAll(champsSecondAddon);
		}
		return allChamps;
	}

	/**
	 * Renvoie l'intégralité des champs concernés par le justificatif.
	 * @return {@link List} des champs concernés par le justificatif
	 */
	public List<String> getAllChamps() {
		List<String> allChamps = new ArrayList<>();
		allChamps.addAll(champs);
		allChamps.addAll(champsListe);
		if(firstAddon) {
			allChamps.addAll(champsFirtsAddon);
		}
		if(secondAddon) {
			allChamps.addAll(champsSecondAddon);
		}
		return allChamps;
	}

	/**
	 * Getter du champ nomDemarche
	 *
	 * @return {@link String} la valeur du champ nomDemarche
	 */
	public String getNomDemarche() {
		return nomDemarche;
	}

	/**
	 * Setter du champ nomDemarche
	 *
	 * @param nomDemarche valeur à setter
	 */
	public void setNomDemarche(String nomDemarche) {
		this.nomDemarche = nomDemarche;
	}

	/**
	 * Getter du champ label
	 *
	 * @return {@link String} la valeur du champ label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Setter du champ label
	 *
	 * @param label valeur à setter
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Getter du champ firstAddon
	 *
	 * @return {@link boolean} la valeur du champ firstAddon
	 */
	public boolean isFirstAddon() {
		return firstAddon;
	}

	/**
	 * Setter du champ firstAddon
	 *
	 * @param firstAddon valeur à setter
	 */
	public void setFirstAddon(boolean firstAddon) {
		this.firstAddon = firstAddon;
	}

	/**
	 * Getter du champ labelFirstAddon
	 *
	 * @return {@link String} la valeur du champ labelFirstAddon
	 */
	public String getLabelFirstAddon() {
		return labelFirstAddon;
	}

	/**
	 * Setter du champ labelFirstAddon
	 *
	 * @param labelFirstAddon valeur à setter
	 */
	public void setLabelFirstAddon(String labelFirstAddon) {
		this.labelFirstAddon = labelFirstAddon;
	}

	/**
	 * Getter du champ secondAddon
	 *
	 * @return {@link boolean} la valeur du champ secondAddon
	 */
	public boolean isSecondAddon() {
		return secondAddon;
	}

	/**
	 * Setter du champ secondAddon
	 *
	 * @param secondAddon valeur à setter
	 */
	public void setSecondAddon(boolean secondAddon) {
		this.secondAddon = secondAddon;
	}

	/**
	 * Getter du champ labelSecondAddon
	 *
	 * @return {@link String} la valeur du champ labelSecondAddon
	 */
	public String getLabelSecondAddon() {
		return labelSecondAddon;
	}

	/**
	 * Setter du champ labelSecondAddon
	 *
	 * @param labelSecondAddon valeur à setter
	 */
	public void setLabelSecondAddon(String labelSecondAddon) {
		this.labelSecondAddon = labelSecondAddon;
	}

	/**
	 * Getter du champ type
	 *
	 * @return {@link TypeJustificatif} la valeur du champ type
	 */
	public TypeJustificatif getType() {
		return type;
	}

	/**
	 * Setter du champ type
	 *
	 * @param type valeur à setter
	 */
	public void setType(TypeJustificatif type) {
		this.type = type;
	}

	/**
	 * Getter du champ champs
	 *
	 * @return {@link List< String>} la valeur du champ champs
	 */
	public List<String> getChamps() {
		return champs;
	}

	/**
	 * Getter du champ champsListe
	 *
	 * @return {@link List< String>} la valeur du champ champsListe
	 */
	public List<String> getChampsListe() {
		return champsListe;
	}

	/**
	 * Setter du champ champsListe
	 *
	 * @param champsListe valeur à setter
	 */
	public void setChampsListe(List<String> champsListe) {
		this.champsListe = champsListe;
	}

	/**
	 * Setter du champ champs
	 *
	 * @param champs valeur à setter
	 */
	public void setChamps(List<String> champs) {
		this.champs = champs;
	}

	/**
	 * Getter du champ champsFirtsAddon
	 *
	 * @return {@link List< String>} la valeur du champ champsFirtsAddon
	 */
	public List<String> getChampsFirtsAddon() {
		return champsFirtsAddon;
	}

	/**
	 * Setter du champ champsFirtsAddon
	 *
	 * @param champsFirtsAddon valeur à setter
	 */
	public void setChampsFirtsAddon(List<String> champsFirtsAddon) {
		this.champsFirtsAddon = champsFirtsAddon;
	}

	/**
	 * Getter du champ champsSecondAddon
	 *
	 * @return {@link List< String>} la valeur du champ champsSecondAddon
	 */
	public List<String> getChampsSecondAddon() {
		return champsSecondAddon;
	}

	/**
	 * Setter du champ champsSecondAddon
	 *
	 * @param champsSecondAddon valeur à setter
	 */
	public void setChampsSecondAddon(List<String> champsSecondAddon) {
		this.champsSecondAddon = champsSecondAddon;
	}

	/**
	 * Getter du champ valeurDocumentsAJoindre
	 *
	 * @return {@link String} la valeur du champ valeurDocumentsAJoindre
	 */
	public String getValeurDocumentsAJoindre() {
		return valeurDocumentsAJoindre;
	}

	/**
	 * Setter du champ valeurDocumentsAJoindre
	 *
	 * @param valeurDocumentsAJoindre valeur à setter
	 */
	public void setValeurDocumentsAJoindre(String valeurDocumentsAJoindre) {
		this.valeurDocumentsAJoindre = valeurDocumentsAJoindre;
	}
}
