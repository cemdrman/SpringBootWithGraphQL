package com.bilisimio.exception;

import java.util.stream.Collectors;

import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;

/**
 * Uygulama istisnalarını anlamlı GraphQL hatalarına (errorType) dönüştürür.
 */
@ControllerAdvice
public class GraphQlExceptionAdvice {

	@GraphQlExceptionHandler
	public GraphQLError handleNotFound(ResourceNotFoundException ex, DataFetchingEnvironment env) {
		return error(env, ErrorType.NOT_FOUND, ex.getMessage());
	}

	@GraphQlExceptionHandler
	public GraphQLError handleInvalidRequest(InvalidRequestException ex, DataFetchingEnvironment env) {
		return error(env, ErrorType.BAD_REQUEST, ex.getMessage());
	}

	@GraphQlExceptionHandler
	public GraphQLError handleValidation(ConstraintViolationException ex, DataFetchingEnvironment env) {
		String message = ex.getConstraintViolations().stream()
				.map(v -> leafName(v.getPropertyPath().toString()) + " " + v.getMessage())
				.sorted()
				.collect(Collectors.joining(", "));
		return error(env, ErrorType.BAD_REQUEST, "Geçersiz istek: " + message);
	}

	private static GraphQLError error(DataFetchingEnvironment env, ErrorType type, String message) {
		return GraphqlErrorBuilder.newError(env).errorType(type).message(message).build();
	}

	private static String leafName(String path) {
		return path.substring(path.lastIndexOf('.') + 1);
	}

}
