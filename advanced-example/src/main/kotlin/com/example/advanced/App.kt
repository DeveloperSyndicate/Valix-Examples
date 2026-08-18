package com.example.advanced

import com.example.advanced.models.Address
import com.example.advanced.models.OrderItem
import com.example.advanced.models.OrderRequest
import com.example.advanced.models.AccountRegistration

fun main() {
    println("--- ADVANCED VALIX SHOWCASE ---")
    
    // 1. Nested Validation Showcase
    val invalidAddress = Address("", "New York", "12") // blank street, invalid zip length
    val invalidItem = OrderItem("prod-1", 0, -5.0)    // zero quantity, negative price
    val validItem = OrderItem("prod-2", 2, 19.99)
    
    val order = OrderRequest(
        customerId = "", // blank customerId
        shippingAddress = invalidAddress,
        items = listOf(invalidItem, validItem)
    )
    
    val orderResult = com.example.advanced.models.generated.OrderRequestValidator.validate(order)
    println("Order Request Valid: ${orderResult.valid}")
    println("Detected Errors:")
    orderResult.errors.forEach { err ->
        println("  - Path: '${err.path}' | Field: '${err.field}' | Message: '${err.message}'")
    }

    println()

    // 2. Custom Annotation Showcase
    val account = AccountRegistration("admin", "weakpass") // invalid password strength
    val accountResult = com.example.advanced.models.generated.AccountRegistrationValidator.validate(account)
    println("Account Registration Valid: ${accountResult.valid}")
    accountResult.errors.forEach { err ->
        println("  - Field: '${err.field}' | Message: '${err.message}'")
    }
}
