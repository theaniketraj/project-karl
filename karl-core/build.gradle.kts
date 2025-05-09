// karl-project/karl-core/build.gradle.kts
plugins {
    kotlin("multiplatform") // Version from settings.gradle.kts or root
}

// Access versions from root project
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm { // Define the JVM target
        // This ensures a JVM variant is published
        // withJava() // Optional, only if mixing Java source files in karl-core's jvmMain
        compilations.all { // Ensure proper Kotlin options
            kotlinOptions.jvmTarget = "1.8" // Or "11" if your project targets that
        }
    }
    // Add other targets (androidTarget, iosX64, etc.) if you expand KMP later

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                // Use 'api' so that modules depending on :karl-core also get coroutines types
                // This is CRITICAL if interfaces in :karl-core use Job, CoroutineScope, etc.
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                // commonMain is automatically a dependency here
            }
        }
        // commonTest, jvmTest definitions...
    }
}