// karl-project/karl-compose-ui/build.gradle.kts

plugins {
    kotlin("multiplatform") version "1.9.23" // Apply the multiplatform plugin
    id("org.jetbrains.compose") // Apply the Jetpack Compose plugin
}

// Access dependency versions defined in the root project's build.gradle.kts
// Using `the<ExtraPropertiesExtension>()` is one way to access 'ext' properties
val composeVersion: String by rootProject.ext
val kotlinVersion: String by rootProject.ext // Although often not explicitly needed here
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    // Define targets that support Compose.
    // For desktop, we need the JVM target.
    jvm()
    // If targeting Android later, add: androidTarget()
    // If targeting Web later, add: js { browser() }
    // If targeting iOS later, add: iosX64(); iosArm64(); iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Core dependencies needed regardless of platform
                implementation(project(":karl-core")) // Dependency on the karl-core module

                // Compose Runtime - essential for Compose features
                implementation(compose.runtime)
                // Compose Foundation - basic UI elements and layouts
                implementation(compose.foundation)
                // Compose material3 or material - depending on your desired design system
                implementation(compose.material3) // Or compose.material for Material 2
                // Compose UI - core UI toolkit components
                implementation(compose.ui)
//                implementation(compose.uiToolingPreview)
                // State management helpers for Compose with Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Ensure coroutines version matches root
            }
        }

        val jvmMain by getting {
            dependencies {
//                implementation(project(":karl-core"))
                // JVM/Desktop specific dependencies if any.
                // Often not needed for pure UI components unless they touch platform APIs.
                // Example: compose.desktop.currentOs // If you need platform-specific desktop composites

                // 1. Compose runtime & foundation for desktop
                implementation(compose.runtime)           // Compose common runtime
                implementation(compose.foundation)        // Compose common foundation
                implementation(compose.material)          // Compose common material

                // 2. Compose Desktop host (windows/mac/linux native libs)
                implementation(compose.desktop.currentOs) // Desktop artifacts for your OS

                // 3. Desktop Preview Tooling API
                implementation("org.jetbrains.compose.ui:ui-tooling-preview-desktop:1.8.0-beta02")
                implementation("org.jetbrains.compose.ui:ui-tooling-desktop:1.8.0-beta02")

//                implementation(compose.desktop.currentOs) // Needed for desktop-specific composites
//                implementation(compose.uiTooling) // <-- Add this for @Preview support in Desktop
//                implementation(project.dependencies.platform("androidx.compose:compose-bom:2024.01.00"))
//                implementation("androidx.compose.ui:ui-tooling-preview")
//                implementation(project.dependencies.platform("androidx.compose:compose-bom:2024.01.00"))
//                debugImplementation("androidx.compose.ui:ui-tooling")
            }
        }

        // Add other platform source sets if you target them later (androidMain, jsMain, iosMain, etc.)
    }
}

// Configure the Compose compiler extension (usually handled by the plugin, but good to know)
// compose.experimental {
//     enableLiveLiterals = true // Example experimental flag
// }

// (Optional) KDoc generation
// tasks.dokkaHtml { ... }
