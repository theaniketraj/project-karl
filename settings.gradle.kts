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
    /*
    plugins {
        kotlin("multiplatform") version "1.9.23" apply false // Or referenced from libs.versions.toml if possible
        // ... etc for other plugins ...
    }
    */
}

rootProject.name = "karl-project"
include(
    ":karl-core",
    ":karl-kldl",
    ":karl-room", // Assuming you created this
    ":karl-compose-ui",
    ":karl-example-desktop"
)