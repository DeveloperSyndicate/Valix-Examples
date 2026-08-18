package com.example.advanced

import com.example.advanced.models.*
import jakarta.validation.Validation
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class NestedObjectBenchmarkTest {

    @Test
    fun runNestedBenchmark() {
        val iterations = 50_000
        val runs = 5

        // JSR-380 Validator setup
        val factory = Validation.buildDefaultValidatorFactory()
        val jsrValidator = factory.validator

        // Valix compiled validator
        val valixValidator = com.example.advanced.models.generated.OrderRequestValidator

        // Complex Payload Setup
        val valixPayloads = listOf(
            // Valid OrderRequest
            OrderRequest("cust-100", Address("55 Broadway", "New York", "10006"), listOf(
                OrderItem("prod-1", 1, 9.99),
                OrderItem("prod-2", 10, 1.49)
            )),
            // Invalid OrderRequest (blank ZIP and empty customerId)
            OrderRequest("", Address("55 Broadway", "New York", ""), listOf(
                OrderItem("prod-1", 0, -1.0)
            ))
        )

        val jsrPayloads = listOf(
            // Valid OrderRequestJsr
            OrderRequestJsr("cust-100", AddressJsr("55 Broadway", "New York", "10006"), listOf(
                OrderItemJsr("prod-1", 1, 9.99),
                OrderItemJsr("prod-2", 10, 1.49)
            )),
            // Invalid OrderRequestJsr
            OrderRequestJsr("", AddressJsr("55 Broadway", "New York", ""), listOf(
                OrderItemJsr("prod-1", 0, -1.0)
            ))
        )

        // Warm up JIT execution paths
        repeat(10_000) { i ->
            valixValidator.validate(valixPayloads[i % valixPayloads.size])
            jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
        }

        // Timing loops
        val valixTimes = mutableListOf<Long>()
        val jsrTimes = mutableListOf<Long>()

        for (run in 1..runs) {
            val valixTime = measureTimeMillis {
                var sink = 0
                repeat(iterations) { i ->
                    val result = valixValidator.validate(valixPayloads[i % valixPayloads.size])
                    sink += result.errors.size
                }
            }
            val jsrTime = measureTimeMillis {
                var sink = 0
                repeat(iterations) { i ->
                    val violations = jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
                    sink += violations.size
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
        println("   NESTED ENTITY GRAPH BENCHMARK (50,000 RUNS)   ")
        println("==================================================")
        println("Valix Average: ${String.format("%.2f", avgValixTime)} ms (${valixOpsPerSec.toLong()} ops/sec)")
        println("JSR-380 Average: ${String.format("%.2f", avgJsrTime)} ms (${jsrOpsPerSec.toLong()} ops/sec)")
        println("Valix Nested Performance Factor: ${String.format("%.2f", improvementFactor)}x Faster")
        println("==================================================\n")

        assertTrue(avgValixTime > 0)
    }
}
