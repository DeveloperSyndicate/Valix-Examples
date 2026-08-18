package com.example.ktor

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import io.valix.annotations.NotBlank
import io.valix.annotations.Email
import io.valix.annotations.Min

@Serializable
data class RegistrationRequest(
    @NotBlank
    val username: String,

    @Email
    val email: String,

    @Min(18)
    val age: Int
)

data class RegistrationRequestJsr(
    @field:jakarta.validation.constraints.NotBlank
    val username: String,

    @field:jakarta.validation.constraints.Email
    val email: String,

    @field:jakarta.validation.constraints.Min(18)
    val age: Int
)

@Serializable
data class ValidationErrorDto(val field: String, val message: String)

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        post("/register") {
            val request = call.receive<RegistrationRequest>()
            val validationResult = com.example.ktor.generated.RegistrationRequestValidator.validate(request)

            if (validationResult.valid) {
                call.respond(HttpStatusCode.OK, mapOf("status" to "registered"))
            } else {
                val errors = validationResult.errors.map { ValidationErrorDto(it.field, it.message) }
                call.respond(HttpStatusCode.BadRequest, errors)
            }
        }
    }
}
