package store_stock.article.domain
import jakarta.persistence.*
import java.util.Date
import java.util.UUID
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

@Entity
@Table(name = "Article")
data class article(
    @Id
    val id:UUID=UUID.randomUUID(),
    @field:Size(min=3, max = 15)
    val firstname:String,
    @field:Size(min=3, max = 15)
    val lastname:String,
    @field:PositiveOrZero
    val price:Float,
    @field:PositiveOrZero
    val stock:Int,
    @field:PastOrPresent
    val lastUpdate: Date
)