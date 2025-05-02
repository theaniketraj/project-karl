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
    ":karl-sqldelight",
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
        kotlin("jvm") version "1.9.23" apply false
    }
}