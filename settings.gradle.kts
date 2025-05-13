// karl-project/settings.gradle.kts
// rootProject.name defines the name of your root project.
// This name is used by Gradle and can be referenced in other build files.
rootProject.name = "karl-project"

// include() function is used to include submodules in the build.
// The string parameter is the path to the module directory relative to the root.
// Each module directory we planned is listed here.
include(
    ":karl-core",
    ":karl-kldl",
    ":karl-room",
    ":karl-compose-ui", // Include if you plan to use Compose UI module
    ":karl-example-desktop" // Include the example application module
    // Note: The 'docs' folder usually doesn't need to be a Gradle module
    // unless you use a Gradle plugin to build documentation.
)

// You might need to specify repository for plugin resolution here
// depending on your Gradle version and setup, but typically,
// plugins are resolved from standard repositories like Maven Central by default.
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // For Compose plugin
         // Add any other repositories needed for plugins (e.g., SQLDelight plugin)
    }
    plugins {
        kotlin("jvm") version "2.1.21" apply false

        // Declare Kotlin Multiplatform plugin
        kotlin("multiplatform") version "2.1.21" apply false // Or your Kotlin version

        // Declare Jetpack Compose plugin
        id("org.jetbrains.compose") version "1.8.0" apply false // Or your Compose version

        // Declare KSP plugin for Kotlin Multiplatform
        id("com.google.devtools.ksp") version "2.2.0-Beta2-2.0.1" apply false // KSP version compatible with our Kotlin version

        id("org.jetbrains.kotlin.plugin.compose") version "2.1.21" apply false
    }
}