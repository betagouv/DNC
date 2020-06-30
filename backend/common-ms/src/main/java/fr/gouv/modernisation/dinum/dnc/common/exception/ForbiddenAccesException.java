package fr.gouv.modernisation.dinum.dnc.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenAccesException extends BaseException {

	/**
	 * Identifiant du client utilisé.
	 */
	private final String idClient;

	/**
	 * @see BaseException#BaseException(String, String, HttpStatus, Throwable)
	 */
	public ForbiddenAccesException(String description, String idClient) {
		super("Forbidden", description, HttpStatus.FORBIDDEN, null);
		this.idClient = idClient;
	}

	public ForbiddenAccesException(Throwable exception, String idClient) {
		super("Forbidden", "Vous n'avez pas le droit d'accéder à cette données du DNC (id client : " + idClient + ")",
				HttpStatus.FORBIDDEN, exception);
		this.idClient = idClient;
	}

	/**
	 * Getter du champ id
	 * @return {@link String} la valeur du champ id
	 */
	public String getIdClient() {
		return idClient;
	}
}
