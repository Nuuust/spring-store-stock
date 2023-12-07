package storeStock.user.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import storeStock.user.domain.User


@Entity
@Table(name = "users")
class UserEntity(
        @Id val email: String,
        val firstName: String,
        val lastName: String,
        val age: Int,
) {
    fun asUser() = User(this.email, this.firstName, this.lastName, this.age)
}
fun User.asEntity() = UserEntity(this.email, this.firstName, this.lastName, this.age)