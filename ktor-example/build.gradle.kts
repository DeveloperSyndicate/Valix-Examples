// ktor-example build configuration
plugins {
    kotlin("jvm") version "2.3.21"
    application
    id("com.google.devtools.ksp") version "2.3.9"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Latest Ktor 3.5.2 Dependencies
    implementation("io.ktor:ktor-server-core:3.5.2")
    implementation("io.ktor:ktor-server-netty:3.5.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.5.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")

    // Valix Integration
    implementation("com.developersyndicate.valix:valix-core")
    ksp("com.developersyndicate.valix:valix-ksp")

    // Optional Hibernate Validator for benchmark comparison
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2") // Correct Glassfish EL runtime dependency

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.ktor.ApplicationKt")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

tasks.test {
    useJUnit()
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        showStandardStreams = true
    }
}
