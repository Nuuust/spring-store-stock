package storestock.panier.domain

import java.util.*

data class Cart(val email: String, val itemId: UUID, val qte: Int)
