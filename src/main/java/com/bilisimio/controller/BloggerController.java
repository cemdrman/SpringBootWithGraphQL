package com.bilisimio.controller;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.Arguments;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.bilisimio.dto.CreateBloggerInput;
import com.bilisimio.dto.UpdateBloggerInput;
import com.bilisimio.entity.Address;
import com.bilisimio.entity.Blog;
import com.bilisimio.entity.Blogger;
import com.bilisimio.service.BlogService;
import com.bilisimio.service.BloggerService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BloggerController {

	private final BloggerService bloggerService;
	private final BlogService blogService;

	@QueryMapping
	public List<Blogger> bloggers() {
		return bloggerService.findAll();
	}

	@QueryMapping
	public Blogger blogger(@Argument Long id) {
		return bloggerService.findById(id).orElse(null);
	}

	@QueryMapping
	public List<Blogger> getAllBloggers() {
		return bloggers();
	}

	@QueryMapping
	public Blogger getBlogger(@Argument Long id) {
		return bloggerService.getById(id);
	}

	@MutationMapping
	public Blogger createBlogger(@Argument @Valid CreateBloggerInput input) {
		Blogger blogger = new Blogger(null, input.name(), input.surname(), input.email(), input.password());
		blogger.setGender(input.gender());
		Blogger created = bloggerService.create(blogger);
		if (input.address() != null) {
			created.setAddress(new Address(created.getId(), input.address().addressType(), input.address().addressField()));
		}
		return created;
	}

	@MutationMapping
	public Blogger updateBlogger(@Arguments @Valid UpdateBloggerInput input) {
		return bloggerService.update(input);
	}

	@MutationMapping
	public boolean deleteBlogger(@Argument Long id) {
		return bloggerService.delete(id);
	}

	@MutationMapping
	public Blogger saveBlogger(@Argument Long id, @Argument String name, @Argument String surname,
			@Argument @Email String email, @Argument String password) {
		return bloggerService.create(new Blogger(id, name, surname, email, password));
	}

	/** Tüm blogger'ların blogları tek seferde yüklenir (N+1 sorgu problemi olmadan). */
	@BatchMapping
	public Map<Blogger, List<Blog>> blogList(List<Blogger> bloggers) {
		Map<Long, List<Blog>> byAuthor = blogService.findByAuthorIds(bloggers.stream().map(Blogger::getId).toList());
		return bloggers.stream().distinct().collect(Collectors.toMap(Function.identity(),
				b -> byAuthor.getOrDefault(b.getId(), List.of())));
	}

}
