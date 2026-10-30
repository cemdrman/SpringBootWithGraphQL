package com.bilisimio.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bilisimio.dto.BlogPage;
import com.bilisimio.dto.CreateBlogInput;
import com.bilisimio.dto.UpdateBlogInput;
import com.bilisimio.entity.Blog;

import reactor.core.publisher.Flux;

public interface BlogService {

	List<Blog> findAll();

	BlogPage findPage(int page, int size);

	/** Blogu döner ve bulunursa VIEWED olayı yayınlar. */
	Optional<Blog> view(Long id);

	List<Blog> search(String keyword);

	List<Blog> findByCategory(Long categoryId);

	Map<Long, List<Blog>> findByAuthorIds(Collection<Long> authorIds);

	Blog create(CreateBlogInput input);

	Blog update(Long id, UpdateBlogInput input);

	boolean delete(Long id);

	/** Yeni yayınlanan blogların akışı (GraphQL subscription). */
	Flux<Blog> published();

}
