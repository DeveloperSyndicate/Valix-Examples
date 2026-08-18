# Valix Integration Examples

This repository contains integration examples showcasing the capabilities and performance characteristics of the **Valix** compile-time validation framework in various Kotlin application environments.

---

## 1. Spring Boot Integration Example (`spring-example`)

The `spring-example` module contains a Spring Boot REST API demonstrating:
- **Compile-Time Annotation Processing:** Using KSP (`valix-ksp`) to generate type-safe validator classes at compile-time instead of utilizing slow runtime reflection.
- **Spring MVC Argument Resolution:** Automatically validating incoming `@RequestBody` controller parameters using the `@ValidValix` annotation.
- **Unified Error Handling:** Utilizing Spring's `@ControllerAdvice` (`ValixControllerAdvice`) to capture validation exceptions and return formatted JSON error structures.

---

## 2. Micronaut Integration Example (`micronaut-example`)

The `micronaut-example` module showcases how Valix integrates into modern cloud-native Microservices using Micronaut:
- **AOP Interceptor Validation:** Demonstrating AOP-driven request body validation on controller actions using the `@ValixValidated` annotation.
- **Dependency Injection Integration:** Resolving validators automatically at compile-time using Micronaut's dependency injection processor.

---

## 3. Android Integration Example (`android-example`)

The `android-example` module showcases how Valix provides extremely lightweight, fast validation in client-side mobile applications:
- **Compose UI Integration:** Using Jetpack Compose dynamic form input validation with the `valix-compose` state utilities (e.g. `rememberValixForm`).
- **Zero Reflection Overhead:** Ideal for Android environments where reflection overhead directly degrades app startup latency and UI response time.
- **On-Device & Unit Benchmarking:** Includes a benchmark suite that runs both on-device (via Compose UI activity) and on-host JVM (via Gradle tests).

## 5. Ktor Integration Example (`ktor-example`)

The `ktor-example` module showcases how to integrate Valix compile-time validation with Ktor 3.x servers:
- **Compile-Time Safety:** Bypasses reflection-based JSR-380 validation, generating pure Kotlin type-safe validator structures via KSP.
- **Unified Clean Error Responses:** Converts validation results into structured REST responses returning a clean, flat list of error fields.
- **Embedded Performance Benchmarks:** Contains an inline comparison benchmark comparing Valix's execution speed against Hibernate Validator.

### Running the Ktor Server
Start the Netty-based server locally (starts by default on port `8080`):
```bash
./gradlew :ktor-example:run
```

### Testing the REST Endpoint
Send a sample validation request containing invalid payload parameters:
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"","email":"invalid-email","age":16}'
```

Response (Flat list of clean validation errors):
```json
[
  {"field":"username","message":"must not be blank"},
  {"field":"email","message":"invalid email"},
  {"field":"age","message":"must be at least 18"}
]
```

---

## 6. Kotlin Multiplatform (KMP) Example (`kmp-example`)

The `kmp-example` module showcases how Valix executes validations inside a multiplatform project architecture:
- **Shared Validation Logic:** Defines shared models and cross-platform validation routines in the `commonMain` source set.
- **Multi-Target Integration:** Evaluates execution compatibility across `JVM`, `iOS (ARM64 & X64)`, `JavaScript (NodeJS & Browser)`, and `WebAssembly (WasmJS)`.
- **Dynamic Programmatic Checks:** Utilizes the programmatic Kotlin DSL validation API (`valixDsl` from the `valix-runtime` package) to run checks dynamically on targets where static annotation processors are not natively configured.

### Running KMP Target Tests
To trigger the shared unit testing suites across all configured target runtimes, execute:
```bash
./gradlew :kmp-example:allTests
```

---

## 4. Real-Time Performance Benchmarks

To illustrate the latency and throughput benefits of Valix compile-time generated validators over reflection-based JSR-380 validation (e.g. Hibernate Validator), real-time JUnit benchmarks are included in all example subprojects.

### Benchmark Configuration
- **Iterations per Run:** 100,000 payload validations (mix of valid and invalid request instances).
- **Sequential Runs:** 10 sequential execution runs to compute an average time and throughput.
- **Parallel Runs:** 10 concurrent threads executing 100,000 validations simultaneously (simulating multi-threaded workloads).

### Performance Results

#### A. Spring Boot Benchmark Results (10 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **22.50 ms** | **4,444,444** | **7.91x Faster** |
| JSR-380 (Hibernate) | 178.00 ms | 561,797 | Baseline |

* **Spring Parallel (10 Threads):** Valix was **3.34x Faster** with **13,157,894 ops/sec** vs JSR-380's **3,937,007 ops/sec**.

#### B. Micronaut Benchmark Results (10 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **19.20 ms** | **5,208,333** | **9.44x Faster** |
| JSR-380 (Hibernate) | 181.30 ms | 551,571 | Baseline |

* **Micronaut Parallel (10 Threads):** Valix was **5.28x Faster** with **14,705,882 ops/sec** vs JSR-380's **2,785,515 ops/sec**.

#### C. Android Benchmark Results (10 Run Average - JVM Host Mode)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **20.90 ms** | **4,784,688** | **7.19x Faster** |
| JSR-380 (Hibernate) | 150.30 ms | 665,335 | Baseline |

* **Android Parallel (10 Threads):** Valix was **3.76x Faster** with **15,151,515 ops/sec** vs JSR-380's **4,032,258 ops/sec**.

#### D. Ktor Benchmark Results (1,000,000 Run Average)

| Framework | Average Time (ms) | Throughput (ops/sec) | Speed Improvement |
| --- | --- | --- | --- |
| **Valix** | **15.20 ms** | **6,578,947** | **6.85x Faster** |
| JSR-380 (Hibernate) | 104.10 ms | 960,614 | Baseline |

#### E. Kotlin Multiplatform (KMP) Verification Results

The test suite runs the shared validations successfully across all target runtime platforms, executing **100,000 runs** of the programmatic validation suite to measure cross-platform overhead:

| Platform Target | Runtime Environment | Build Status | Average Latency | Throughput (ops/sec) |
| --- | --- | --- | --- | --- |
| **JVM** | Adoptium OpenJDK 17 | ✅ Success | **400.89 ns** | **2,500,000** |
| **iOS Simulator** | iOS Simulator (ARM64) | ✅ Success | **7,853.21 ns** | **127,388** |
| **JavaScript** | Node.js (v24.10) | ✅ Success | **165.91 ns** | **6,250,000** |
| **WebAssembly** | WasmJS Browser (Chrome Headless) | ✅ Success | **723.00 ns** | **1,388,888** |

> [!NOTE]
> Since Valix generates plain Kotlin validation statements directly at compile-time, it completely bypasses runtime annotation parsing, reflection lookup, and metadata caches. This provides massive performance benefits even under heavy parallel load.

---

## Running the Benchmarks Locally

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

```
