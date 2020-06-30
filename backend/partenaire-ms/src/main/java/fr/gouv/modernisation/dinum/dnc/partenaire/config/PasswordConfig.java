package fr.gouv.modernisation.dinum.dnc.partenaire.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

/**
 * Configuration du {@link PasswordEncoder} permettant de crypter les clients secrets
 */
@Configuration
public class PasswordConfig {

	@Value("${dnc.secret}")
	private String secretDnc;

	@Bean
	public PasswordEncoder defaultPasswordEncoder() {
		return new Pbkdf2PasswordEncoder(secretDnc, 10000, 128);
	}
}
