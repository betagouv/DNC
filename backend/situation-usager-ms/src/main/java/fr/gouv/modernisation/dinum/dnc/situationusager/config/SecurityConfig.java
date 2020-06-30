package fr.gouv.modernisation.dinum.dnc.situationusager.config;

import fr.gouv.modernisation.dinum.dnc.common.filter.ExceptionHandlerFilter;
import fr.gouv.modernisation.dinum.dnc.common.filter.PartenaireCredentialFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configuration Spring Security
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


	@Value("${api.dnc.partenaire-ms.url}")
	private String partenaireMsUrl;

	@Value("${api.dnc.partenaire-ms.apiKey}")
	private String partenaireApiKey;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.authorizeRequests()
					.antMatchers("/health").permitAll()
				.and()
					.antMatcher("/demarche/**")
					.addFilterBefore(new ExceptionHandlerFilter(), PartenaireCredentialFilter.class)
					.addFilter(new PartenaireCredentialFilter(partenaireMsUrl, partenaireApiKey))
					.authorizeRequests().antMatchers("/demarche/**").authenticated()
				.and()
				.authorizeRequests().anyRequest().permitAll()
				.and()
				.csrf().disable()
				.formLogin().disable()
				;
	}
}
