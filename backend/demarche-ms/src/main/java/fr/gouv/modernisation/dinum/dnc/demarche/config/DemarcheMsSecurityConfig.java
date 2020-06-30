package fr.gouv.modernisation.dinum.dnc.demarche.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configuration Spring Security
 */
@Configuration
public class DemarcheMsSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
				.authorizeRequests()
					.antMatchers("/health").permitAll()
				.and()
				.authorizeRequests().anyRequest().permitAll()
				.and()
				.csrf().disable()
				.formLogin().disable()
				;
	}
}
