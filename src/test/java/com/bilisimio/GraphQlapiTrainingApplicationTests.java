package com.bilisimio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.ResponseError;
import org.springframework.graphql.test.tester.GraphQlTester;

import com.bilisimio.dto.CreateBlogInput;
import com.bilisimio.service.BlogService;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureGraphQlTester
class GraphQlapiTrainingApplicationTests {

	@Autowired
	private GraphQlTester graphQlTester;

	@Autowired
	private BlogService blogService;

	@Test
	void bloggerWithNestedBlogsAndCategories() {
		graphQlTester.document("""
				{ blogger(id: 8) { name blogList { title author { name } categoryList { name } } } }
				""")
				.execute()
				.path("blogger.name").entity(String.class).isEqualTo("Cem")
				.path("blogger.blogList").entityList(Object.class).hasSizeGreaterThan(0)
				.path("blogger.blogList[0].author.name").entity(String.class).isEqualTo("Cem")
				.path("blogger.blogList[0].categoryList[*].name").entityList(String.class)
				.containsExactly("GraphQL", "Java", "Spring Boot");
	}

	@Test
	void unknownBloggerReturnsNullButDeprecatedQueryReturnsNotFound() {
		graphQlTester.document("{ blogger(id: 999) { id } }").execute()
				.path("blogger").valueIsNull();

		graphQlTester.document("{ getBlogger(id: 999) { id } }").execute()
				.errors().satisfy(hasError("NOT_FOUND", "Blogger bulunamadı: id=999"));
	}

	@Test
	void createUpdateAndDeleteBlogger() {
		Long id = graphQlTester.document("""
				mutation {
				  createBlogger(input: {name: "Ada", surname: "Lovelace", email: "ada@bilisim.io",
				    password: "secret123", gender: FEMALE, address: {addressType: HOME, addressField: "Londra"}}) {
				    id gender address { addressField } blogList { id }
				  }
				}
				""")
				.execute()
				.path("createBlogger.gender").entity(String.class).isEqualTo("FEMALE")
				.path("createBlogger.address.addressField").entity(String.class).isEqualTo("Londra")
				.path("createBlogger.blogList").entityList(Object.class).hasSize(0)
				.path("createBlogger.id").entity(Long.class).get();

		graphQlTester.document("mutation($id: ID!) { updateBlogger(id: $id, surname: \"King\") { name surname email } }")
				.variable("id", id)
				.execute()
				.path("updateBlogger.name").entity(String.class).isEqualTo("Ada")
				.path("updateBlogger.surname").entity(String.class).isEqualTo("King")
				.path("updateBlogger.email").entity(String.class).isEqualTo("ada@bilisim.io");

		graphQlTester.document("mutation($id: ID!) { deleteBlogger(id: $id) }")
				.variable("id", id)
				.execute()
				.path("deleteBlogger").entity(Boolean.class).isEqualTo(true);

		graphQlTester.document("mutation($id: ID!) { deleteBlogger(id: $id) }")
				.variable("id", id)
				.execute()
				.errors().satisfy(hasError("NOT_FOUND", "Blogger bulunamadı"));
	}

	@Test
	void createBloggerValidatesInput() {
		graphQlTester.document("""
				mutation { createBlogger(input: {name: "X", surname: "Y", email: "not-an-email", password: "123"}) { id } }
				""")
				.execute()
				.errors().satisfy(hasError("BAD_REQUEST", "email"));

		graphQlTester.document("""
				mutation { createBlogger(input: {name: "X", surname: "Y", email: "cem@bilisim.io", password: "123456"}) { id } }
				""")
				.execute()
				.errors().satisfy(hasError("BAD_REQUEST", "E-posta zaten kullanımda"));
	}

	@Test
	void blogsArePaginated() {
		graphQlTester.document("{ blogs(page: 0, size: 2) { content { id } page size totalElements hasNext } }")
				.execute()
				.path("blogs.content").entityList(Object.class).hasSize(2)
				.path("blogs.size").entity(Integer.class).isEqualTo(2)
				.path("blogs.hasNext").entity(Boolean.class).isEqualTo(true);

		graphQlTester.document("{ blogs(size: 500) { totalElements } }")
				.execute()
				.errors().satisfy(hasError("BAD_REQUEST", "size"));
	}

	@Test
	void searchIsCaseInsensitive() {
		graphQlTester.document("{ searchBlogs(keyword: \"GRAPHQL apı – 3\") { title } }")
				.execute()
				.path("searchBlogs[*].title").entityList(String.class)
				.containsExactly("Spring Boot Ile GraphQL API – 3");
	}

	@Test
	void blogCrudAndValidation() {
		Long id = graphQlTester.document("""
				mutation { createBlog(input: {title: "GraphQL Subscriptions", content: "WebSocket ile",
				  authorId: 1, categoryIds: [1]}) { id author { name } categoryList { name } publishedAt } }
				""")
				.execute()
				.path("createBlog.author.name").entity(String.class).isEqualTo("Alper")
				.path("createBlog.categoryList[*].name").entityList(String.class).containsExactly("GraphQL")
				.path("createBlog.id").entity(Long.class).get();

		graphQlTester.document("mutation($id: ID!) { updateBlog(id: $id, input: {categoryIds: [2, 3]}) { title categoryList { name } } }")
				.variable("id", id)
				.execute()
				.path("updateBlog.title").entity(String.class).isEqualTo("GraphQL Subscriptions")
				.path("updateBlog.categoryList[*].name").entityList(String.class).containsExactly("Java", "Spring Boot");

		graphQlTester.document("mutation($id: ID!) { deleteBlog(id: $id) }")
				.variable("id", id)
				.execute()
				.path("deleteBlog").entity(Boolean.class).isEqualTo(true);

		graphQlTester.document("mutation { createBlog(input: {title: \"t\", content: \"c\", authorId: 999}) { id } }")
				.execute()
				.errors().satisfy(hasError("NOT_FOUND", "Blogger bulunamadı"));

		graphQlTester.document("mutation { createBlog(input: {title: \"t\", content: \"c\", authorId: 1, categoryIds: [42]}) { id } }")
				.execute()
				.errors().satisfy(hasError("BAD_REQUEST", "Kategori bulunamadı: [42]"));
	}

	@Test
	void categoriesExposeTheirBlogs() {
		graphQlTester.document("mutation { createCategory(name: \"Kotlin\") { id name blogs { id } } }")
				.execute()
				.path("createCategory.name").entity(String.class).isEqualTo("Kotlin")
				.path("createCategory.blogs").entityList(Object.class).hasSize(0);

		graphQlTester.document("mutation { createCategory(name: \"java\") { id } }")
				.execute()
				.errors().satisfy(hasError("BAD_REQUEST", "Kategori zaten mevcut"));

		graphQlTester.document("{ category(id: 1) { name blogs { id } } }")
				.execute()
				.path("category.name").entity(String.class).isEqualTo("GraphQL")
				.path("category.blogs").entityList(Object.class).hasSizeGreaterThan(0);
	}

	@Test
	void subscriptionReceivesPublishedBlogs() {
		Flux<String> titles = graphQlTester.document("subscription { blogPublished { title } }")
				.executeSubscription()
				.toFlux("blogPublished.title", String.class);

		StepVerifier.create(titles)
				.then(() -> blogService.create(new CreateBlogInput("Canlı yayın", "içerik", null, 2L, List.of())))
				.expectNext("Canlı yayın")
				.thenCancel()
				.verify(Duration.ofSeconds(5));
	}

	private static Consumer<List<ResponseError>> hasError(String classification, String messagePart) {
		return errors -> assertThat(errors).anySatisfy(error -> {
			assertThat(String.valueOf(error.getErrorType())).isEqualTo(classification);
			assertThat(error.getMessage()).contains(messagePart);
		});
	}

}
