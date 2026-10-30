package com.bilisimio.messaging;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.bilisimio.config.RabbitMQProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.messaging.rabbitmq.enabled", havingValue = "true")
public class RabbitBlogEventPublisher implements BlogEventPublisher {

	private final AmqpTemplate rabbitTemplate;
	private final RabbitMQProperties properties;

	@Override
	public void publish(BlogEvent event) {
		try {
			rabbitTemplate.convertAndSend(properties.exchange(), properties.routingKey(), event);
		} catch (AmqpException ex) {
			// Mesajlaşma hatası GraphQL isteğini düşürmemeli.
			log.warn("Blog olayı RabbitMQ'ya gönderilemedi: {} id={}", event.type(), event.blog().getId(), ex);
		}
	}

}
