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
```
