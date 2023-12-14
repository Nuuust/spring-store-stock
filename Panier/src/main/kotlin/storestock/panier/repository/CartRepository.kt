package storestock.user.repository

import storestock.Panier.domain.Cart

interface CartRepository {
    fun addToCart(cart: Cart): Result<Cart>
    fun listCarts(): List<Cart>
    fun updateCart(cart: Cart): Result<Cart>
    fun deleteItemCart(email: String): Cart?
    fun ValidateCart(email: String): Cart?
}