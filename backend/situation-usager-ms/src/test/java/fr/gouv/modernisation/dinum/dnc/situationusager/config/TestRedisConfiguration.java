package fr.gouv.modernisation.dinum.dnc.situationusager.config;

import fr.gouv.modernisation.dinum.dnc.situationusager.controller.InformationsApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {
	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TestRedisConfiguration.class);

	private RedisServer redisServer;

	public TestRedisConfiguration(RedisProperties redisProperties) {
		LOGGER.info("Création du serveur avec le port {}",redisProperties.getPort());
		this.redisServer = RedisServer.builder()
				.port(redisProperties.getPort())
				.setting("daemonize no")
				.setting("appendonly no")
				.setting("maxmemory 64M")
				.setting("dbfilename dump.rdb")
				.build();
	}

	@PostConstruct
	public void postConstruct() {
		LOGGER.info("Démarrage du serveur avec le port {}", ReflectionTestUtils.getField(redisServer,"port"));
		redisServer.start();
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.info("Arret du serveur avec le port {}",ReflectionTestUtils.getField(redisServer,"port"));
		redisServer.stop();
	}
}
