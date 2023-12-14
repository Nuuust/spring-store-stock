package storestock.Panier.domain

import java.util.*

data class Cart(val email: String, val ItemId: UUID, val qte: Int)
