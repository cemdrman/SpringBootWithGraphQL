package com.bilisimio.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bilisimio.entity.Blog;
import com.bilisimio.service.BlogService;
import com.bilisimio.service.RabbitMQService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Service
public class BlogQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private BlogService service;
	
	@Autowired
	private RabbitMQService rabbit;

	public Blog getBlogById(Long id) {
		Blog blog = service.getBlogById(id);
		rabbit.send(blog);
		return blog;
	}
	
	public List<Blog> getAllBlogs() {
		return service.getAllBlogs();
	}

}
