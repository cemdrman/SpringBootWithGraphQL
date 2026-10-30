package com.bilisimio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("app.messaging.rabbitmq")
public record RabbitMQProperties(
		@DefaultValue("false") boolean enabled,
		@DefaultValue("bilisimio.exchange") String exchange,
		@DefaultValue("bilisimio.queue") String queue,
		@DefaultValue("bilisimio.routingkey") String routingKey) {
}
