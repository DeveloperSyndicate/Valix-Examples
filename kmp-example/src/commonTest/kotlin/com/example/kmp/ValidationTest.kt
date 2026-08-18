package com.example.kmp

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class ValidationTest {

    @Test
    fun testValidUser() {
        val user = UserProfile("Kotlin Dev", "kmp@example.com", "secure123")
        val result = UserProfileValidator.validate(user)
        assertTrue(result.valid, "Valid user profile should pass checks")
    }

    @Test
    fun testInvalidUser() {
        val user = UserProfile("", "not-an-email", "123")
        val result = UserProfileValidator.validate(user)
        assertFalse(result.valid, "Invalid user profile must fail checks")
        
        val fieldsWithErrors = result.errors.map { it.field }.toSet()
        assertTrue(fieldsWithErrors.contains("displayName"))
        assertTrue(fieldsWithErrors.contains("emailAddress"))
        assertTrue(fieldsWithErrors.contains("password"))
    }
}
