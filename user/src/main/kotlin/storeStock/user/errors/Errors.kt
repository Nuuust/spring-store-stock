package storeStock.user.errors

sealed class Errors(message: String = "", cause: Exception? = null) :
        Exception(message, cause)

class UserNotFoundError(email: String) : Errors(message = "User $email not found")