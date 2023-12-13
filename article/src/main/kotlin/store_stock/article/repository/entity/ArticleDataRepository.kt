package store_stock.article.repository.entity

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import store_stock.article.domain.Article
import store_stock.article.repository.ArticleRepository
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Repository
class ArticleDataRepository(private val jpa:ArticleJpaRepository):ArticleRepository {
    override fun create(article:Article): Result<Article> = if (jpa.findById(article.id).isPresent) {
        Result.failure(Exception("Article already in DB"))
    } else {
        val saved = jpa.save(article.asEntity())
        Result.success(saved.asArticle())
    }

    override fun list(price:Float?): List<Article> {
        return if (price == null) {
            jpa.findAll().map { it.asArticle() }
        } else {
            jpa.findAllByPrice(price).map { it.asArticle() }
        }
    }

    override fun get(id: UUID): Article? {
        return jpa.findById(id)
            .map { it.asArticle() }.get()
    }

    override fun update(article: Article): Result<Article> = if(jpa.findById(article.id).isPresent) {
        val saved=jpa.save(article.asEntity())
        Result.success(saved.asArticle())
    }else{
        Result.failure(Exception("Artile not in DB"))
    }

    override fun delete(id:UUID): Article? {
        return jpa.findById(id)
                .also { jpa.deleteById(id) }
                .map { it.asArticle() }
                .getOrNull()
    }
}
interface ArticleJpaRepository : JpaRepository<ArticleEntity, UUID> {
    fun findAllByPrice(price: Float): List<ArticleEntity>
}