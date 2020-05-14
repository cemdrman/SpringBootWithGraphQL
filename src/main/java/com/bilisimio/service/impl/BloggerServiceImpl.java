package com.bilisimio.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bilisimio.entity.Blogger;
import com.bilisimio.service.BloggerService;

@Service
public class BloggerServiceImpl implements BloggerService {

	private static List<Blogger> bloggerList = new ArrayList<>();

	static {
		bloggerList.add(new Blogger(1L, "Alper", "AKALIN", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(2L, "Kubilay", "KULAOĞLU", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(3L, "Ömer", "KOÇBİL", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(4L, "Barış", "UYAR", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(5L, "Burak", "KARADAĞ", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(6L, "Tahir", "KARDAK", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(7L, "Volkan", "ÖZDEMİR", "admin@bilisim.io", "password"));
		bloggerList.add(new Blogger(8L, "Cem", "DIRMAN", "admin@bilisim.io", "password"));
	}

	public List<Blogger> getBloggerList() {
		return bloggerList;
	}

}
