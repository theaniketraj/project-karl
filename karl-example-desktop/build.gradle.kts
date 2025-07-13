// karl-project/karl-example-desktop/build.gradle.kts
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile // For setting jvmTarget in a 'kotlin("jvm")' module

plugins {
    alias(libs.plugins.kotlinJvm) // This module is a pure JVM application
    alias(libs.plugins.jetbrainsCompose)
    // alias(libs.plugins.kotlinComposeCompiler) // Needed if using Kotlin 2.0+ with Compose
}

group = "com.karl.example"
version = "1.0.0"

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlinx.coroutines.core)

    // Compose Desktop. The 'jetbrainsCompose' plugin usually provides the 'compose.' accessors.
    // If not, or for clarity, define them in libs.versions.toml [libraries]
    // For an application, compose.desktop.application or compose.desktop.currentOs are key
    implementation(compose.desktop.currentOs) // Or compose.desktop.application if preferred for app setup

    // KARL Module Dependencies
    implementation(project(":karl-core"))
    implementation(project(":karl-kldl"))
    implementation(project(":karl-room"))
    implementation(project(":karl-compose-ui"))

    // Dependencies needed by KARL Implementations that aren't transitive via 'api'
    implementation(libs.sqlite.jdbc) // For :karl-room's SQLite backend
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17" // Set the JVM target version
    }
}

compose.desktop {
    application {
        mainClass = "com.karl.example.DesktopExampleAppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)
            packageName = "KarlExampleApp"
            packageVersion = "1.0.0"
        }
    }
}
