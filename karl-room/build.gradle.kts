// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" // Or your compatible KSP version
    kotlin("plugin.serialization") version "1.9.10" // Or your compatible Kotlin version
}

// Access versions from your version catalog (libs.versions.toml)
// val roomVersion: String by rootProject.ext // Old way, use catalog instead

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "20"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // CRITICAL: This makes the interfaces and models from :karl-core
                // available to this module's common code, and by extension, to jvmMain.
                // Use 'api' to ensure these types are transitively visible.
                api(project(":karl-core"))

                // Coroutines are needed for suspend functions in the DataStorage interface
                api(libs.kotlinx.coroutines.core)

                // Common Room annotations for KMP
                api(libs.androidx.room.common)

                // Serialization for TypeConverters
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)

                // Room Runtime & KTX for JVM target
                api(libs.androidx.room.runtime)
                api(libs.androidx.room.ktx)

                // SQLite framework and JDBC driver
                api(libs.androidx.sqlite.framework)
                implementation(libs.sqlite.jdbc)

                // KSP processor for the JVM target
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
                implementation("com.google.devtools.ksp:symbol-processing:1.9.10-1.0.13")
            }
        }
        // ... jvmTest dependencies ...
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}
