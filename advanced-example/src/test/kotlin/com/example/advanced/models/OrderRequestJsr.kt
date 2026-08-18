package com.example.advanced.models

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class OrderRequestJsr(
    @field:NotBlank
    val customerId: String,
    
    @field:Valid
    val shippingAddress: AddressJsr,
    
    @field:Valid
    @field:NotEmpty
    val items: List<OrderItemJsr>
)
