package fr.gouv.modernisation.dinum.dnc.franceconnect.redis.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class TestRedisConfiguration {

	private RedisServer redisServer;

	public TestRedisConfiguration(RedisProperties redisProperties) {
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
		redisServer.start();
	}

	@PreDestroy
	public void preDestroy() {
		redisServer.stop();
	}
}
