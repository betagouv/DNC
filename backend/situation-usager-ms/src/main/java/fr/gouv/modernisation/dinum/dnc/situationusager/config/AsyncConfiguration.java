package fr.gouv.modernisation.dinum.dnc.situationusager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration pour le travail Asynchrone.
 * Concerne uniquement les Appels aux API Franceconnectées qui doivent se faire dans la minute où le token est valide.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

	@Value("${dnc.pool.corePoolSize}")
	private int corePoolSize;

	@Value("${dnc.pool.maxPoolSize}")
	private int maxPoolSize;

	@Value("${dnc.pool.threadNamePrefix}")
	private String threadNamePrefix;

	// Pas de bean car de base Spring va définir un TaskExecutor
	@Bean
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);
		executor.setMaxPoolSize(maxPoolSize);
		executor.setThreadNamePrefix(threadNamePrefix);
		executor.initialize();
		return executor;
	}
}
