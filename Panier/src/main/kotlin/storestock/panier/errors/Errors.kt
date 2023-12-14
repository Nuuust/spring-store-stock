package storestock.panier.errors

sealed class Errors(message: String = "", cause: Exception? = null) :
        Exception(message, cause)

class CartNotFoundError(email: String) : Errors(message = "Cart of $email not found")