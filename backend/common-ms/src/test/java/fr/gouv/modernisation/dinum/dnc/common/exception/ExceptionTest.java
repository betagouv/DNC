package fr.gouv.modernisation.dinum.dnc.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests sur les exceptions du DNC
 */
public class ExceptionTest {

	public static final String STRING_VALUE = "Test";

	@Test
	public void exceptionBaseExceptionTest() {
		Throwable t = new Exception(STRING_VALUE);
		BaseException b = new BaseException(STRING_VALUE, STRING_VALUE,
				HttpStatus.INTERNAL_SERVER_ERROR, t);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, b.getHttpStatus());
		assertEquals(STRING_VALUE, b.getLabel());
		assertEquals(STRING_VALUE, b.getDescription());
	}

	@Test
	public void exceptionForbiddenAccesExceptionTest() {
		Throwable t = new Exception(STRING_VALUE);
		ForbiddenAccesException b = new ForbiddenAccesException(t, STRING_VALUE);

		assertEquals(HttpStatus.FORBIDDEN, b.getHttpStatus());
		assertEquals(STRING_VALUE, b.getIdClient());
	}

	@Test
	public void exceptionBadRequestExceptionTest() {
		BadRequestException b = new BadRequestException(STRING_VALUE);

		assertEquals(HttpStatus.BAD_REQUEST, b.getHttpStatus());
		assertEquals(STRING_VALUE, b.getNameField());
	}

	@Test
	public void exceptionNotFoundExceptionTest() {
		Throwable t = new Exception(STRING_VALUE);
		NotFoundException b = new NotFoundException(t, STRING_VALUE);

		assertEquals(HttpStatus.NOT_FOUND, b.getHttpStatus());
		assertEquals(STRING_VALUE, b.getId());
	}

	@Test
	public void exceptionUnknownExceptionTest() {
		Throwable t = new Exception(STRING_VALUE);
		UnknownException b = new UnknownException(t);

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, b.getHttpStatus());
	}
}
