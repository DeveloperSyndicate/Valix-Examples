pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "Valix-Example"

includeBuild("/Users/sanjay/Documents/VibeCode/Valix")
include(":spring-example")
include(":micronaut-example")
include(":android-example")
include(":ktor-example")