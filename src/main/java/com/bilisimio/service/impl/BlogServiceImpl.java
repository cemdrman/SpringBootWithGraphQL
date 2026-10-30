package com.bilisimio.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bilisimio.dto.BlogPage;
import com.bilisimio.dto.CreateBlogInput;
import com.bilisimio.dto.UpdateBlogInput;
import com.bilisimio.entity.Blog;
import com.bilisimio.exception.InvalidRequestException;
import com.bilisimio.exception.ResourceNotFoundException;
import com.bilisimio.messaging.BlogEvent;
import com.bilisimio.messaging.BlogEventPublisher;
import com.bilisimio.messaging.BlogEventType;
import com.bilisimio.service.BlogService;
import com.bilisimio.service.BloggerService;
import com.bilisimio.service.CategoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class BlogServiceImpl implements BlogService {

	private final Map<Long, Blog> blogs = new ConcurrentSkipListMap<>();
	private final AtomicLong sequence = new AtomicLong();
	private final Sinks.Many<Blog> publishedSink = Sinks.many().multicast().directBestEffort();

	private final BloggerService bloggerService;
	private final CategoryService categoryService;
	private final BlogEventPublisher eventPublisher;

	public BlogServiceImpl(BloggerService bloggerService, CategoryService categoryService,
			BlogEventPublisher eventPublisher) {
		this.bloggerService = bloggerService;
		this.categoryService = categoryService;
		this.eventPublisher = eventPublisher;

		for (int i = 1; i <= 5; i++) {
			store(new Blog(null, "Spring Boot Ile GraphQL API – " + i, "Yeni Nesil API",
					"https://bilisim.io/2020/02/17/spring-boot-ile-graphql-api-" + i + "/", 8L,
					List.of(1L, 2L, 3L), Instant.parse("2020-02-17T09:00:00Z").plusSeconds(i * 86_400L)));
		}
	}

	@Override
	public List<Blog> findAll() {
		return new ArrayList<>(blogs.values());
	}

	@Override
	public BlogPage findPage(int page, int size) {
		return BlogPage.of(findAll(), page, size);
	}

	@Override
	public Optional<Blog> view(Long id) {
		Optional<Blog> blog = Optional.ofNullable(blogs.get(id));
		blog.ifPresent(b -> eventPublisher.publish(BlogEvent.of(BlogEventType.VIEWED, b)));
		return blog;
	}

	@Override
	public List<Blog> search(String keyword) {
		String needle = normalize(keyword == null ? "" : keyword.trim());
		if (needle.isEmpty()) {
			return findAll();
		}
		return blogs.values().stream()
				.filter(b -> normalize(b.getTitle()).contains(needle) || normalize(b.getContent()).contains(needle))
				.toList();
	}

	/** Büyük/küçük harf ve Türkçe I/ı/İ/i farklarını yok sayan karşılaştırma için normalleştirir. */
	private static String normalize(String text) {
		return text.toLowerCase(Locale.ROOT).replace("i\u0307", "i").replace('ı', 'i');
	}

	@Override
	public List<Blog> findByCategory(Long categoryId) {
		return blogs.values().stream().filter(b -> b.getCategoryIds().contains(categoryId)).toList();
	}

	@Override
	public Map<Long, List<Blog>> findByAuthorIds(Collection<Long> authorIds) {
		Set<Long> ids = new HashSet<>(authorIds);
		return blogs.values().stream()
				.filter(b -> ids.contains(b.getAuthorId()))
				.collect(Collectors.groupingBy(Blog::getAuthorId));
	}

	@Override
	public Blog create(CreateBlogInput input) {
		bloggerService.getById(input.authorId());
		Blog blog = store(new Blog(null, input.title(), input.content(), input.imageUrl(), input.authorId(),
				validCategoryIds(input.categoryIds()), Instant.now()));
		eventPublisher.publish(BlogEvent.of(BlogEventType.CREATED, blog));
		publishedSink.tryEmitNext(blog);
		return blog;
	}

	@Override
	public synchronized Blog update(Long id, UpdateBlogInput input) {
		Blog blog = Optional.ofNullable(blogs.get(id)).orElseThrow(() -> new ResourceNotFoundException("Blog", id));
		if (input.title() != null) {
			blog.setTitle(input.title());
		}
		if (input.content() != null) {
			blog.setContent(input.content());
		}
		if (input.imageUrl() != null) {
			blog.setImageUrl(input.imageUrl());
		}
		if (input.categoryIds() != null) {
			blog.setCategoryIds(validCategoryIds(input.categoryIds()));
		}
		eventPublisher.publish(BlogEvent.of(BlogEventType.UPDATED, blog));
		return blog;
	}

	@Override
	public boolean delete(Long id) {
		Blog removed = blogs.remove(id);
		if (removed == null) {
			throw new ResourceNotFoundException("Blog", id);
		}
		eventPublisher.publish(BlogEvent.of(BlogEventType.DELETED, removed));
		return true;
	}

	@Override
	public Flux<Blog> published() {
		return publishedSink.asFlux();
	}

	private Blog store(Blog blog) {
		blog.setId(sequence.incrementAndGet());
		blogs.put(blog.getId(), blog);
		return blog;
	}

	private List<Long> validCategoryIds(List<Long> categoryIds) {
		if (categoryIds == null || categoryIds.isEmpty()) {
			return new ArrayList<>();
		}
		List<Long> distinct = categoryIds.stream().distinct().toList();
		Set<Long> found = categoryService.findAllByIds(distinct).keySet();
		List<Long> missing = distinct.stream().filter(id -> !found.contains(id)).toList();
		if (!missing.isEmpty()) {
			throw new InvalidRequestException("Kategori bulunamadı: " + missing);
		}
		return new ArrayList<>(distinct);
	}

}
