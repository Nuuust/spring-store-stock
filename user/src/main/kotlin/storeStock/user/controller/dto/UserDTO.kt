package storeStock.user.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import storeStock.user.domain.User
import java.util.Date

data class UserDTO(
        @field:Email val email: String,
        @field:Size(min = 1, max = 30) val name: String,
        @field:Size(min = 1, max = 300) val adress: String,
        val sub: Boolean,
        val lastOrder:Date
) {

    fun asUser() = User(email, name, adress, sub,lastOrder)
}

fun User.asUserDTO() = UserDTO(this.email, this.name, this.adress, this.sub, this.lastOrder)