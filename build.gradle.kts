// karl-project/build.gradle.kts
// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Apply plugins required at the root level.
    // For Compose Desktop/Multiplatform, the Compose plugin is often applied here.
    // For SQLDelight, the SQLDelight plugin is also applied here.
//    id("org.gradle.build-cache")
    id("org.jetbrains.compose") version "1.6.10" apply false // Apply false means modules apply it explicitly
    id("app.cash.sqldelight") version "2.0.1" apply false // Apply false means modules apply it explicitly
    id("org.jetbrains.kotlin.jvm") version "1.9.23" apply false // Apply false means modules apply it explicitly
    // kotlin("jvm") apply false // Apply false means modules apply it explicitly (used by example app) //
    kotlin("multiplatform") version "1.9.23" apply false // Apply false means modules apply it explicitly (used by core, kldl)
}

// Define repositories where Gradle should look for dependencies and plugins.
allprojects {
    repositories {
        mavenCentral() // Standard Java/Kotlin libraries
        google()       // Google/Android related libraries (often needed even for Compose Desktop)
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Jetpack Compose libraries
        // Add any other repositories your specific dependencies or plugins require
    }
}

// Central place to define dependency versions.
// Using a versions catalog (gradle/libs.versions.toml) is the recommended modern approach,
// but defining them here directly is simpler for this example.
// You'll reference these versions in the module build.gradle.kts files.
val kotlinVersion = "1.9.23" // Or whatever your current Kotlin version is
val composeVersion = "1.6.10" // Should match the plugin version or compatible
val kotlinxCoroutinesVersion = "1.7.3"
val kotlinDlVersion = "0.5.1"
val sqldelightVersion = "2.0.2" // Should match the plugin version or compatible

// Example of defining versions using ext (less type-safe than versions catalog but works)
ext {
    set("kotlinVersion", "1.9.23")
    set("composeVersion", "1.6.10")
    set("multiplatformVersion", "1.9.23")
    set("kotlinxCoroutinesVersion", "1.7.3")
    set("kotlinDlVersion", "0.5.1")
    set("sqldelightVersion", "2.0.1")
    set("roomVersion", "2.6.1")
    set("kspVersion", "1.9.23-1.0.19")
    // ... and so on
}

// Configure subprojects (optional, often better to configure in module build files)
// subprojects {
//     tasks.withType<Test> {
//         useJUnitPlatform()
//     }
// }

// Clean task for the root project
tasks.register("clean") {
    delete(rootProject.layout.buildDirectory)
}