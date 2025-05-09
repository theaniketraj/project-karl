// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" // Ensure version is explicit if not in settings.gradle.kts pluginManagement
}

// Access versions
val roomVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8" // Or "11"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                // api("androidx.room:room-common:$roomVersion") // Example
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                api("androidx.room:room-runtime:$roomVersion")
                api("androidx.room:room-ktx:$roomVersion")
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0") // Or latest

                // KSP processor for the JVM target
                // ksp("androidx.room:room-compiler:$roomVersion") // <-- USE THIS DIRECT SYNTAX
                // add("kspJvm", "androidx.room:room-compiler:$roomVersion")
                kspJvm("androidx.room:room-compiler:$roomVersion")
            }
        }
        // commonTest, jvmTest ...
         val jvmTest by getting {
               dependencies {
                   kspJvmTest("androidx.room:room-compiler:$roomVersion")
                   implementation("androidx.room:room-testing:$roomVersion")
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}