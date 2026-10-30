package com.bilisimio.exception;

@SuppressWarnings("serial")
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String resource, Object id) {
		super(resource + " bulunamadı: id=" + id);
	}

}
