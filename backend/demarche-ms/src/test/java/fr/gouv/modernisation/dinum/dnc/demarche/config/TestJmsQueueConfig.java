package fr.gouv.modernisation.dinum.dnc.demarche.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;

@TestConfiguration
public class TestJmsQueueConfig {

	@Bean
	public JmsListenerContainerFactory<?> queueListenerFactory(@Autowired MessageConverter messageConverter) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setMessageConverter(messageConverter);
		return factory;
	}
}
