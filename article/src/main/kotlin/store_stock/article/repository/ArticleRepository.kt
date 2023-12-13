package store_stock.article.repository

import store_stock.article.domain.Article
import java.util.UUID

interface ArticleRepository {
    fun create(article: Article): Result<Article>
    fun list(price: Float? = null): List<Article>
    fun get(id: UUID): Article?
    fun update(article: Article): Result<Article>
    fun delete(id: UUID): Article?
}