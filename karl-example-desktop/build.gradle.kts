import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") // Apply Kotlin JVM plugin for the desktop app
    id("org.jetbrains.compose") // Apply Compose plugin
    // Apply SQLDelight plugin IF your DataStorage implementation requires it
    // and needs code generation in this module (usually not needed if the implementation
    // is self-contained in :karl-sqldelight)
    // id("app.cash.sqldelight") version "2.0.1" // Use version from root project if defined
}

// Access versions from root project
val kotlinVersion: String by rootProject.ext
val composeVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext
val sqldelightVersion: String by rootProject.ext // Get SQLDelight version if needed

group = "com.karl.example" // Define group ID for this application module
version = "1.0.0" // Define version for this application module

repositories {
    // Repositories are usually inherited from root project's allprojects block
    // If not, declare mavenCentral(), google(), etc. here
}

dependencies {
    // Kotlin standard library
    implementation(kotlin("stdlib-jdk8")) // Use JVM stdlib

    // Coroutines (if not pulled transitively, good to be explicit)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    // Compose Desktop Application Runtime
    // Use implementation(compose.desktop.currentOs) if you just need libs
    // Use implementation(compose.desktop.application) for full app setup helpers
    implementation(compose.desktop.currentOs) // Provides 'application' entry point and packaging

    // --- KARL Module Dependencies ---
    implementation(project(":karl-core"))
    implementation(project(":karl-kldl"))       // Depends on KotlinDL implementation
    implementation(project(":karl-room")) // Depends on SQLDelight implementation
    implementation(project(":karl-compose-ui")) // Depends on the Compose UI components

    // --- Dependencies needed by KARL Implementations ---
    // Example: SQLDelight driver (must match the one used in :karl-sqldelight if setup there)
    // If :karl-sqldelight handles its own driver dependency, you might not need this here.
    // Check the requirements of your :karl-sqldelight module.
    implementation("app.cash.sqldelight:sqlite-driver:$sqldelightVersion") // Example: JDBC SQLite driver

    // Add any other specific libraries your example app needs
}

// SQLDelight configuration (only if generating DB classes directly in this module)
// sqldelight {
//     databases {
//         create("KarlDatabase") { // Must match the database name used in SQLDelightDataStorage
//             packageName.set("com.karl.example.db") // Package for generated classes
//             // If the .sq files are in :karl-sqldelight module, link them:
//             // srcDirs.setFrom(files("../karl-sqldelight/src/commonMain/sqldelight"))
//         }
//     }
// }

compose.desktop {
    application {
        // Main entry point class (generated from file name usually)
        mainClass = "com.karl.example.DesktopExampleAppKt" // Adjust if your file name/package differs

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm) // Choose desired formats
            packageName = "KarlExampleApp" // Name for installer/package
            packageVersion = "1.0.0" // Version for installer/package

            // Optional: Configure vendor, description, icons, etc.
            // vendor = "Karl Inc."
            // description = "Example Desktop App for Project KARL"
            // windows { ... }
            // macOS { ... }
            // linux { ... }
        }
    }
}