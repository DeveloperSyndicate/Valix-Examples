package com.example.android

import io.valix.annotations.Email
import io.valix.annotations.Min
import io.valix.annotations.NotBlank
import io.valix.annotations.NotNull
import jakarta.validation.constraints.Min as JsrMin
import jakarta.validation.constraints.NotBlank as JsrNotBlank
import jakarta.validation.constraints.NotNull as JsrNotNull
import jakarta.validation.constraints.Email as JsrEmail

/**
 * Domain model representing a user registration request, validated at compile-time by Valix.
 */
data class RegistrationRequest(
    @NotNull
    @NotBlank
    val username: String?,

    @NotNull
    @Email
    val email: String?,

    @NotNull
    @Min(18)
    val age: Int?
)

/**
 * Domain model representing the same user registration request, validated at runtime by Hibernate Validator (JSR-380).
 */
data class RegistrationRequestJsr(
    @field:JsrNotNull
    @field:JsrNotBlank
    val username: String?,

    @field:JsrNotNull
    @field:JsrEmail
    val email: String?,

    @field:JsrNotNull
    @field:JsrMin(18)
    val age: Int?
)
