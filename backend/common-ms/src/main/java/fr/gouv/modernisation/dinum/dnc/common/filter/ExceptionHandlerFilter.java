package fr.gouv.modernisation.dinum.dnc.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.gouv.modernisation.dinum.dnc.common.exception.BaseException;
import fr.gouv.modernisation.dinum.dnc.partenaire.generated.api.model.Erreur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtre permettant de gérer les exceptions ayant lieu dans les filters
 */
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (BaseException e) {
			setErrorResponse(response, e);
		} catch (RuntimeException e) {
			BaseException baseException = new BaseException("ErreurInterne",
					"Erreur interne lors de la requête " + request.getRequestURI(),
					HttpStatus.INTERNAL_SERVER_ERROR,
					e);
			setErrorResponse(response, baseException);
		}
	}

	public void setErrorResponse(HttpServletResponse response, BaseException e){
		response.setStatus(e.getHttpStatus().value());
		response.setContentType("application/json");
		Erreur apiError = e.createErreur();
		try {
			response.getWriter().write(new ObjectMapper().writeValueAsString(apiError));
		} catch (IOException ioe) {
			LOGGER.error("Erreur lors de la génération du JSON d'une Erreur de traitement" ,ioe);
		}
	}
}
