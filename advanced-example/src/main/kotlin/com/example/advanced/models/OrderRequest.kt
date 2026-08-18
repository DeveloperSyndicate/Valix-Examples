package com.example.advanced.models

import io.valix.annotations.NotBlank
import io.valix.annotations.NotEmpty
import io.valix.annotations.Valid

data class OrderRequest(
    @NotBlank
    val customerId: String,
    
    @Valid
    val shippingAddress: Address,
    
    @Valid
    @NotEmpty
    val items: List<OrderItem>
)
