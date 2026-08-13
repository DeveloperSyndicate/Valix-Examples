pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
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