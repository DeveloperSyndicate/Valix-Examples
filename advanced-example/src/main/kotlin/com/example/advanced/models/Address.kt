package com.example.advanced.models

import io.valix.annotations.NotBlank
import io.valix.annotations.MinLength
import io.valix.annotations.MaxLength

data class Address(
    @NotBlank
    val street: String,
    
    @NotBlank
    val city: String,
    
    @NotBlank
    @MinLength(5)
    @MaxLength(5)
    val zipCode: String
)
