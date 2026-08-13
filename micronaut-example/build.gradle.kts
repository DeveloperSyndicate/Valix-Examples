plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val micronautVersion = "4.4.2"

dependencies {
    implementation("io.micronaut:micronaut-inject:$micronautVersion")
    implementation("io.micronaut:micronaut-runtime:$micronautVersion")
    implementation("io.micronaut:micronaut-http-server-netty:$micronautVersion")
    implementation("io.micronaut.validation:micronaut-validation:4.4.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    // KSP for Micronaut Inject
    ksp("io.micronaut:micronaut-inject-java:$micronautVersion")

    implementation("com.developersyndicate.valix:valix-core")
    implementation("com.developersyndicate.valix:valix-runtime")
    implementation("com.developersyndicate.valix:valix-micronaut")
    ksp("com.developersyndicate.valix:valix-ksp")

    testImplementation("io.micronaut.test:micronaut-test-junit5:4.3.0")
    testImplementation(kotlin("test"))
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    testImplementation("org.glassfish:jakarta.el:4.0.2")
}

application {
    mainClass.set("com.example.micronaut.ApplicationKt")
}

kotlin {
    jvmToolchain(17)
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
