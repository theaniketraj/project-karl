plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "2.2.0-Beta2-2.0.1"
}

val roomVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                api("androidx.room:room-runtime:$roomVersion")   // Room runtime
                api("androidx.room:room-ktx:$roomVersion")       // Room coroutine extensions
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")

                // KSP processor for Room
                ksp("androidx.room:room-compiler:$roomVersion")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("androidx.room:room-testing:$roomVersion") // Room testing utilities
                // if you need KSP for test sources
                // ksp("androidx.room:room-compiler:$roomVersion")
            }
        }
    }
}

configure<com.google.devtools.ksp.gradle.KspExtension> {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}