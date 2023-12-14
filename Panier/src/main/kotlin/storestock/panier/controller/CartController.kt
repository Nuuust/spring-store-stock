package storestock.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController
import storestock.user.controller.dto.CartDTO
import storestock.user.controller.dto.asUserDTO
import storestock.user.repository.CartRepository


@RestController
@Validated
class CartController(val cartRepository: CartRepository) {

    @Operation(summary = "Add item to cart")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Item added created",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = CartDTO::class)
                )]),])
    @PostMapping("/api/cart")
    fun addToCart(@RequestBody @Valid cart: CartDTO): ResponseEntity<CartDTO> =
            cartRepository.addToCart(cart.asCart()).fold(
                    { success -> ResponseEntity.status(HttpStatus.CREATED).body(success.asUserDTO()) },
                    { failure -> ResponseEntity.status(HttpStatus.CONFLICT).build() })

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
                    .map { it.asUserDTO() }
                    .let {
                        ResponseEntity.ok(it)
                    }

    /*@Operation(summary = "Get user by email")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "The user",
                content = [
                    Content(mediaType = "application/json",
                            schema = Schema(implementation = CartDTO::class))]),
        ApiResponse(responseCode = "404", description = "User not found")
    ])
    @GetMapping("/api/users/{email}")
    fun findOne(@PathVariable @Email email: String): ResponseEntity<CartDTO> {
        val user = cartRepository.get(email)
        return if (user != null) {
            ResponseEntity.ok(user.asUserDTO())
        } else {
            throw UserNotFoundError(email)
        }
    }

    @Operation(summary = "Update a user by email")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "User updated",
                content = [Content(mediaType = "application/json",
                        schema = Schema(implementation = CartDTO::class))]),
        ApiResponse(responseCode = "400", description = "Invalid request",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])])
    @PutMapping("/api/users/{email}")
    fun update(@PathVariable @Email email: String, @RequestBody @Valid cart: CartDTO): ResponseEntity<Any> =
            if (email != cart.email) {
                ResponseEntity.badRequest().body("Invalid email")
            } else {
                cartRepository.update(cart.asCart()).fold(
                        { success -> ResponseEntity.ok(success.asUserDTO()) },
                        { failure -> ResponseEntity.badRequest().body(failure.message) }
                )
            }

    @Operation(summary = "Delete user by email")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "User deleted"),
        ApiResponse(responseCode = "400", description = "User not found",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = String::class))])
    ])
    @DeleteMapping("/api/users/{email}")
    fun delete(@PathVariable @Email email: String): ResponseEntity<Any> {
        val deleted = cartRepository.delete(email)
        return if (deleted == null) {
            ResponseEntity.badRequest().body("User not found")
        } else {
            ResponseEntity.noContent().build()
        }
    }*/
}