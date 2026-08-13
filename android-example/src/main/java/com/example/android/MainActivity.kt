package com.example.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.valix.compose.ValidatedTextField
import io.valix.compose.rememberValixForm
import jakarta.validation.Validation
import kotlin.system.measureTimeMillis

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val scrollState = rememberScrollState()
    var successMessage by remember { mutableStateOf("") }
    
    // 1. Form state setup
    val form = rememberValixForm(
        initialValue = RegistrationRequest("", "", null),
        validator = com.example.android.generated.RegistrationRequestValidator
    )

    // 2. Benchmark state setup
    var isBenchmarking by remember { mutableStateOf(false) }
    var benchmarkResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text("Valix Validation Example", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Registration form fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Registration Form", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                ValidatedTextField(
                    value = form.value.username ?: "",
                    onValueChange = { 
                        form.onFieldChange("username", form.value.copy(username = it)) 
                    },
                    error = form.errorFor("username"),
                    label = "Username",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ValidatedTextField(
                    value = form.value.email ?: "",
                    onValueChange = { 
                        form.onFieldChange("email", form.value.copy(email = it)) 
                    },
                    error = form.errorFor("email"),
                    label = "Email Address",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ValidatedTextField(
                    value = form.value.age?.toString() ?: "",
                    onValueChange = { ageStr ->
                        val parsedAge = ageStr.toIntOrNull()
                        form.onFieldChange("age", form.value.copy(age = parsedAge))
                    },
                    error = form.errorFor("age"),
                    label = "Age (Min: 18)",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val res = form.validate()
                        if (res.valid) {
                            successMessage = "Successfully registered user: ${form.value.username}"
                        } else {
                            successMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Register User")
                }

                if (successMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(successMessage, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Benchmark section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("On-Device Performance Benchmark", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Runs 10,000 sequential iterations and 10 parallel threads (10k iterations each) comparing compile-time generated Valix against reflection-based JSR-380 validation.",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isBenchmarking = true
                        benchmarkResult = null
                        // Run in background thread to avoid blocking Compose UI
                        Thread {
                            val result = runBenchmark()
                            isBenchmarking = false
                            benchmarkResult = result
                        }.start()
                    },
                    enabled = !isBenchmarking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isBenchmarking) "Running Benchmarks..." else "Run Benchmark")
                }

                benchmarkResult?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun runBenchmark(): String {
    val iterations = 10_000
    val runs = 10

    // Setup JSR-380 Validator
    val factory = Validation.buildDefaultValidatorFactory()
    val jsrValidator = factory.validator

    // Valix compiled validator
    val valixValidator = com.example.android.generated.RegistrationRequestValidator

    val valixPayloads = listOf(
        RegistrationRequest("john_doe", "john@example.com", 25), // valid
        RegistrationRequest("", "invalid-email", 12),           // invalid
        RegistrationRequest("alice", "", 30),                   // invalid
        RegistrationRequest(null, "alice@example.com", null)    // invalid
    )

    val jsrPayloads = listOf(
        RegistrationRequestJsr("john_doe", "john@example.com", 25), // valid
        RegistrationRequestJsr("", "invalid-email", 12),           // invalid
        RegistrationRequestJsr("alice", "", 30),                   // invalid
        RegistrationRequestJsr(null, "alice@example.com", null)    // invalid
    )

    // Warm up
    repeat(2_000) { i ->
        valixValidator.validate(valixPayloads[i % valixPayloads.size])
        jsrValidator.validate(jsrPayloads[i % jsrPayloads.size])
    }

    // 1. Sequential Runs
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
    val sequentialImprovement = avgJsrTime / avgValixTime

    // 2. Parallel Runs (10 concurrent threads)
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

    val jsrParallelStart = System.currentTimeMillis()
    val futuresJsr = threadPool.invokeAll(tasksJsr)
    val jsrParallelTotalTime = System.currentTimeMillis() - jsrParallelStart

    threadPool.shutdown()

    val valixParallelOpsPerSec = ((iterations * 10).toDouble() / valixParallelTotalTime) * 1000
    val jsrParallelOpsPerSec = ((iterations * 10).toDouble() / jsrParallelTotalTime) * 1000
    val parallelImprovement = jsrParallelTotalTime.toDouble() / valixParallelTotalTime

    return buildString {
        appendLine("=== BENCHMARK RESULTS (10,000 Iterations) ===")
        appendLine("\n--- SEQUENTIAL RUN ---")
        appendLine("Valix: ${String.format("%.2f", avgValixTime)}ms (${valixOpsPerSec.toLong()} ops/sec)")
        appendLine("JSR-380: ${String.format("%.2f", avgJsrTime)}ms (${jsrOpsPerSec.toLong()} ops/sec)")
        appendLine("Speedup: ${String.format("%.2f", sequentialImprovement)}x faster")
        appendLine("\n--- PARALLEL RUN (10 THREADS) ---")
        appendLine("Valix Wall Time: ${valixParallelTotalTime}ms (${valixParallelOpsPerSec.toLong()} ops/sec)")
        appendLine("JSR-380 Wall Time: ${jsrParallelTotalTime}ms (${jsrParallelOpsPerSec.toLong()} ops/sec)")
        appendLine("Speedup: ${String.format("%.2f", parallelImprovement)}x faster")
    }
}
