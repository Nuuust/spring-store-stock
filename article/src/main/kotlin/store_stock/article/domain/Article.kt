package store_stock.article.domain
import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
//test
@Entity
@Table(name = "Article")
data class Article(
    @Id
    val id:UUID=UUID.randomUUID(),
    @field:Size(min=3, max = 30)
    val name:String,
    @field:PositiveOrZero
    val price:Float,
    @field:PositiveOrZero
    val stock:Int,
    @field:PastOrPresent
    val lastUpdate: Date
)