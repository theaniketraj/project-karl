plugins {
    // Apply the multiplatform plugin, as the implementation depends on the common core
    kotlin("multiplatform") // Ensure version matches root project (e.g., version "1.9.23")
    // DO NOT apply id("org.jetbrains.compose") here
}

// Access versions from the root project's ext block
val kotlinxCoroutinesVersion: String by rootProject.ext
val kotlinDlVersion: String by rootProject.ext

kotlin {
    // Define the JVM target, as KotlinDL and this implementation are currently JVM-based
    jvm {
        // withJava() // Optional: Only if you need Java interop within this module
        // testRuns["test"].executionTask.configure { useJUnitPlatform() } // Optional: Configure tests
    }
    // Add other KMP targets here ONLY if KotlinDL supports them AND
    // you provide implementations for them within this module.

    sourceSets {
        val commonMain by getting {
            dependencies {
                // This module's common code (if any) depends on karl-core's common code
                api(project(":karl-core")) // Use 'api' so downstream modules also get karl-core transitively

                // Coroutines dependency might be needed if commonMain uses it directly,
                // otherwise it's inherited via karl-core's api dependency.
                // api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            // jvmMain depends implicitly on commonMain of this module
            // and also sees commonMain of dependencies declared in its own commonMain (like :karl-core)
            dependencies {
                // KotlinDL library for the JVM implementation
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:$kotlinDlVersion")
                // Add other KotlinDL modules if needed (e.g., tensorflow, onnx) based on KLDLLearningEngine needs
                // implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:$kotlinDlVersion")

                // Kotlin standard library for JVM
                implementation(kotlin("stdlib-jdk8"))

                // NO compose dependencies here
            }
        }
        // Define test source sets if needed
        // val commonTest by getting { dependencies { ... } }
        // val jvmTest by getting { dependencies { ... } }
    }
}

// Optional: Publishing configuration if you plan to publish this module
// publishing { ... }