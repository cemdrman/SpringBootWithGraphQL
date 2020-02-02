package com.bilisimio.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Blogger {
	
	private Long id;
	private String name;
	private String surname;
	private String email;
	private String password;

}
