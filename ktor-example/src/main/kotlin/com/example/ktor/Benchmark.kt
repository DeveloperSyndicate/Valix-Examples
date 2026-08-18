package com.example.ktor

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import jakarta.validation.Validation
import kotlin.system.measureNanoTime

fun runBenchmark() {
    println("--- BENCHMARK RUNNER START ---")
    val payload = RegistrationRequest("john_doe", "john@example.com", 30)

    // Valix validator initialization
    val valixValidator = com.example.ktor.generated.RegistrationRequestValidator
    
    // Warm-up
    println("Performing deep warm-up for Valix...")
    var valixSink = 0
    repeat(10_000_000) { 
        val res = valixValidator.validate(payload, failFast = true)
        if (res.valid) {
            valixSink += res.errors.size + 1
        }
    }

    println("Running Valix benchmarks (1,000,000 iterations)...")
    val valixTime = measureNanoTime {
        repeat(1_000_000) { 
            val res = valixValidator.validate(payload, failFast = true)
            if (res.valid) {
                valixSink += res.errors.size + 1
            }
        }
    }
    val valixAvg = valixTime / 1_000_000.0

    // Hibernate Validator initialization
    println("Initializing Hibernate validation factory...")
    val hvValidator = Validation.byDefaultProvider()
        .configure()
        .messageInterpolator(ParameterMessageInterpolator())
        .buildValidatorFactory()
        .validator

    // Warm-up
    println("Performing deep warm-up for Hibernate...")
    var hibernateSink = 0
    repeat(10_000_000) { 
        val violations = hvValidator.validate(payload)
        if (violations.isEmpty()) {
            hibernateSink += violations.size + 1
        }
    }

    println("Running Hibernate benchmarks (1,000,000 iterations)...")
    val hvTime = measureNanoTime {
        repeat(1_000_000) { 
            val violations = hvValidator.validate(payload)
            if (violations.isEmpty()) {
                hibernateSink += violations.size + 1
            }
        }
    }
    val hvAvg = hvTime / 1_000_000.0

    println("--- BENCHMARK RESULTS ---")
    println("Valix Average: ${valixAvg} ns (${valixAvg / 1_000_000.0} ms) [Sink: $valixSink]")
    println("Hibernate Average: ${hvAvg} ns (${hvAvg / 1_000_000.0} ms) [Sink: $hibernateSink]")
    println("Speedup factor: ${hvAvg / valixAvg}x")
    println("-------------------------")
}

fun main() {
    runBenchmark()
}
