package com.bilisimio.entity;

import java.util.List;

import com.bilisimio.enums.GenderType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blogger {

	private Long id;
	private String name;
	private String surname;
	private GenderType gender;
	private String email;
	private String password;
	private List<Blog> blogList;
	private Address address;

	public Blogger(Long id, String name, String surname, String email, String password) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
	}

}
