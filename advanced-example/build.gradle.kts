plugins {
    kotlin("jvm")
    application
    id("com.google.devtools.ksp")
    id("org.graalvm.buildtools.native") version "0.9.28"
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
    testImplementation("jakarta.validation:jakarta.validation-api:3.0.2")
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    testImplementation("org.glassfish:jakarta.el:4.0.2") // Expression Language impl required for Hibernate Validator
}

application {
    mainClass.set("com.example.advanced.AppKt")
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.advanced.AppKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("valix-advanced-native")
            mainClass.set("com.example.advanced.AppKt")
            buildArgs.addAll(
                "--no-fallback",
                "-J-Xmx1536m",    // Limit builder heap to 1.5GB
                "--parallelism=2"  // Restrict build threads to 2 cores (minimum required by GraalVM)
            )
        }
    }
}
