package com.example.spring

import io.valix.spring.ValidValix
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {

    @PostMapping("/valix")
    fun createUserValix(
        @RequestBody @ValidValix request: CreateUserRequest
    ): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf(
            "status" to "success",
            "message" to "User created successfully using Valix validation",
            "data" to request
        ))
    }

    @PostMapping("/jsr")
    fun createUserJsr(
        @RequestBody @Valid request: CreateUserRequestJsr
    ): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf(
            "status" to "success",
            "message" to "User created successfully using Hibernate Validator JSR-380",
            "data" to request
        ))
    }
}
