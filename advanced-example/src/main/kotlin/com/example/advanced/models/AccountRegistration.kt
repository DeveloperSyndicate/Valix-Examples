package com.example.advanced.models

import io.valix.annotations.NotBlank
import com.example.advanced.constraints.PasswordStrength

data class AccountRegistration(
    @NotBlank
    val username: String,
    
    @PasswordStrength
    val password: String
)
