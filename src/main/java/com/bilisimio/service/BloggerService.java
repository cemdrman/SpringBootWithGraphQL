package com.bilisimio.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bilisimio.dto.UpdateBloggerInput;
import com.bilisimio.entity.Blogger;

public interface BloggerService {

	List<Blogger> findAll();

	Optional<Blogger> findById(Long id);

	Blogger getById(Long id);

	Map<Long, Blogger> findAllByIds(Collection<Long> ids);

	/** id {@code null} ise yeni id üretilir. */
	Blogger create(Blogger blogger);

	Blogger update(UpdateBloggerInput input);

	boolean delete(Long id);

}
