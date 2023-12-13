package storestock.user.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import storestock.Panier.domain.User
import java.util.*


@Entity
@Table(name = "users")
class UserEntity(
        @Id val email: String,
        val name: String,
        val adress: String,
        val sub: Boolean,
        val lastOrder: Date
) {
    fun asUser() = User(this.email, this.name, this.adress, this.sub, this.lastOrder)
}
fun User.asEntity() = UserEntity(this.email, this.name, this.adress, this.sub, this.lastOrder)