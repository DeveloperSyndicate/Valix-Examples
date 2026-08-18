plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("multiplatform") version "2.3.21" apply false
    id("com.google.devtools.ksp") version "2.3.9" apply false
    kotlin("plugin.spring") version "2.3.21" apply false
    id("com.android.application") version "9.1.1" apply false
    kotlin("plugin.compose") version "2.3.21" apply false
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

kotlin {
    jvmToolchain(17)
}