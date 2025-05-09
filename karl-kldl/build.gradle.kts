plugins {
    kotlin("multiplatform") // Ensure version is correctly inherited or specified from root/settings
}

// Access versions from the root project's ext block
val kotlinxCoroutinesVersion: String by rootProject.ext
val kotlinDlVersion: String by rootProject.ext

kotlin {
    jvm { // Define the JVM target
        // withJava() // Optional
    }
    // NO OTHER KMP TARGETS DEFINED HERE (unless you specifically intend and configure them)

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Crucial: :karl-core provides LearningEngine, InteractionData, etc.
                // 'api' ensures transitive visibility to modules depending on :karl-kldl
                api(project(":karl-core"))

                // Coroutines are needed for KLDLLearningEngine's interface methods (Job, CoroutineScope)
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                // KotlinDL library for the JVM implementation
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:$kotlinDlVersion")
                // If using TensorFlow backend (common with KotlinDL for advanced features):
                // implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:$kotlinDlVersion")

                // Kotlin standard library for JVM
                implementation(kotlin("stdlib-jdk8"))
            }
        }
    }
}