package com.example.advanced.models

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddressJsr(
    @field:NotBlank
    val street: String,
    
    @field:NotBlank
    val city: String,
    
    @field:NotBlank
    @field:Size(min = 5, max = 5)
    val zipCode: String
)
