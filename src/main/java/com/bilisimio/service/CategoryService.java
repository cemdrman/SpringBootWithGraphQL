package com.bilisimio.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bilisimio.entity.Category;

public interface CategoryService {

	List<Category> findAll();

	Optional<Category> findById(Long id);

	Map<Long, Category> findAllByIds(Collection<Long> ids);

	Category create(String name);

}
