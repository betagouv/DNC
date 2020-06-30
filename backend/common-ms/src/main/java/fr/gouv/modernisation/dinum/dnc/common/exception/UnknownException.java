package fr.gouv.modernisation.dinum.dnc.common.exception;

import org.springframework.http.HttpStatus;

public class UnknownException extends BaseException {

	/**
	 * @see BaseException#BaseException(String, String, HttpStatus, Throwable)
	 */
	public UnknownException(String label, String description, HttpStatus httpStatus, Throwable exception) {
		super(label, description, httpStatus, exception);
	}

	/**
	 * Constructeur spécifique à l'exception NotFound.
	 *
	 * @param exception   Exception ayant générée l'erreur {@link Throwable}
	 */
	public UnknownException(Throwable exception) {
		super("Unknown", "Erreur inconnue, veuillez contacter le support ProConnect.", HttpStatus.INTERNAL_SERVER_ERROR, exception);
	}
}
