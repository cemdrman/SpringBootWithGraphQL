package com.bilisimio.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * RabbitMQ kapalıyken kullanılır; olayları yalnızca loglar.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.messaging.rabbitmq.enabled", havingValue = "false", matchIfMissing = true)
public class LoggingBlogEventPublisher implements BlogEventPublisher {

	@Override
	public void publish(BlogEvent event) {
		log.info("Blog olayı: {} id={}", event.type(), event.blog().getId());
	}

}
