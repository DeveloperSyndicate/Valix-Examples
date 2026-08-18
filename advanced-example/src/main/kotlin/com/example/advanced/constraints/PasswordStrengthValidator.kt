package com.example.advanced.constraints

import io.valix.core.ConstraintValidator
import io.valix.core.ValidationContext

class PasswordStrengthValidator : ConstraintValidator<String> {
    private val regex = Regex("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#\\$%^&*()_+={}\\[\\]|\\\\:;\"'<>,.?/~`\\-]).{8,}$")

    override fun validate(value: String, context: ValidationContext): Boolean {
        return regex.matches(value)
    }
}
