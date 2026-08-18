package com.example.ktor

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import jakarta.validation.Validation
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class BenchmarkTest {

    @Test
    fun runRealTimeBenchmark() {
        val iterations = 100_000
        val runs = 10

        // Hibernate Validator setup
        val hvValidator = Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(ParameterMessageInterpolator())
            .buildValidatorFactory()
            .validator

        // Valix compiled validator
        val valixValidator = com.example.ktor.generated.RegistrationRequestValidator

        val valixPayloads = listOf(
            RegistrationRequest("john_doe", "john@example.com", 25), // valid
            RegistrationRequest("", "invalid-email", 12),           // invalid
            RegistrationRequest("alice", "", 30)                    // invalid
        )

        // Hibernate JSR payload setup using Spring's pattern for testing JSR equivalents correctly
        val jsrPayloads = listOf(
            RegistrationRequestJsr("john_doe", "john@example.com", 25), // valid
            RegistrationRequestJsr("", "invalid-email", 12),           // invalid
            RegistrationRequestJsr("alice", "", 30)                    // invalid
        )

        // Warm up JVM paths (100,000 iterations to trigger JIT compiler optimization)
        repeat(100_000) { i ->
            valixValidator.validate(valixPayloads[i % valixPayloads.size])
            hvValidator.validate(jsrPayloads[i % jsrPayloads.size])
        }

        // ==========================================
        // Sequential Runs
        // ==========================================
        val valixTimes = mutableListOf<Long>()
        val jsrTimes = mutableListOf<Long>()

        for (run in 1..runs) {
            val valixTime = measureTimeMillis {
                repeat(iterations) { i ->
                    valixValidator.validate(valixPayloads[i % valixPayloads.size])
                }
            }
            val jsrTime = measureTimeMillis {
                repeat(iterations) { i ->
                    hvValidator.validate(jsrPayloads[i % jsrPayloads.size])
                }
            }
            valixTimes.add(valixTime)
            jsrTimes.add(jsrTime)
        }

        val avgValixTime = valixTimes.average()
        val avgJsrTime = jsrTimes.average()

        val valixOpsPerSec = (iterations.toDouble() / avgValixTime) * 1000
        val jsrOpsPerSec = (iterations.toDouble() / avgJsrTime) * 1000
        val improvementFactor = avgJsrTime / avgValixTime

        println("\n==================================================")
        println("       VALIX VS JSR-380 BENCHMARK RESULTS (KTOR)  ")
        println("==================================================")
        println("Iterations per Run: $iterations")
        println("Valix (Compile-Time Generated):")
        println("  Average Time: ${String.format("%.2f", avgValixTime)}ms")
        println("  Throughput: ${valixOpsPerSec.toLong()} ops/sec")
        println("JSR-380 (Hibernate Runtime Reflection):")
        println("  Average Time: ${String.format("%.2f", avgJsrTime)}ms")
        println("  Throughput: ${jsrOpsPerSec.toLong()} ops/sec")
        println("Performance Factor: ${String.format("%.2f", improvementFactor)}x faster")
        println("==================================================\n")

        assertTrue(avgValixTime > 0)
    }
}
