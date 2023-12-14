package storestock.panier.repository

import storestock.panier.domain.Cart
import java.util.UUID

interface CartRepository {
    fun addToCart(cart: Cart): Result<Cart>
    fun listCarts(): List<Cart>
    fun updateCart(cart: Cart): Result<Cart>
    fun deleteItemCart(email: String,itemId: UUID): Cart?
    fun validateCart(email: String): Result<List<Cart>>
}