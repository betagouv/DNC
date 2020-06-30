/**
 * ProConnect® - 2020
 **/
package fr.gouv.modernisation.dinum.dnc.common.config;

import fr.gouv.modernisation.dinum.dnc.common.exception.BaseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Handler des erreurs spécifiques.
 *
 * @author Sopra Steria Group
 **/
@ControllerAdvice
public class RequestExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handler qui intercepte toutes les erreurs spécifiques du système.
     *
     * @param ex        Exception spécifique {@link BaseException}
     * @param request   Requête Web {@link WebRequest}
     * @return          Erreur au format ProConnect {@link ResponseEntity}
     *                  {@link fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.Erreur}
     */
    @ExceptionHandler({ BaseException.class })
    public ResponseEntity<Object> handleMainException(
            BaseException ex, WebRequest request) {
        return new ResponseEntity<>(ex.createErreur(),ex.getHttpStatus());
    }
}
