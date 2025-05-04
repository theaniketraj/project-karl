plugins {
    // ONLY apply the multiplatform plugin for the core module
    kotlin("multiplatform") version "1.9.23" // Ensure version matches root if declared there
    // DO NOT apply id("org.jetbrains.compose") here
}

kotlin {
    // Define the targets this core module supports
    jvm { // JVM target is needed if other JVM modules depend on it
        // withJava() // Only include if you explicitly need Java interop *within* karl-core
    }
    // Add other targets if you plan to support them later (e.g., android(), iosX64(), etc.)
    // js { browser() }
    // ...

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Core Kotlin standard library for common code
                implementation(kotlin("stdlib-common")) // Use the correct alias

                // Core interfaces use CoroutineScope/Job, so add coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Use version from rootProject.ext if defined
            }
        }
        val jvmMain by getting {
            dependencies {
                // JVM-specific Kotlin standard library
                implementation(kotlin("stdlib-jdk8")) // Use stdlib-jdk8 for JVM

                // NO project(":karl-core") dependency here (self-dependency)
                // NO compose dependencies here
            }
        }
        // Define test source sets if needed
        // val commonTest by getting { dependencies { ... } }
        // val jvmTest by getting { dependencies { ... } }
    }
}

// Remove any compose.desktop blocks from here