plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    application
    kotlin("plugin.spring")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")

    implementation("com.developersyndicate.valix:valix-core")
    implementation("com.developersyndicate.valix:valix-runtime")
    implementation("com.developersyndicate.valix:valix-spring")
    ksp("com.developersyndicate.valix:valix-ksp")

    testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.example.spring.SpringExampleAppKt")
}

kotlin {
    jvmToolchain(17)
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}
