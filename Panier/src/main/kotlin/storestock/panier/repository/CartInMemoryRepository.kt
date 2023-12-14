package storestock.panier.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.stereotype.Repository
import storestock.panier.domain.Cart
import java.util.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
@Repository
class CartInMemoryRepository : CartRepository {

    private val map = mutableMapOf<Pair<String, UUID>, Cart>()

    override fun addToCart(cart: Cart): Result<Cart> {
        val emailExists = doesEmailExist(cart.email)
        if (!emailExists) {
            return Result.failure(Exception("Email does not exist"))
        }

        val stock = getStockForItemId(cart.itemId)
        if (stock == null || stock-cart.qte<0) {
            return Result.failure(Exception("Not enough quantity"))
        }

        val previous = map.putIfAbsent(Pair(cart.email,cart.itemId), cart)
        return if (previous == null) {
            Result.success(cart)
        } else {
            Result.failure(Exception("CartItem already exit"))
        }
    }

    override fun listCarts(): List<Cart> {
        return map.values.toList()
    }

    override fun updateCart(cart: Cart): Result<Cart> {
        val stock = getStockForItemId(cart.itemId)
        if (stock == null || stock-cart.qte<0) {
            return Result.failure(Exception("Not enough quantity"))
        }

        val updated = map.replace(Pair(cart.email,cart.itemId), cart)
        return if (updated == null) {
            Result.failure(Exception("CartItem doesn't exit"))
        } else {
            Result.success(cart)
        }
    }

    override fun deleteItemCart(email: String,itemId: UUID) = map.remove(Pair(email,itemId))

    override fun validateCart(email: String): Result<List<Cart>> {
        val emailExists = doesEmailExist(email)
        if (!emailExists) {
            return Result.failure(Exception("Email does not exist"))
        }

        val itemsCart = map.filterKeys { it.first == email }.values.toList()
        itemsCart.forEach { cart ->
            val stock = getStockForItemId(cart.itemId)
            if (stock == null || stock - cart.qte < 0) {
                return Result.failure(Exception("No more enough quantity for item ${cart.itemId}"))
            }
        }

        itemsCart.forEach { cart ->
            updateArticle(cart)
        }

        itemsCart.forEach { cart ->
            deleteItemCart(cart.email,cart.itemId)
        }

        updateUser(email)

        return Result.success(itemsCart)
    }


}


fun doesEmailExist(email: String): Boolean {
    val url = URL("http://localhost:8080/api/users/$email")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    try {
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return true
        } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return false
        } else {
            return false
        }
    } finally {
        connection.disconnect()
    }
}

fun getStockForItemId(itemId: UUID): Int? {
    val url = URL("http://localhost:8081/api/articles/$itemId")
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"

    try {
        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val responseContent = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                responseContent.append(line)
            }
            reader.close()

            val jsonResponse = responseContent.toString()
            val stockValue = parseStockFromJson(jsonResponse)

            return stockValue
        } else {
            return null
        }
    } finally {
        connection.disconnect()
    }
}

fun parseStockFromJson(jsonResponse: String): Int? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("stock")?.asInt
}
fun parseNameFromJson(jsonResponse: String): String? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("name")?.asString
}
fun parsePriceFromJson(jsonResponse: String): Int? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("price")?.asInt
}

fun updateArticle(cart: Cart) {
    val getUrl = URL("http://localhost:8081/api/articles/${cart.itemId}")
    val getConn = getUrl.openConnection() as HttpURLConnection
    getConn.requestMethod = "GET"

    try {
        val responseCode = getConn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse = getConn.inputStream.bufferedReader().use { it.readText() }

            val stock = parseStockFromJson(jsonResponse)
            val name = parseNameFromJson(jsonResponse)
            val price = parsePriceFromJson(jsonResponse)
            val updatedStock = stock?.minus(cart.qte)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val lastUpdate = dateFormat.format(Date())

            val json = """
                {
                    "id": "${cart.itemId}",
                    "name": "$name",
                    "price": $price,
                    "stock": $updatedStock,
                    "lastUpdate": "$lastUpdate"
                }
            """.trimIndent()

            val putUrl = URL("http://localhost:8081/api/articles/${cart.itemId}")
            val putConn = putUrl.openConnection() as HttpURLConnection
            putConn.requestMethod = "PUT"
            putConn.doOutput = true
            putConn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            putConn.outputStream.use { os ->
                val input = json.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val putResponseCode = putConn.responseCode
            if (putResponseCode != HttpURLConnection.HTTP_OK) {
                println("Failed to update article ${cart.itemId}: Response code $putResponseCode")
            }
        } else {
            println("Failed to fetch article ${cart.itemId}: Response code $responseCode")
        }
    } finally {
        getConn.disconnect()
    }
}
fun parseUserNameFromJson(jsonResponse: String): String? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("name")?.asString
}
fun parseUserAdressFromJson(jsonResponse: String): String? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("adress")?.asString
}
fun parseUserSubFromJson(jsonResponse: String): Boolean? {
    val gson = Gson()
    val jsonObject = gson.fromJson(jsonResponse, JsonObject::class.java)
    return jsonObject?.get("sub")?.asBoolean
}

fun updateUser(email: String) {
    val getUrl = URL("http://localhost:8080/api/users/${email}")
    val getConn = getUrl.openConnection() as HttpURLConnection
    getConn.requestMethod = "GET"

    try {
        val responseCode = getConn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val jsonResponse = getConn.inputStream.bufferedReader().use { it.readText() }

            val name = parseUserNameFromJson(jsonResponse)
            val adress = parseUserAdressFromJson(jsonResponse)
            val sub = parseUserSubFromJson(jsonResponse)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            val lastUpdate = dateFormat.format(Date())

            val json = """
                {
                    "email": "$email",
                    "name": "$name",
                    "adress": "$adress",
                    "sub": $sub,
                    "lastOrder": "$lastUpdate"
                }
            """.trimIndent()

            val putUrl = URL("http://localhost:8080/api/users/${email}")
            val putConn = putUrl.openConnection() as HttpURLConnection
            putConn.requestMethod = "PUT"
            putConn.doOutput = true
            putConn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            putConn.outputStream.use { os ->
                val input = json.toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val putResponseCode = putConn.responseCode
            if (putResponseCode != HttpURLConnection.HTTP_OK) {
                println("Failed to update user ${email}: Response code $putResponseCode")
            }
        } else {
            println("Failed to fetch user ${email}: Response code $responseCode")
        }
    } finally {
        getConn.disconnect()
    }
}
