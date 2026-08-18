package com.example.kmp

import io.valix.annotations.NotBlank
import io.valix.annotations.Email
import io.valix.annotations.MinLength
import io.valix.annotations.MaxLength

data class UserProfile(
    @NotBlank
    val displayName: String,
    
    @Email
    val emailAddress: String,
    
    @MinLength(6)
    @MaxLength(20)
    val password: String
)
