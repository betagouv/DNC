package fr.gouv.modernisation.dinum.dnc.demarche.config;

import fr.gouv.modernisation.dinum.dnc.common.config.BaseWebMvcConfig;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

/**
 * Configuration des interceptors pour Demarche-MS
 */
@Configuration
public class DemarcheMsWebMvcConfig extends BaseWebMvcConfig {

	@Value("${api.dnc.franceconnect-ms.url}")
	private String franceconnectMsUrl;

	@Value("${api.dnc.franceconnect-ms.apiKey}")
	private String franceconnectMsApiKey;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		// Ajout de l'interceptor uniquement sur certains endpoints
		registry
				.addInterceptor(new SessionInterceptor(franceconnectMsUrl, franceconnectMsApiKey))
					.addPathPatterns("/demarche/usager/**", "/demarche/**")
					.excludePathPatterns("/demarche/check");
	}
}
