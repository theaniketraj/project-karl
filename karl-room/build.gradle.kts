// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
}

// COMMENT OUT or DELETE these lines for now:
// val roomVersion: String by rootProject.ext
// val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                // HARDCODE Coroutines version
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                // api("androidx.room:room-common:2.6.1") // Example: HARDCODE Room version
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                // HARDCODE Room versions
                api("androidx.room:room-runtime:2.6.1")
                api("androidx.room:room-ktx:2.6.1")
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")

                // KSP dependency with HARDCODED Room version
                dependencies.add("kspJvm", "androidx.room:room-compiler:2.6.1")
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}