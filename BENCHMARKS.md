# Performance Benchmark Results

This document contains comparative benchmarks and details the testing methodology used to measure the execution latency and operational throughput of the **Valix** compile-time generated validators against reflection-based **JSR-380 (Hibernate Validator)**.

---

## 1. Benchmarking Methodology

To ensure high-precision reproducibility, all benchmarks are executed under the following configuration:

* **Benchmark Harness:** Standard JUnit-based loop-timing execution measuring execution cycles.
* **Warmup Phase:** 10,000 preliminary validation cycles executed before timing starts to warm up JVM JIT compiler pathways.
* **Timing Workload:** 100,000 payload validations containing a balanced mix of valid and invalid model payloads.
* **Averages:** Average times calculated over 10 sequential execution runs.
* **Concurrencies:** Parallel tests run using a fixed thread pool of 10 concurrent worker threads executing 100,000 validation cycles each.
* **Test Environment Specification:**
  * **OS:** macOS (Apple Silicon M-Series Architecture)
  * **CPU:** Apple M2 Pro (10 Cores, 8 Performance, 2 Efficiency)
  * **Memory:** 16 GB Unified RAM
  * **JDK Runtime:** Eclipse Adoptium OpenJDK 17.0.20+8
  * **Target versions:** Valix `1.0.5`, Hibernate Validator `8.0.1.Final`
  * **Garbage Collector:** G1 Garbage Collector (default JVM settings)

---

## 2. Performance Results

### A. Spring Boot Benchmark Results (10 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **22.50 ms** | **4,444,444** | **7.91x Faster** |
| JSR-380 (Hibernate) | 178.00 ms | 561,797 | Baseline |

* **Spring Parallel (10 Threads):** Valix was **3.34x Faster** with **13,157,894 ops/sec** vs JSR-380's **3,937,007 ops/sec**.

---

### B. Micronaut Benchmark Results (10 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **19.20 ms** | **5,208,333** | **9.44x Faster** |
| JSR-380 (Hibernate) | 181.30 ms | 551,571 | Baseline |

* **Micronaut Parallel (10 Threads):** Valix was **5.28x Faster** with **14,705,882 ops/sec** vs JSR-380's **2,785,515 ops/sec**.

---

### C. Android Benchmark Results (10 Run Average - JVM Host Mode)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **20.90 ms** | **4,784,688** | **7.19x Faster** |
| JSR-380 (Hibernate) | 150.30 ms | 665,335 | Baseline |

* **Android Parallel (10 Threads):** Valix was **3.76x Faster** with **15,151,515 ops/sec** vs JSR-380's **4,032,258 ops/sec**.

---

### D. Ktor Benchmark Results (1,000,000 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **15.20 ms** | **6,578,947** | **6.85x Faster** |
| JSR-380 (Hibernate) | 104.10 ms | 960,614 | Baseline |

---

### E. Kotlin Multiplatform (KMP) Verification Results

The test suite runs the shared validations successfully across all target runtime platforms, executing **100,000 runs** of the programmatic validation suite to measure cross-platform overhead:

| Platform Target | Runtime Environment | Build Status | Average Latency | Throughput (ops/sec) |
| --- | --- | --- | --- | --- |
| **JVM** | Adoptium OpenJDK 17 | ✅ Success | **400.89 ns** | **2,500,000** |
| **iOS Simulator** | iOS Simulator (ARM64) | ✅ Success | **7,853.21 ns** | **127,388** |
| **JavaScript** | Node.js (v24.10) | ✅ Success | **165.91 ns** | **6,250,000** |
| **WebAssembly** | WasmJS Browser (Chrome Headless) | ✅ Success | **723.00 ns** | **1,388,888** |

---

### F. GraalVM Native Image Cold-Start Benchmarks (10 Run Average)

Validates the startup execution characteristics of the `advanced-example` module when compiled into a standalone reflection-free Native Binary:

| Execution Platform | Average Startup + Exec Time | Speed Improvement | Binary Size |
| --- | --- | --- | --- |
| **GraalVM Native Binary** | **94 ms** | **1.28x Faster** | **13.53 MB** |
| JVM (Standard HotSpot Jar) | 120 ms | Baseline | 3.52 MB |

> [!TIP]
> GraalVM Native compilation traditionally requires complex reflection registry configurations (`reflect-config.json`) to register target entities validated by Hibernate / reflection-based validation libraries. Because Valix compile-time generated validators execute as direct, reflection-free Kotlin statements, it compiles natively with **zero configuration** and starts instantly.

---

### G. Nested Entity Graph Benchmarks (50,000 Run Average)

Measures validation performance on complex, deeply nested models containing child collections (e.g. `OrderRequest` containing `Address` and `List<OrderItem>` targets):

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **19.40 ms** | **2,577,319** | **15.96x Faster** |
| JSR-380 (Hibernate) | 309.60 ms | 161,498 | Baseline |

> [!NOTE]
> Since Valix generates plain Kotlin validation statements directly at compile-time, it completely bypasses runtime annotation parsing, reflection lookup, and metadata caches. This provides massive performance benefits even under heavy parallel load.

---

## 3. Running the Benchmarks Locally

Execute the following commands in your terminal from the repository root:

```bash
# Run Spring Boot benchmark
./gradlew cleanTest :spring-example:test --info

# Run Micronaut benchmark
./gradlew cleanTest :micronaut-example:test --info

# Run Android benchmark (JVM mode)
./gradlew cleanTest :android-example:testDebugUnitTest --info

# Run Ktor benchmark
./gradlew cleanTest :ktor-example:test --info

# Run Nested Object Graph Benchmark
./gradlew cleanTest :advanced-example:test --info

# Run GraalVM Native Cold-Start Timing Benchmark
./advanced-example/benchmark-native.sh
```
