package storeStock.user.repository

import storeStock.user.domain.User

interface UserRepository {
    fun create(user: User): Result<User>
    fun list(age: Int? = null): List<User>
    fun get(email: String): User?
    fun update(user: User): Result<User>
    fun delete(email: String): User?
}