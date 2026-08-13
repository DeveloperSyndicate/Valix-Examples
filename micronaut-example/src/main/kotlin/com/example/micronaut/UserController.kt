package com.example.micronaut

import io.valix.micronaut.ValixValidated
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.validation.Valid

@Controller("/api/users")
class UserController {

    @Post("/valix")
    @ValixValidated
    fun createUserValix(@Body request: CreateUserRequest): HttpResponse<Map<String, Any>> {
        return HttpResponse.ok(mapOf(
            "status" to "success",
            "message" to "User created successfully using Valix validation",
            "data" to request
        ))
    }

    @Post("/jsr")
    fun createUserJsr(@Body @Valid request: CreateUserRequestJsr): HttpResponse<Map<String, Any>> {
        return HttpResponse.ok(mapOf(
            "status" to "success",
            "message" to "User created successfully using JSR-380 validation",
            "data" to request
        ))
    }
}
