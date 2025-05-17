// karl-project/karl-kldl/build.gradle.kts
plugins {
    kotlin("multiplatform") // Version inherited
}

val kotlinxCoroutinesVersion: String by rootProject.ext // Potentially inherited via :karl-core
val kotlinDlVersion: String by rootProject.ext

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Depends on the common part of karl-core.
                // This makes com.karl.core.api.LearningEngine and com.karl.core.models.* available.
                api(project(":karl-core"))

                // If KLDLLearningEngine common code (if any) or its jvmMain implementation directly
                // uses coroutine builders (launch, async) or other coroutine features not
                // just types from the LearningEngine interface, then it needs its own coroutine dependency.
                // If :karl-core uses 'api' for coroutines, this might be redundant but harmless.
//                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                // KotlinDL library for the JVM implementation
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-api:$kotlinDlVersion")
                implementation("org.jetbrains.kotlinx:kotlin-deeplearning-dataset:$kotlinDlVersion")
                // If you use TensorFlow specific features from KotlinDL:
                // implementation("org.jetbrains.kotlinx:kotlin-deeplearning-tensorflow:$kotlinDlVersion")
            }
        }
    }
}