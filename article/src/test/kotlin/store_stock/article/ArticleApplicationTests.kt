package store_stock.article

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.internal.util.collections.CollectionHelper.listOf
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import java.util.Date
import org.springframework.boot.test.web.client.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import store_stock.article.domain.Article
import java.util.UUID

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = [
		"spring.datasource.url=jdbc:h2:mem:testdb"
	]
)
class ArticleApplicationTests(@Autowired val client:TestRestTemplate) {

	@Test
	fun `test post and get article`() {

		val id = UUID.randomUUID()
		val article =Article(id, "new article", 20.20.toFloat(), 30, Date())
		client.postForObject<Article>("/api/articles", article)

		val entity = client.getForEntity<Article>("/api/articles/$id")
		val artcl= client.getForObject<Article>("/api/articles/$id")!!
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(artcl.name).contains(article.name)
		assertThat(artcl.price).isEqualTo(article.price)
		assertThat(artcl.stock).isEqualTo(article.stock)
	}



	@Test
	fun `test get list articles`() {
		val id = UUID.randomUUID()
		val article =Article(id, "new article", 20.20.toFloat(), 30, Date())
		client.postForObject<Article>("/api/articles", article)

		val article2 =Article(UUID.randomUUID(), "new article 2", 10.54.toFloat(), 14, Date())
		client.postForObject<Article>("/api/articles", article2)

		val response = client.getForEntity<List<Article>>("/api/articles")
		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

		// Prepare expected JSON
		val objectMapper = jacksonObjectMapper()
		val jsonArticle = objectMapper.writeValueAsString(article)
		val jsonArticle2 = objectMapper.writeValueAsString(article2)

		val expectedArticles = listOf(article, article2)
		// Assert
		assertThat(response.body).isEqualTo(expectedArticles)
	}
	@Test
	fun  `test update article`() {
// Create or seed a user
		val originalArticle = Article(UUID.randomUUID(), "new article", 20.20.toFloat(), 30, Date())
		client.postForObject<Article>("/api/users", originalArticle)
		val updatedArticle = originalArticle.copy(name = "Updated Name", price = 10.39.toFloat(), stock = 39)
		val request = HttpEntity(updatedArticle)
		val updateResponse = client.exchange("/api/articles/${originalArticle.id}", HttpMethod.PUT, request, Article::class.java)
		assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)

		val fetchResponse = client.getForEntity<Article>("/api/users/${originalArticle.id}")
		assertThat(fetchResponse.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(fetchResponse.body?.name).isEqualTo(updatedArticle.name)
		assertThat(fetchResponse.body?.price).isEqualTo(updatedArticle.price)
		assertThat(fetchResponse.body?.stock).isEqualTo(updatedArticle.stock)
	}
	@Test
	fun `test delete article`() {

		val article =Article(UUID.randomUUID(), "new article", 20.20.toFloat(), 30, Date())
		client.postForObject<Article>("/api/articles", article)
		val deleteResponse = client.exchange("/api/articles/${article.id}", HttpMethod.DELETE, null, Void::class.java)
		assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
	}
}
