package fr.gouv.modernisation.dinum.dnc.partenaire.config;

import fr.gouv.modernisation.dinum.dnc.common.filter.ApiKeyFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Map;

/**
 * Configuration Spring Security
 */
@Configuration
@ConfigurationProperties(prefix = "dnc")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private Map<String, String> apiKeys;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.authorizeRequests()
					.antMatchers("/health").permitAll()
				.and()
				.antMatcher("/partenaire/**")
				.antMatcher("/account/**")
				.addFilter(new ApiKeyFilter(apiKeys))
				.authorizeRequests().anyRequest().authenticated()
				.and()
				.csrf().disable()
				.formLogin().disable()
				;
	}

	/**
	 * Getter du champ apiKeys
	 * return {@link Map} {@link String} vers {@link String} la valeur du champ apiKeys
	 */
	public Map<String, String> getApiKeys() {
		return apiKeys;
	}

	/**
	 * Setter du champ apiKeys
	 *
	 * @param apiKeys valeur à setter
	 */
	public void setApiKeys(Map<String, String> apiKeys) {
		this.apiKeys = apiKeys;
	}
}
