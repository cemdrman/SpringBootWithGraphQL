package com.bilisimio.service.impl;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bilisimio.config.RabbitMQConfig;
import com.bilisimio.entity.Blog;
import com.bilisimio.service.RabbitMQService;

@Service
public class RabbitMQServiceImpl implements RabbitMQService {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private RabbitMQConfig config;

	@Override
	public void send(Blog blog) {
		rabbitTemplate.convertAndSend(config.getExchange(), config.getRoutingkey(), blog);
	}

}
