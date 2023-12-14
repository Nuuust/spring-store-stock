package store_stock.article.errors

import java.util.UUID

sealed class Errors(message: String = "", cause: Exception? = null) :
    Exception(message, cause)

class ArticleNotFoundError(id: UUID) : Errors(message = "Article $id not found")