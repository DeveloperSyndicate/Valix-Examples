package com.example.kmp

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource

class KmpBenchmarkTest {

    @Test
    fun runKmpBenchmark() {
        println("--- START KMP MULTIPLATFORM BENCHMARK ---")
        val payload = UserProfile("Kotlin Dev", "kmp@example.com", "secure123")

        // Warm up execution paths
        repeat(100_000) {
            UserProfileValidator.validate(payload)
        }

        // Measure validation execution times
        val timeSource = TimeSource.Monotonic
        val start = timeSource.markNow()
        
        var sink = 0
        repeat(100_000) {
            val result = UserProfileValidator.validate(payload)
            if (result.valid) {
                sink += result.errors.size + 1
            }
        }
        
        val elapsed: Duration = start.elapsedNow()
        val averageNs = elapsed.inWholeNanoseconds / 100_000.0
        val throughput = (100_000.0 / elapsed.inWholeMilliseconds) * 1000.0

        println("Performance Metrics:")
        println("  Total Iterations: 100,000")
        println("  Total Time: ${elapsed.inWholeMilliseconds} ms")
        println("  Average Latency: ${averageNs} ns (${averageNs / 1_000_000.0} ms)")
        println("  Throughput: ${throughput.toLong()} ops/sec")
        println("  Validation Sink Verification: $sink")
        println("--- END KMP MULTIPLATFORM BENCHMARK ---")

        assertTrue(sink > 0)
    }
}
