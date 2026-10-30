package com.bilisimio.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bilisimio.entity.Category;
import com.bilisimio.exception.InvalidRequestException;
import com.bilisimio.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final Map<Long, Category> categories = new ConcurrentSkipListMap<>();
	private final AtomicLong sequence = new AtomicLong();

	public CategoryServiceImpl() {
		create("GraphQL");
		create("Java");
		create("Spring Boot");
	}

	@Override
	public List<Category> findAll() {
		return new ArrayList<>(categories.values());
	}

	@Override
	public Optional<Category> findById(Long id) {
		return Optional.ofNullable(categories.get(id));
	}

	@Override
	public Map<Long, Category> findAllByIds(Collection<Long> ids) {
		return ids.stream().distinct().filter(categories::containsKey)
				.collect(Collectors.toMap(id -> id, categories::get));
	}

	@Override
	public synchronized Category create(String name) {
		String trimmed = name == null ? "" : name.trim();
		if (trimmed.isEmpty()) {
			throw new InvalidRequestException("Kategori adı boş olamaz");
		}
		if (categories.values().stream().anyMatch(c -> c.getName().equalsIgnoreCase(trimmed))) {
			throw new InvalidRequestException("Kategori zaten mevcut: " + trimmed);
		}
		Category category = new Category(sequence.incrementAndGet(), trimmed);
		categories.put(category.getId(), category);
		return category;
	}

}
