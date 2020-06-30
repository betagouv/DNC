package fr.gouv.modernisation.dinum.dnc.common.config;

import fr.gouv.modernisation.dinum.dnc.common.interceptor.CorrelationIdInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class BaseWebMvcConfig implements WebMvcConfigurer {

	@Value("${dnc.front.urls:}")
	private String[] dncFrontUrl;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CorrelationIdInterceptor());
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry)
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addStatusController("/health", HttpStatus.OK);
	}

	/**
	 * @see org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addCorsMappings(CorsRegistry)
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry
				.addMapping("/**")
				.allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE")
				.allowedOrigins(dncFrontUrl);
	}
}
