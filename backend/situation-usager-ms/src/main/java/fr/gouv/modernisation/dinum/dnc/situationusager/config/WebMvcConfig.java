package fr.gouv.modernisation.dinum.dnc.situationusager.config;

import fr.gouv.modernisation.dinum.dnc.common.config.BaseWebMvcConfig;
import fr.gouv.modernisation.dinum.dnc.common.interceptor.SessionInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
public class WebMvcConfig extends BaseWebMvcConfig {

	@Value("${api.dnc.franceconnect-ms.url}")
	private String franceconnectUrl;

	@Value("${api.dnc.franceconnect-ms.apiKey}")
	private String franceconnectUrlApiKey;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		super.addInterceptors(registry);
		// Ajout de l'interceptor uniquement sur certains endpoints
		registry
				.addInterceptor(new SessionInterceptor(franceconnectUrl, franceconnectUrlApiKey))
				.addPathPatterns("/informations","/informations/**",
						"/justificatifs", "/justificatifs/**",
						"/justificatif", "/justificatif/**",
						"/embedded/**")
				.excludePathPatterns("/verif-justificatif", "/demarche/**", "/meta/**");
	}
}
