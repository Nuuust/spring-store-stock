package store_stock.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Null
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import store_stock.article.domain.Article
import store_stock.article.errors.ArticleNotFoundError
import store_stock.article.repository.ArticleRepository
import store_stock.user.controller.dto.ArticleDTO
import store_stock.user.controller.dto.asArticleDTO
import java.util.UUID

@RestController
@Validated
class ArticleController(val articleRepository: ArticleRepository){
    @Operation(summary = "Create article")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Article created",
            content = [Content(mediaType = "application/json",
                schema = Schema(implementation = ArticleDTO::class)
            )]),
        ApiResponse(responseCode = "409", description = "Article already exist",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = UUID::class))])])
    @PostMapping("/api/articles")
    fun create(@RequestBody @Valid article: ArticleDTO): ResponseEntity<ArticleDTO> =
        articleRepository.create(article.asArticle()).fold(
            { success -> ResponseEntity.status(HttpStatus.CREATED).body(success.asArticleDTO()) },
            { failure -> ResponseEntity.status(HttpStatus.CONFLICT).build() })


    @Operation(summary = "Get article")
    @ApiResponses(value=[
        ApiResponse(responseCode = "200", description = "The article",
            content = [
                Content(mediaType="application/json",
                    schema=Schema(implementation = ArticleDTO::class))]),
        ApiResponse(responseCode = "404", description = "Article not found")
    ])
    @GetMapping("api/articles/{id}")
    fun findOne(@PathVariable id:UUID):ResponseEntity<ArticleDTO>{
        val article=articleRepository.get(id)
        return if (article != null){
            ResponseEntity.ok(article.asArticleDTO())
        }else{
            throw ArticleNotFoundError(id)
        }
    }

    @Operation(summary = "List articles")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List articles",
            content = [Content(mediaType = "application/json",
                array = ArraySchema(
                    schema = Schema(implementation = ArticleDTO::class))
            )])])
    @GetMapping("/api/articles")
    fun list(@RequestParam(required = false) @Min(0) price: Float?) =
        articleRepository.list(price)
            .map { it.asArticleDTO() }
            .let {
                ResponseEntity.ok(it)
            }

    @Operation(summary = "Update a article by ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Article updated",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = ArticleDTO::class))]),
        ApiResponse(responseCode = "400", description = "Invalid request",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])])
    @PutMapping("/api/articles/{id}")
    fun update(@PathVariable id: UUID, @RequestBody @Valid article: ArticleDTO): ResponseEntity<Any> =
            if (id != article.id) {
                ResponseEntity.badRequest().body("Invalid email")
            } else {
                articleRepository.update(article.asArticle()).fold(
                        { success -> ResponseEntity.ok(success.asArticleDTO()) },
                        { failure -> ResponseEntity.badRequest().body(failure.message) }
                )
            }


    @Operation(summary = "Delete article by id")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "article deleted"),
        ApiResponse(responseCode = "400", description = "article not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])
    ])
    @DeleteMapping("/api/articles/{id}")
    fun delete(@PathVariable id:UUID, @RequestBody @Valid article: ArticleDTO): ResponseEntity<Any> {
        val deleted = articleRepository.delete(id)
        return if (deleted == null) {
            ResponseEntity.badRequest().body("article not found")
        } else {
            ResponseEntity.noContent().build()
        }
    }
}