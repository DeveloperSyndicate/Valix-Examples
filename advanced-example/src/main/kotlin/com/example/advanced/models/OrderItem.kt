package com.example.advanced.models

import io.valix.annotations.NotBlank
import io.valix.annotations.Min
import io.valix.annotations.Positive

data class OrderItem(
    @NotBlank
    val productId: String,
    
    @Min(1)
    val quantity: Int,
    
    @Positive
    val price: Double
)
