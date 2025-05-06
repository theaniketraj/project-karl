// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") // KSP plugin is essential for Room
}

// Access versions from root project
val roomVersion: String by rootProject.ext
val kspVersion: String by rootProject.ext // Ensure this is defined in root and compatible
val kotlinxCoroutinesVersion: String by rootProject.ext

dependencies {
    ksp("androidx.room:room-compiler:$roomVersion")
}


kotlin {
    jvm {
        // withJava() // Optional
    }
    // Add other targets like androidTarget() if you plan to support Room on Android

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core")) // Expose core interfaces
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

                // Room KMP common dependencies (if any, check latest Room KMP docs)
                // Sometimes room-common or similar might be needed here.
                // For now, let's assume room-runtime and room-ktx in jvmMain cover enough.
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // Room Dependencies for JVM
                api("androidx.room:room-runtime:$roomVersion")
                api("androidx.room:room-ktx:$roomVersion")
                // ksp("androidx.room:room-compiler:$roomVersion") // KSP annotation processor for Room

                // SQLite framework APIs needed by Room
                api("androidx.sqlite:sqlite-framework:2.4.0") // Or latest stable

                // The actual SQLite JDBC driver for JVM environments
                implementation("org.xerial:sqlite-jdbc:3.43.0.0") // Or latest stable
            }
        }
        // commonTest, jvmTest ...
        // val jvmTest by getting {
        //     dependencies {
        //         implementation("androidx.room:room-testing:$roomVersion")
        //     }
        // }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    // Potentially configure target for KSP if needed by specific KSP/Room KMP versions
    // Find the correct syntax from Room KMP documentation if this is an issue
    // Currently, KSP usually runs for each target that applies it.
}