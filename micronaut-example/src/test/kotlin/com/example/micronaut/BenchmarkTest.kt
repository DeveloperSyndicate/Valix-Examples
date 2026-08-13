package com.example.micronaut

import jakarta.validation.Validation
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertTrue

class BenchmarkTest {

    @Test
    fun runRealTimeBenchmark() {
        val iterations = 100_000
        val runs = 10

        // JSR-380 Validator setup
        val factory = Validation.buildDefaultValidatorFactory()
        val jsrValidator = factory.validator

        // Valix compiled validator
        val valixValidator = com.example.micronaut.generated.CreateUserRequestValidator

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

        // Warm up
        repeat(10_000) { i ->
            valixValidator.validate(valixPayloads[i % valixPayloads.size])
            jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
        }

        // ==========================================
        // 1. Sequential Runs
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
                    jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
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

        // ==========================================
        // 2. Parallel Runs (10 concurrent threads)
        // ==========================================
        val threadPool = java.util.concurrent.Executors.newFixedThreadPool(10)
        val tasksValix = (1..10).map {
            java.util.concurrent.Callable {
                measureTimeMillis {
                    repeat(iterations) { i ->
                        valixValidator.validate(valixPayloads[i % valixPayloads.size])
                    }
                }
            }
        }
        val tasksJsr = (1..10).map {
            java.util.concurrent.Callable {
                measureTimeMillis {
                    repeat(iterations) { i ->
                        jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
                    }
                }
            }
        }

        val valixParallelStart = System.currentTimeMillis()
        val futuresValix = threadPool.invokeAll(tasksValix)
        val valixParallelTotalTime = System.currentTimeMillis() - valixParallelStart
        val valixIndividualTimes = futuresValix.map { it.get() }

        val jsrParallelStart = System.currentTimeMillis()
        val futuresJsr = threadPool.invokeAll(tasksJsr)
        val jsrParallelTotalTime = System.currentTimeMillis() - jsrParallelStart
        val jsrParallelIndividualTime = futuresJsr.map { it.get() }

        threadPool.shutdown()

        val valixParallelOpsPerSec = ((iterations * 10).toDouble() / valixParallelTotalTime) * 1000
        val jsrParallelOpsPerSec = ((iterations * 10).toDouble() / jsrParallelTotalTime) * 1000
        val parallelImprovementFactor = jsrParallelTotalTime.toDouble() / valixParallelTotalTime

        println("\n==================================================")
        println("   MICRONAUT VALIX VS JSR-380 BENCHMARK RESULTS   ")
        println("==================================================")
        println("Iterations per Run: $iterations")
        println("\n--- SEQUENTIAL RUN AVG ($runs RUNS) ---")
        println("Valix:")
        println("  Average Time: ${String.format("%.2f", avgValixTime)}ms")
        println("  Throughput: ${valixOpsPerSec.toLong()} ops/sec")
        println("JSR-380:")
        println("  Average Time: ${String.format("%.2f", avgJsrTime)}ms")
        println("  Throughput: ${jsrOpsPerSec.toLong()} ops/sec")
        println("Performance Factor: ${String.format("%.2f", improvementFactor)}x faster")

        println("\n--- PARALLEL RUN (10 CONCURRENT THREADS) ---")
        println("Valix:")
        println("  Total Wall Time: ${valixParallelTotalTime}ms")
        println("  Average Thread Time: ${String.format("%.2f", valixIndividualTimes.average())}ms")
        println("  Combined Throughput: ${valixParallelOpsPerSec.toLong()} ops/sec")
        println("JSR-380:")
        println("  Total Wall Time: ${jsrParallelTotalTime}ms")
        println("  Average Thread Time: ${String.format("%.2f", jsrParallelIndividualTime.average())}ms")
        println("  Combined Throughput: ${jsrParallelOpsPerSec.toLong()} ops/sec")
        println("Performance Factor: ${String.format("%.2f", parallelImprovementFactor)}x faster")
        println("==================================================\n")

        assertTrue(avgValixTime > 0)
    }
}
