package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Classe de test pour les opérations de chiffrement/déchiffrement
 */
@ActiveProfiles("test")
public class CipherUtilsTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CipherUtilsTest.class);

	private String secretKey = "hUbAfVjD5cIJaViN8y1I9g==";

	@ParameterizedTest
	@ValueSource(strings = {
			"DUBOIS",
			"Angela Claire Louise",
			"femme",
			"24-08-1962",
			"France",
			"20 Avenue de Ségur Paris France 750107",
			"haitham el rharbi"
	})
	public void testEncrypt(String dataToEncrypt) throws UnsupportedEncodingException {

		String encryptedData = CipherUtils.encrypt(dataToEncrypt, secretKey);

		String decryptedData = CipherUtils.decrypt(encryptedData, secretKey);

		LOGGER.info(String.format("Source : %s, Valeur encryptée : %s, Valeur Décryptée : %s, Valeur encryptée compatible URL : %s",
				dataToEncrypt, encryptedData, decryptedData, encodeValueForUrl(encryptedData) ));
		Assertions.assertEquals(dataToEncrypt, decryptedData);
	}

	private static String encodeValueForUrl(String value) throws UnsupportedEncodingException {
		return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"6/g65P/oESx+7Z7eYMlE/VF8ZhEy5q6Ch+6AIsk8og5ZoA==",
			"xnmkgfJKUuLTGpTi6wTiBzUjcJEzySGIvIAREqZPXeS5+ZyOV6ri2sNbJkKdQj48",
			"n9PpvcT5k3MiJYzW4a1J5eH/MbMrt6OpIvRG+UPfIGlN",
			"mep0BXaTZbXWN5WexZRohW9Tyg944mEytWG3D31KFq6Y7LQb8S8=",
			"OxOV/ijmnZJ5bZfJZf0XmE2PNlkF8iDsc/FnutExD67RUw==",
			"0vHACq4770XMnVXzZnCe8cEPW0gg4TUYhi3jqIMe5HQmWIoV89gzf3S0TNvswrE6sQxUnbZC9O4/jC7wajJdqFuk6A==",
			"91FDJqNCrlEqzlIosreFInHcwH334q3FC4IKQIKYq9f9ZelvoEjBV4l3vJHt"
	})
	public void testDecrypt(String dataToDecrypt) {

		String decryptedData = CipherUtils.decrypt(dataToDecrypt, secretKey);

		LOGGER.info(String.format("Source : %s, Sortie Décryptée : %s", dataToDecrypt, decryptedData));
		Assertions.assertNotNull(decryptedData);
	}
}
