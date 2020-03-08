package com.bilisimio.exception;

import java.util.List;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

@SuppressWarnings("serial")
public class BloggerNotFoundException extends RuntimeException implements GraphQLError {

	private String errorMessage;

	public BloggerNotFoundException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public String getMessage() {
		return errorMessage;
	}

	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorClassification getErrorType() {
		return ErrorType.DataFetchingException;
	}

}
