plugins {
    kotlin("multiplatform") version "1.9.23"          // or the desired KMP version
    id("org.jetbrains.compose") version "1.7.3"      // Compose Multiplatform plugin
}

kotlin {
    jvm {
        withJava()  // Enable mixed Kotlin/Java if needed (Kotlin 2.1+ does this by default)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Add shared dependencies here (Kotlin stdlib, etc.)
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))           // Kotlin/JVM standard library
                // Compose Multiplatform libraries (use Compose plugin's coordinates)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                // Compose Desktop runtime (native libs for current OS)
                implementation(compose.desktop.currentOs)
                // Compose BOM for version alignment (no explicit versions on the above libs)
                implementation(project.dependencies.platform("androidx.compose:compose-bom:2025.04.00"))
            }
        }
        // (other sourceSets like jvmTest can be configured similarly)
    }
}

// Optionally configure Compose Desktop application settings if needed:
// compose.desktop {
//     application { /* ... */ }
// }
