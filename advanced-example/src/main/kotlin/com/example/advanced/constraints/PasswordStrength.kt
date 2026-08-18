package com.example.advanced.constraints

import io.valix.annotations.Constraint
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validator = PasswordStrengthValidator::class)
annotation class PasswordStrength(
    val message: String = "Password must contain at least one digit, one uppercase letter, and one special character",
    val messageKey: String = "valix.custom.passwordstrength",
    val groups: Array<KClass<*>> = []
)
