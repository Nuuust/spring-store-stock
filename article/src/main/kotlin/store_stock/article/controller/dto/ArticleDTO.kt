package store_stock.article.controller.dto

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.*
import java.util.*
import store_stock.article.domain.Article

data class ArticleDTO(
    @Id
    @GeneratedValue
    val id: UUID = UUID.randomUUID(),
    @field:Size(min=3, max = 30)
    val name:String,
    @field:PositiveOrZero
    val price:Float,
    @field:PositiveOrZero
    val stock:Int,
    @field:PastOrPresent
    val lastUpdate: Date
) {
    fun asArticle()=Article(id,name,price,stock,lastUpdate)
}

fun Article.asArticleDTO() = ArticleDTO(this.id,this.name,this.price,this.stock,this.lastUpdate)