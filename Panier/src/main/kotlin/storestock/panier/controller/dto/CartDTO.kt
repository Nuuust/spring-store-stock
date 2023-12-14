package storestock.user.controller.dto

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.Email
import storestock.Panier.domain.Cart
import java.util.*

data class CartDTO(
        @field:Email val email: String,
        val ItemId: UUID,
        val qte: Int

) {

    fun asCart() = Cart( email, ItemId, qte)
}

fun Cart.asUserDTO() = CartDTO( this.email, this.ItemId, this.qte)