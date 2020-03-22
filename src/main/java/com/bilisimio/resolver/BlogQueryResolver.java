package com.bilisimio.resolver;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bilisimio.entity.Blog;
import com.bilisimio.service.BlogService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Service
public class BlogQueryResolver implements GraphQLQueryResolver {

	@Autowired
	private BlogService service;

	public Blog getBlogById(Long id) {
		return service.getBlogById(id);
	}
	
	public List<Blog> getAllBlogs() {
		return service.getAllBlogs();
	}

}
