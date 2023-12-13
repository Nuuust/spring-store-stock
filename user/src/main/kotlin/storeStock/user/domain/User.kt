package storeStock.user.domain

import java.util.Date

data class User(val email: String, val name: String, val adress: String, val sub: Boolean, val lastOrder: Date)