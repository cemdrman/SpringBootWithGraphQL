package com.bilisimio.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bilisimio.model.Blogger;
import com.bilisimio.service.BloggerService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Service
public class BloggerQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private BloggerService service;

	public Blogger getBlogger(Long id) {
		return (Blogger) service.getBloggerList()
				.stream()
				.filter(blogger -> blogger.getId().equals(id)).findAny()
				.orElse(null);
	}

	public List<Blogger> getAllBloggers() {
		return service.getBloggerList();
	}

}
