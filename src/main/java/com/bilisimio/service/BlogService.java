package com.bilisimio.service;

import java.util.List;

import com.bilisimio.entity.Blog;

public interface BlogService {

	Blog getBlogById(Long id);

	List<Blog> getAllBlogs();

}
