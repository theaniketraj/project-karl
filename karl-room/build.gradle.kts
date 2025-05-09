// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" // Explicit KSP version
}

val roomVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        compilations.all { kotlinOptions.jvmTarget = "1.8" }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core")) // To see core models if Room entities map to/from them
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
                // Common Room dependencies (check latest Room KMP docs if needed)
                // api("androidx.room:room-common:$roomVersion")
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
                add("kspJvm", "androidx.room:room-compiler:$roomVersion") // Using add("kspJvm", ...)
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}