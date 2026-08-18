package com.example.kmp

import io.valix.core.ValidationResult
import io.valix.core.ValixValidator
import io.valix.runtime.valixDsl

object UserProfileValidator {
    // Compile programmatic validator to run on commonMain across KMP targets without platform KSP dependency resolution errors
    private val validator: ValixValidator<UserProfile> = valixDsl {
        field(UserProfile::displayName) {
            notBlank("must not be blank")
        }
        field(UserProfile::emailAddress) {
            notBlank("must not be blank")
            email("invalid email")
        }
        field(UserProfile::password) {
            notBlank("must not be blank")
            minLength(6, "minimum length is 6")
            rule("MAX_LENGTH", "maximum length is 20") { it.length <= 20 }
        }
    }

    fun validate(profile: UserProfile): ValidationResult {
        return validator.validate(profile)
    }
}
