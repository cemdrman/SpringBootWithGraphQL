package com.bilisimio.mutation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bilisimio.model.Blogger;
import com.bilisimio.service.BloggerService;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;

@Service
public class BloggerMutation implements GraphQLMutationResolver {

	@Autowired
	private BloggerService service;

	public Blogger saveBlogger(Long id, String name, String surname, String email, String password) {
		Blogger blogger = new Blogger(id, name, surname, email, password);
		service.getBloggerList().add(blogger);
		return blogger;
	}

}