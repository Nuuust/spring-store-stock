package store_stock.article.repository.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.sql.Update
import store_stock.article.domain.Article
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "articles")
class ArticleEntity(
    @Id val id: UUID=UUID.randomUUID(),
    val name: String,
    val price:Float,
    val stock:Int,
    val lastUpdate: Date
) {
    fun asArticle() = Article(this.id, this.name, this.price,this.stock,this.lastUpdate)
}
fun Article.asEntity() = ArticleEntity(this.id, this.name, this.price,this.stock,this.lastUpdate)