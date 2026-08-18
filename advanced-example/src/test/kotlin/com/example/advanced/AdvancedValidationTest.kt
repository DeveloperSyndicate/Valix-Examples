package com.example.advanced

import com.example.advanced.models.Address
import com.example.advanced.models.OrderItem
import com.example.advanced.models.OrderRequest
import com.example.advanced.models.AccountRegistration
import org.junit.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class AdvancedValidationTest {

    @Test
    fun testNestedValidationCascades() {
        val address = Address("123 Main St", "Chicago", "60601")
        val item = OrderItem("product-id", 3, 49.99)
        
        val validOrder = OrderRequest("cust-123", address, listOf(item))
        val result = com.example.advanced.models.generated.OrderRequestValidator.validate(validOrder)
        assertTrue(result.valid, "Valid order request tree should pass validation checks")

        val invalidAddress = Address("", "Chicago", "606") // street blank, zip code size invalid
        val invalidOrder = OrderRequest("", invalidAddress, emptyList()) // customerId blank, empty items list
        
        val invalidResult = com.example.advanced.models.generated.OrderRequestValidator.validate(invalidOrder)
        assertFalse(invalidResult.valid, "Invalid nested order tree must fail validation")

        val errorPaths = invalidResult.errors.map { it.path }.toSet()
        // Verify path resolution propagates correctly through nested structures
        assertTrue(errorPaths.contains("customerId"), "Should catch root customerId error")
        assertTrue(errorPaths.contains("shippingAddress.street"), "Should resolve nested street path")
        assertTrue(errorPaths.contains("shippingAddress.zipCode"), "Should resolve nested zipCode path")
        assertTrue(errorPaths.contains("items"), "Should catch empty items list validation")
    }

    @Test
    fun testCustomPasswordStrengthConstraint() {
        val validAccount = AccountRegistration("user1", "SecurePass123!")
        val validResult = com.example.advanced.models.generated.AccountRegistrationValidator.validate(validAccount)
        assertTrue(validResult.valid, "Password meeting custom constraints should be valid")

        val invalidAccount = AccountRegistration("user1", "simplepwd")
        val invalidResult = com.example.advanced.models.generated.AccountRegistrationValidator.validate(invalidAccount)
        assertFalse(invalidResult.valid, "Simple password must violate password strength rules")
        
        val error = invalidResult.errors.firstOrNull { it.field == "password" }
        assertTrue(error != null, "Should return a validation error for password field")
        assertTrue(error.message.contains("uppercase letter"), "Should supply the specified validation warning message")
    }
}
