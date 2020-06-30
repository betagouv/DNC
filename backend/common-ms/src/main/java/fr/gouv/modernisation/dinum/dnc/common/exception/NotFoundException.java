package fr.gouv.modernisation.dinum.dnc.common.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

	/**
	 * Identifiant de l'objet recherché s'il est connu.
	 */
	private final String id;

	/**
	 * @see BaseException#BaseException(String, String, HttpStatus, Throwable)
	 */
	public NotFoundException(String description, String id) {
		super("NotFound", description, HttpStatus.NOT_FOUND, null);
		this.id = id;
	}

	public NotFoundException(Throwable exception, String id) {
		super("NotFound", "L'objet (ID : " + id + ") est inconnu du DNC", HttpStatus.NOT_FOUND, exception);
		this.id = id;
	}

	/**
	 * Getter du champ id
	 * @return {@link String} la valeur du champ id
	 */
	public String getId() {
		return id;
	}
}
