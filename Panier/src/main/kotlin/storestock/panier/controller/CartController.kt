package storestock.panier.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import storestock.panier.controller.dto.CartDTO
import storestock.panier.controller.dto.asCartDTO
import storestock.panier.repository.CartRepository
import java.util.UUID


@RestController
@Validated
class CartController(val cartRepository: CartRepository) {

    @Operation(summary = "Add item to cart")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "ItemCart created",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = CartDTO::class)
                )]),ApiResponse(responseCode = "409", description = "ItemCart Conflict",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = UUID::class))])])
    @PostMapping("/api/cart")
    fun addToCart(@RequestBody @Valid cart: CartDTO): ResponseEntity<Any> =
            cartRepository.addToCart(cart.asCart()).fold(
                    { success -> ResponseEntity.ok(success.asCartDTO()) },
                    { failure -> ResponseEntity.badRequest().body(failure.message) }
            )

    @Operation(summary = "List Carts")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List All items of all Carts",
                content = [Content(mediaType = "application/json",
                        array = ArraySchema(
                                schema = Schema(implementation = CartDTO::class))
                )])])
    @GetMapping("/api/carts")
    fun listCarts() =
            cartRepository.listCarts()
                    .map { it.asCartDTO() }
                    .let {
                        ResponseEntity.ok(it)
                    }
    @Operation(summary = "Delete ItemCart by email & itemId")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "ItemCart deleted"),
        ApiResponse(responseCode = "400", description = "ItemCart not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])
    ])
    @DeleteMapping("/api/cart/{email}/{itemId}")
    fun delete(@PathVariable @Email email: String, @PathVariable itemId: UUID): ResponseEntity<Any> {
        val deleted = cartRepository.deleteItemCart(email,itemId)
        return if (deleted == null) {
            ResponseEntity.badRequest().body("CartItem not found")
        } else {
            ResponseEntity.noContent().build()
        }
    }
    @Operation(summary = "Update a CartItem by email & itemId")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "CartItem updated",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = CartDTO::class))]),
        ApiResponse(responseCode = "400", description = "Invalid request",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])])
    @PutMapping("/api/cart/update")
    fun update(@RequestBody @Valid cart: CartDTO): ResponseEntity<Any> =
            cartRepository.updateCart(cart.asCart()).fold(
                    { success -> ResponseEntity.ok(success.asCartDTO()) },
                    { failure -> ResponseEntity.badRequest().body(failure.message) }
            )

    @Operation(summary = "Validate the cart / Update quantity / Clear for the user")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Cart Validated"),
        ApiResponse(responseCode = "400", description = "Error",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])
    ])
    @GetMapping("/api/cart/{email}")
    fun validate(@PathVariable @Email email: String): ResponseEntity<Any> {
        return cartRepository.validateCart(email).fold(
                onSuccess = { carts ->
                    ResponseEntity.ok(carts.map { it.asCartDTO() })
                },
                onFailure = { error ->
                    ResponseEntity.badRequest().body(error.message)
                }
        )
    }

}