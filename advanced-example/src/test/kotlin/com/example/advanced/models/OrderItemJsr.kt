package com.example.advanced.models

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class OrderItemJsr(
    @field:NotBlank
    val productId: String,
    
    @field:Min(1)
    val quantity: Int,
    
    @field:Positive
    val price: Double
)
