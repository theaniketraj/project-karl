// karl-project/karl-kldl/build.gradle.kts
plugins {
//    kotlin("multiplatform") apply false
    id("org.jetbrains.kotlin.multiplatform")
}

// Access versions from root project
val kotlinxCoroutinesVersion: String by rootProject.ext
val kotlinDlVersion: String by rootProject.ext

kotlin {
    jvm{
        // JVM Target version
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core")) // Makes LearningEngine interface available
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion") // For CoroutineScope, Job etc.
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:$kotlinDlVersion")
                // Add other DL artifacts if needed:
                // implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:$kotlinDlVersion")
            }
        }
    }
}