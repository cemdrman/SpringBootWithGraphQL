package com.bilisimio.resolver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bilisimio.model.Blogger;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;

@Service
public class BloggerQueryResolver implements GraphQLQueryResolver {

	private static List<Blogger> bloggers = new ArrayList<>();

	static {
		bloggers.add(new Blogger(1L, "Alper", "AKALIN", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(2L, "Kubilay", "KULAOĞLU", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(3L, "Ömer", "KOÇBİL", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(4L, "Barış", "UYAR", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(5L, "Burak", "KARADAĞ", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(6L, "Tahir", "KARDAK", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(7L, "Volkan", "ÖZDEMİR", "admin@bilisim.io", "password"));
		bloggers.add(new Blogger(8L, "Cem", "DIRMAN", "admin@bilisim.io", "password"));
	}

	public List<Blogger> getAllBloggers() {
		return bloggers;
	}

	public Blogger getBlogger(Long id) {
		return (Blogger) bloggers.stream()
				.filter(blogger -> blogger.getId().equals(id))
				.findAny()
				.orElse(null);
	}

}
