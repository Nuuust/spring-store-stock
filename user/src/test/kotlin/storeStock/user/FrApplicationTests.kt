package storeStock.user

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
import storeStock.user.domain.User
@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = [
		"spring.datasource.url=jdbc:h2:mem:testdb"
	]
)
class FrApplicationTests(@Autowired val client:TestRestTemplate) {

	@Test
	fun `test post and get user`() {

		val email = "benjamin.couet@mail.com"
		val user =User(email, "Benjamin Couet", "5 avenue Michel", true, Date())
		client.postForObject<User>("/api/users", user)

		val entity = client.getForEntity<User>("/api/users/$email")
		val usr= client.getForObject<User>("/api/users/$email")!!
		assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(usr.name).contains(user.name)
		assertThat(usr.email).contains(user.email)
		assertThat(usr.adress).contains(user.adress)
	}



	@Test
	fun `test get list users`() {
		val user = User("benjamin.couet@mail.com", "Benjamin Couet", "5 avenue Michel", true, Date())
		client.postForObject<User>("/api/users", user)

		val user2 = User("alexandre.clenet@mail.com", "Alexandre Clenet", "5 avenue Yannis", true, Date())
		client.postForObject<User>("/api/users", user2)

		val response = client.getForEntity<List<User>>("/api/users")
		assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

		// Prepare expected JSON
		val objectMapper = jacksonObjectMapper()
		val jsonUser = objectMapper.writeValueAsString(user)
		val jsonUser2 = objectMapper.writeValueAsString(user2)

		val expectedUsers = listOf(jsonUser, jsonUser2)
		// Assert
		assertThat(response.body).isEqualTo(expectedUsers)
	}
	@Test
	fun  `test update user`() {
// Create or seed a user
		val originalUser = User("user@example.com", "Original Name", "Original Address", true, Date())
		client.postForObject<User>("/api/users", originalUser)
		val updatedUser = originalUser.copy(name = "Updated Name", adress = "Updated Address")
		val request = HttpEntity(updatedUser)
		val updateResponse = client.exchange("/api/users/${originalUser.email}", HttpMethod.PUT, request, User::class.java)
		assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)

		val fetchResponse = client.getForEntity<User>("/api/users/${originalUser.email}")
		assertThat(fetchResponse.statusCode).isEqualTo(HttpStatus.OK)
		assertThat(fetchResponse.body?.name).isEqualTo(updatedUser.name)
		assertThat(fetchResponse.body?.adress).isEqualTo(updatedUser.adress)
	}
	@Test
	fun `test delete user`() {

		val user = User("user@example.com", "Test User", "123 Test Street", true, Date())
		client.postForObject<User>("/api/users", user)
		val deleteResponse = client.exchange("/api/users/${user.email}", HttpMethod.DELETE, null, Void::class.java)
		assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
	}
}
