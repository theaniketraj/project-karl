// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") // KSP plugin itself
}

// Access versions from root project
val roomVersion: String by rootProject.ext
// val kspVersion: String by rootProject.ext // Not directly used in dependency string here
val kotlinxCoroutinesVersion: String by rootProject.ext

// REMOVE any top-level ksp dependency declaration if you had one
// dependencies {
//    // ksp("androidx.room:room-compiler:$roomVersion") // <-- REMOVE FROM HERE
// }

kotlin {
    jvm { // Your JVM target
        // withJava() // Optional
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

                // Common Room KMP dependencies (if any are needed at this level)
                // api("androidx.room:room-common:$roomVersion") // Example, check Room docs
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))

                // Room Runtime & KTX for JVM
                api("androidx.room:room-runtime:$roomVersion")
                api("androidx.room:room-ktx:$roomVersion")

                // SQLite framework APIs
                api("androidx.sqlite:sqlite-framework:2.4.0") // Or latest stable

                // SQLite JDBC driver for JVM
                implementation("org.xerial:sqlite-jdbc:3.43.0.0") // Or latest stable

                // KSP dependency for the JVM target
                // Use the target-specific KSP configuration 'kspJvm'
                kspJvm("androidx.room:room-compiler:$roomVersion")
//                add("kspJvm", "androidx.room:room-compiler:$roomVersion") // <-- CORRECTED WAY
                // OR, if using older Gradle/KSP, it might just be:
                // ksp("androidx.room:room-compiler:$roomVersion") // But this was giving you issues
                // The add("kspJvm", ...) is more robust for KMP
            }
        }
        // ... commonTest, jvmTest ...
        // val jvmTest by getting {
        //     dependencies {
        //         add("kspJvmTest", "androidx.room:room-compiler:$roomVersion") // For running KSP during tests
        //         implementation("androidx.room:room-testing:$roomVersion")
        //     }
        // }
    }
}

ksp { // KSP arguments block remains the same
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    // If KSP needs to know about all targets, this might be needed,
    // but usually it runs per target where 'ksp<TargetName>' is used.
    // blockTestProcessing(true) // Example KSP argument
}