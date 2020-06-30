package fr.gouv.modernisation.dinum.dnc.common.exception;

import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.Erreur;
import org.springframework.http.HttpStatus;

/**
 * Exception de base des Services
 */
public class BaseException extends RuntimeException {

	/**
	 * Label de l'erreur dans le message d'erreur retourné au client.
	 * {@link String}
	 */
	private final String label;

	/**
	 * Description de l'erreur dans le message d'erreur retourné au client.
	 * {@link String}
	 */
	private final String description;

	/**
	 * Statut HTTP de l'erreur.
	 * {@link HttpStatus}
	 */
	private final HttpStatus httpStatus;

	/**
	 * Constructeur de base des Exceptions
	 * @param label Label de l'erreur {@link String}
	 * @param description Description de l'erreur {@link String}
	 * @param httpStatus Statut HTTP de l'erreur {@link HttpStatus}
	 * @param exception Exception ayant générée l'erreur {@link Throwable}
	 */
	public BaseException(String label, String description, HttpStatus httpStatus, Throwable exception){
		super(description,exception);
		this.label = label;
		this.description = description;
		this.httpStatus = httpStatus;
	}

	/**
	 * Création de l'erreur associée à l'exception.
	 *
	 */
	public Erreur createErreur() {
		Erreur erreur = new Erreur();
		erreur.setCode(label);
		erreur.setDescription(description);
		return erreur;
	}

	/**
	 * Getter du champ label
	 * return {@link String} la valeur du champ label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Getter du champ description
	 * return {@link String} la valeur du champ description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Getter du champ httpStatus
	 * return {@link HttpStatus} la valeur du champ httpStatus
	 */
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
