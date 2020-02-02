package com.bilisimio;

import org.springframework.stereotype.Service;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Service
public class HelloQueryResolver implements GraphQLQueryResolver{
	
	public String hello() {
        return "Hello GraphQL";
    }
	
	public String helloWithParameter(String name) {
        return "Hello ".concat(name);
    }

}
