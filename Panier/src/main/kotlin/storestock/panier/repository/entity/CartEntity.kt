package storestock.user.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import storestock.Panier.domain.Cart
import java.util.*


@Entity
@Table(name = "users")
class CartEntity(
        @Id
        @field:Email val email: String,
        @Id
        val ItemId: UUID,
        val qte: Int
) {
    fun asCart() = Cart( this.email,this.ItemId, this.qte)
}
fun Cart.asEntity() = CartEntity(this.email, this.ItemId, this.qte)
