// karl-project/karl-room/build.gradle.kts

plugins {
    kotlin("multiplatform") // KMP plugin
    id("com.google.devtools.ksp") // KSP plugin - CRITICAL for Room
    // No Compose plugin needed here
}

// Access versions from root project's ext block
val roomVersion: String by rootProject.ext // Define "roomVersion" in root ext, e.g., "2.6.1"
val kspVersion: String by rootProject.ext  // Define "kspVersion" in root ext, e.g., "1.9.23-1.0.19" (matches Kotlin)
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm { // Define JVM target
        // withJava() // Optional
        // testRuns["test"].executionTask.configure { useJUnitPlatform() }
    }
    // Add other targets if needed (androidTarget(), etc.)

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core")) // Expose core interfaces/models transitively
                // Coroutines might be inherited via :karl-core, but explicit is fine
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // Room Dependencies for JVM/KMP
                api("androidx.room:room-runtime:$roomVersion")
                // Room Kotlin Extensions (provides suspend functions, Flow support etc.)
                api("androidx.room:room-ktx:$roomVersion")

                // You might need a specific driver/backend for Room on Desktop/JVM
                // Often uses SQLite via androidx.sqlite:sqlite-framework and a native driver
                // Check Room KMP documentation for the exact Desktop setup.
                // Example (might vary based on Room KMP version):
                api("androidx.sqlite:sqlite-framework:2.4.0") // Provides SQLite APIs
                api("androidx.sqlite:sqlite-driver:2.4.0")   // Provides the actual driver (can be platform specific)
            }
        }
        // commonTest, jvmTest dependencies...
        // val jvmTest by getting {
        //     dependencies {
        //         implementation("androidx.room:room-testing:$roomVersion")
        //     }
        // }
    }
}

// KSP Configuration - Tell KSP which target uses the Room annotation processor
// The exact configuration might depend on the Room/KSP version for KMP.
// Refer to the official Room KMP setup guide.
// Example structure:
ksp {
    // This tells KSP to run the Room compiler for the jvm target's sources
    arg("room.schemaLocation", "$projectDir/schemas") // Recommended: Export DB schema for migrations
    arg("room.incremental", "true") // Enable incremental processing
    // Specify target platforms if needed by KSP configuration for KMP
}

// Ensure KSP task runs before Kotlin compilation for the target
// This dependency might be configured automatically by newer KSP/Gradle versions
// tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    dependsOn(tasks.withType<com.google.devtools.ksp.gradle.KspTask>())
// }