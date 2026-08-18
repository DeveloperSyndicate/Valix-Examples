plugins {
    kotlin("jvm")
    application
    id("com.google.devtools.ksp")
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
    // Valix dependencies
    implementation("com.developersyndicate.valix:valix-core")
    implementation("com.developersyndicate.valix:valix-runtime")
    ksp("com.developersyndicate.valix:valix-ksp")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.advanced.AppKt")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
