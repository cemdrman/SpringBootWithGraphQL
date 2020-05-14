package com.bilisimio.service;

import com.bilisimio.entity.Blog;

public interface RabbitMQService {

	void send(Blog blog);

}
