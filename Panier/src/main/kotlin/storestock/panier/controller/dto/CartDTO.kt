package storestock.panier.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import storestock.panier.domain.Cart
import java.util.*

data class CartDTO(
        @field:Email val email: String,
        val itemId: UUID,
        @field:Min(1) val qte: Int

) {

    fun asCart() = Cart( email, itemId, qte)
}

fun Cart.asCartDTO() = CartDTO( this.email, this.itemId, this.qte)