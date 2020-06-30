package fr.gouv.modernisation.dinum.dnc.situationusager.utils;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Classe utilitaire pour le chiffrement/déchiffrement
 */
public class CipherUtils {

	/**
	 * Message d'erreur en cas de clé de chiffrement null.
	 */
	private static final String ERREUR_SECRET_NULL = "Le secret ne doit pas être null";

	/**
	 * Algorithme de chiffrement/Mode/Padding
	 * {@link Cipher#getInstance(String)}
	 */
	public static final String ALGORITHME_MODE_PADDING = "AES/GCM/NoPadding";

	/**
	 * Algorithme de chiffrement
	 */
	public static final String ALGORITHME_CHIFFREMENT = "AES";

	/**
	 * Logger de la classe
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CipherUtils.class);

	/**
	 * Constructeur par défaut, privé pour ne bloquer l'accès
	 */
	private CipherUtils() {
		// Constructeur privée pour couper l'accès au constructeur par défaut
	}

	/**
	 * Méthode de chiffrement d'une données
	 * @param data données en entrée
	 * @param secretKey clé de chiffrement
	 * @return valeur chiffrée
	 */
	public static String encrypt(String data, String secretKey) {
		Assert.notNull(data, "La donnée ne doit pas être null");
		Assert.notNull(secretKey, ERREUR_SECRET_NULL);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHME_MODE_PADDING);

			SecureRandom secureRandom = new SecureRandom();
			byte[] iv = new byte[12];
			secureRandom.nextBytes(iv);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

			cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(secretKey), parameterSpec);

			byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
			byteBuffer.put(iv);
			byteBuffer.put(cipherText);
			byte[] cipherMessage = byteBuffer.array();

			return new String(Base64.encodeBase64(cipherMessage));
		}
		catch (GeneralSecurityException gse) {
			LOGGER.error("Erreur lors de la récupération de l'instance du Cipher", gse);
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors du traitement de chiffrement", e);
		}

		return null;
	}

	/**
	 * Méthode de déchiffrement de données
	 * @param data données à déchiffrer
	 * @param secretKey clé de chiffrement
	 * @return valeur déchiffrée
	 */
	public static String decrypt(String data, String secretKey) {
		Assert.notNull(data, "La donnée ne doit pas être null");
		Assert.notNull(secretKey, ERREUR_SECRET_NULL);
		try {
			Cipher cipher = Cipher.getInstance(ALGORITHME_MODE_PADDING);

			byte[] cipherData = Base64.decodeBase64(data);

			// récupération iv
			AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherData, 0, 12);

			cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(secretKey), gcmIv);

			return new String(cipher.doFinal(cipherData, 12, cipherData.length - 12), StandardCharsets.UTF_8);
		}
		catch (GeneralSecurityException gse) {
			LOGGER.error("Erreur lors de la récupération de l'instance du Cipher", gse);
		}
		catch (Exception e) {
			LOGGER.error("Erreur lors du traitement de déchiffrement", e);
		}

		return null;
	}

	/**
	 * Paramètre {@link SecretKeySpec} pour la création du Cipher. Dépend de la clé de chiffrement.
	 * @see Cipher#init(int, Key, AlgorithmParameterSpec)
	 * @param secretKey clé de chiffrement
	 * @return {@link SecretKeySpec} correspondant à la clé de chiffrement
	 */
	private static SecretKeySpec getSecretKeySpec(String secretKey) {
		Assert.notNull(secretKey, ERREUR_SECRET_NULL);
		return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHME_CHIFFREMENT);
	}
}
