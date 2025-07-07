// karl-project/settings.gradle.kts
pluginManagement {
    repositories { // For resolving plugin artifacts
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https.maven.pkg.jetbrains.space/public/p/compose/dev")
        // Add any other EAP/Dev repositories if needed for cutting-edge plugin versions
    }
    // The plugin versions are now managed by libs.versions.toml, so this block below is often not strictly needed
    // if all plugins are defined in the catalog and applied using alias(libs.plugins...).
    // However, keeping it can still be useful for IDE autocompletion for plugin versions.
    // If you keep it, ensure versions match libs.versions.toml.

    plugins {
        kotlin("multiplatform") version "1.9.10" apply false // Or referenced from libs.versions.toml if possible
        kotlin("jvm") version "1.9.10" apply false // If used
        kotlin("plugin.serialization") version "1.5.1" apply false // If used

        // Ensure your Jetpack Compose plugin version is here
        id("org.jetbrains.compose") version "1.5.2" apply false // For Kotlin 1.9.23
        // id("org.jetbrains.kotlin.plugin.compose") version "1.9.10" apply false // For Kotlin 1.9.23 + Compose 1.6.10

        // ---> ADD OR VERIFY THIS LINE FOR KSP <---
        id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false // KSP version aligned with Kotlin 1.9.23

        // SQLDelight (if you still have plans for it or use it in other modules)
        // id("app.cash.sqldelight") version "2.0.1" apply false
        id("org.gradle.toolchains.foojay-resolver-convention").version("0.10.0")
    }
}

// dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        google()
//        mavenCentral()
//    }
// }

rootProject.name = "karl-project"
include(
    ":karl-core",
    ":karl-kldl",
    ":karl-room", // Assuming you created this
    ":karl-compose-ui",
    ":karl-example-desktop",
)
