plugins {
    kotlin("multiplatform")
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

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    
    js(IR) {
        browser()
        nodejs()
    }
    
    wasmJs {
        browser()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation("com.developersyndicate.valix:valix-core")
            implementation("com.developersyndicate.valix:valix-runtime")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", "com.developersyndicate.valix:valix-ksp")
    add("kspJvm", "com.developersyndicate.valix:valix-ksp")
    add("kspIosArm64", "com.developersyndicate.valix:valix-ksp")
    add("kspIosX64", "com.developersyndicate.valix:valix-ksp")
    add("kspIosSimulatorArm64", "com.developersyndicate.valix:valix-ksp")
    add("kspJs", "com.developersyndicate.valix:valix-ksp")
    add("kspWasmJs", "com.developersyndicate.valix:valix-ksp")
}

kotlin.sourceSets.configureEach {
    val targetName = name
    val buildDir = project.layout.buildDirectory.dir("generated/ksp").get().asFile.absolutePath
    if (targetName == "commonMain") {
        kotlin.srcDir("$buildDir/metadata/commonMain/kotlin")
    } else if (targetName.endsWith("Main")) {
        val platformName = targetName.substringBefore("Main")
        kotlin.srcDir("$buildDir/$platformName/${platformName}Main/kotlin")
    } else if (targetName.endsWith("Test")) {
        val platformName = targetName.substringBefore("Test")
        kotlin.srcDir("$buildDir/$platformName/${platformName}Test/kotlin")
    }
}
