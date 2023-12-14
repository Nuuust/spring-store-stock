package storestock.panier.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import storestock.panier.domain.Cart
import java.util.*


@Entity
@Table(name = "users")
class CartEntity(
        @Id
        @field:Email val email: String,
        val itemId: UUID,
        val qte: Int
) {
    fun asCart() = Cart( this.email,this.itemId, this.qte)
}
fun Cart.asEntity() = CartEntity(this.email, this.itemId, this.qte)
