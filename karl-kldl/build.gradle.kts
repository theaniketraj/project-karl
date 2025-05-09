// karl-project/karl-kldl/build.gradle.kts
plugins {
    kotlin("multiplatform") // Version from settings.gradle.kts or root
}

// Access versions from root project
val kotlinxCoroutinesVersion: String by rootProject.ext // Though this might be inherited via :karl-core
val kotlinDlVersion: String by rootProject.ext

kotlin {
    jvm { // Define the JVM target
        compilations.all {
            kotlinOptions.jvmTarget = "1.8" // Or "11"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Depends on the common part of karl-core.
                // This makes com.karl.core.api.LearningEngine and com.karl.core.models.* available.
                api(project(":karl-core"))

                // Coroutines should be inherited from :karl-core if it uses 'api'.
                // If not, add it here:
                // api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // KotlinDL library for the JVM implementation
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:$kotlinDlVersion")
                // Add -tensorflow or -onnx if KLDLLearningEngine.kt uses features requiring them
                // implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:$kotlinDlVersion")
            }
        }
    }
}