plugins {
    kotlin("multiplatform") // Use multiplatform plugin
    // kotlin("jvm")
}

kotlin {
    // Define targets. For now, we might only need JVM if targeting Desktop,
    // but defining commonMain is good practice for core logic.
    // If you plan Android/iOS/JS later, add android(), iosX64(), js() etc.
    jvm() // Target the JVM platform

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Add dependencies needed in common code
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // For coroutines interfaces
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("androidx.compose.ui:ui-tooling")
//                implementation(libs.bundles.compose.tooling)
                // Add JVM-specific dependencies if any are needed for common types (rare)
            }
        }
        // Define test source sets if needed
        // val commonTest by getting
        // val jvmTest by getting
    }
}

// (Optional) Publishing configuration (future plan) to publish this as a library
// publishing { ... }

// (Optional) KDoc generation - later on, we can add Dokka for documentation generation
// tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
//     outputDirectory.set(buildDir.resolve("dokka")) // Set output directory for Dokka
//     dokkaSourceSets {
//         configure(listOf(this)) {
//             // Configure source sets for Dokka
//             includeNonPublic.set(true) // Include non-public members if needed
//             skipEmptyPackages.set(true) // Skip empty packages in the documentation
//         }
//     }
// }
// tasks.dokkaHtmlMultiModule.configure {
//    outputDirectory.set(buildDir.resolve("dokka")) // Set output directory for Dokka
//    dokkaSourceSets {
//        configure(listOf(this)) {
//            includeNonPublic.set(true) // Include non-public members if needed
//            skipEmptyPackages.set(true) // Skip empty packages in the documentation
//        }
//    }
//}