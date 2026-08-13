package com.example.spring

import jakarta.validation.Validator as JsrValidator
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.system.measureTimeMillis

@RestController
@RequestMapping("/api/benchmark")
class BenchmarkController(
    private val jsrValidator: JsrValidator
) {

    @GetMapping
    fun runBenchmark(): ResponseEntity<Map<String, Any>> {
        val iterations = 100_000

        // Create a pool of payloads, some valid and some invalid
        val valixPayloads = listOf(
            CreateUserRequest("john_doe", "john@example.com", 25), // valid
            CreateUserRequest("", "invalid-email", 12),           // invalid
            CreateUserRequest("alice", "", 30),                   // invalid
            CreateUserRequest(null, "alice@example.com", null)    // invalid
        )

        val jsrPayloads = listOf(
            CreateUserRequestJsr("john_doe", "john@example.com", 25), // valid
            CreateUserRequestJsr("", "invalid-email", 12),           // invalid
            CreateUserRequestJsr("alice", "", 30),                   // invalid
            CreateUserRequestJsr(null, "alice@example.com", null)    // invalid
        )

        // Resolve compiled Valix validator.
        val valixValidator = com.example.spring.generated.CreateUserRequestValidator

        // Warm up
        repeat(10_000) { i ->
            valixValidator.validate(valixPayloads[i % valixPayloads.size])
            jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
        }

        // Measure Valix
        val valixTime = measureTimeMillis {
            repeat(iterations) { i ->
                valixValidator.validate(valixPayloads[i % valixPayloads.size])
            }
        }

        // Measure JSR-380 / Hibernate Validator
        val jsrTime = measureTimeMillis {
            repeat(iterations) { i ->
                jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
            }
        }

        val valixOpsPerSec = (iterations.toDouble() / valixTime) * 1000
        val jsrOpsPerSec = (iterations.toDouble() / jsrTime) * 1000

        return ResponseEntity.ok(mapOf(
            "iterations" to iterations,
            "valix" to mapOf(
                "totalTimeMs" to valixTime,
                "opsPerSecond" to valixOpsPerSec.toLong()
            ),
            "jsr380" to mapOf(
                "totalTimeMs" to jsrTime,
                "opsPerSecond" to jsrOpsPerSec.toLong()
            ),
            "performanceImprovementFactor" to String.format("%.2f", jsrTime.toDouble() / valixTime)
        ))
    }
}
