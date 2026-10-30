package com.bilisimio.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Kuyruk/exchange tanımları. Yalnızca app.messaging.rabbitmq.enabled=true iken aktiftir;
 * RabbitTemplate, Spring Boot tarafından bu MessageConverter ile otomatik yapılandırılır.
 */
@Configuration
@ConditionalOnProperty(name = "app.messaging.rabbitmq.enabled", havingValue = "true")
public class RabbitMQConfig {

	@Bean
	public Queue queue(RabbitMQProperties properties) {
		return new Queue(properties.queue(), false);
	}

	@Bean
	public DirectExchange exchange(RabbitMQProperties properties) {
		return new DirectExchange(properties.exchange());
	}

	@Bean
	public Binding binding(Queue queue, DirectExchange exchange, RabbitMQProperties properties) {
		return BindingBuilder.bind(queue).to(exchange).with(properties.routingKey());
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new JacksonJsonMessageConverter();
	}

}
