package fr.gouv.modernisation.dinum.dnc.common.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

	/**
	 * Identifiant de l'objet recherché s'il est connu.
	 */
	private final String nameField;

	/**
	 * Constructeur par défaut.
	 *  @param label Label de l'erreur ProConnect {@link String}
	 * @param description Description de l'erreur ProConnect {@link String}
	 * @param exception Exception ayant générée l'erreur ProConnect {@link Throwable}
	 * @param nameField Champ obligatoire et manquant {@link String}
	 */
	public BadRequestException(String label, String description, Throwable exception, String nameField) {
		super(label, description, HttpStatus.BAD_REQUEST, exception);
		this.nameField = nameField;
	}

	public BadRequestException(String nameField){
		super("BadRequest","L'objet " + nameField + " est manquant ou incorrecte.", HttpStatus.BAD_REQUEST, null);
		this.nameField = nameField;
	}

	/**
	 * Getter du champ nameField
	 * @return {@link String} la valeur du champ nameField
	 */
	public String getNameField() {
		return nameField;
	}
}
