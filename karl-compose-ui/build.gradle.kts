// karl-project/karl-compose-ui/build.gradle.kts

plugins {
    kotlin("multiplatform") // Version inherited from settings\
    id("org.jetbrains.compose") // Version inherited from settings
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvm { // Define the JVM target for desktop components
        //tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            //kotlinOptions {
                //jvmTarget = "17" // Set JVM target compatibility
            //}
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    // testRuns.named("test") { useJUnitPlatform() } // Optional: for JVM tests
    // Add other targets like androidTarget(), iosX64() if this UI module becomes truly multiplatform

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":karl-core")) // Dependency on your core module

                // Compose libraries are typically brought in by the org.jetbrains.compose plugin.
                // You usually don't need to declare compose.runtime, compose.foundation, etc. explicitly here
                // when the plugin is applied. If you DO need them (e.g., for specific versions not managed
                // by the plugin's BOM, or for clarity), use aliases from libs.versions.toml:
                // implementation(libs.compose.runtime)
                // implementation(libs.compose.ui)
                // implementation(libs.compose.foundation)
                // implementation(libs.compose.material3) // Or libs.compose.material
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3) // Or compose.material if you prefer Material 2
                implementation(compose.ui)
                implementation(compose.uiTooling)

                // Coroutines
                implementation(libs.kotlinx.coroutines.core) // Assumes 'kotlinx-coroutines-core' is in libs.versions.toml
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test")) // Standard Kotlin test library
                // If you need Compose specific testing libraries, you can add them here
                // e.g., implementation(libs.compose.ui.test) if needed
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8) // Assumes 'kotlin-stdlib-jdk8' is in libs.versions.toml

                // Compose Desktop specific host libraries (windows/mac/linux natives)
                // This is typically brought in by the org.jetbrains.compose plugin when you configure
                // compose.desktop { ... } in a module that *is* an application.
                // For a library module like :karl-compose-ui, you might not need it directly,
                // or if you do, it would be `implementation(compose.desktop.currentOs)` if that accessor is available.
                // Let's rely on the plugin for now, unless specific errors occur.
                // implementation(compose.desktop.currentOs) // If needed and accessor works

                // Desktop Preview Tooling API
                // The `compose.uiTooling` accessor should provide the correct artifact.
                // The explicit "org.jetbrains.compose.ui:ui-tooling-preview-desktop:..." is less common.
                implementation(compose.uiTooling) // This should bring in ui-tooling for @Preview
            }
        }
        // ... commonTest, jvmTest ...
    }
}

// If this module itself needs specific desktop configurations (unlikely for a pure UI library)
// compose.desktop {
//     // Example:
//     // Jvm specific configuration for this module if it were an application.
//     // For a library, this block might not be needed here.
// }
