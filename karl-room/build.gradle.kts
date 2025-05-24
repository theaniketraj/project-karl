plugins {
    kotlin("multiplatform") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
    kotlin("plugin.serialization") version "1.9.10"
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                api("androidx.room:room-common:2.6.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
                api("androidx.room:room-runtime:2.7.1")
                api("androidx.room:room-ktx:2.7.1")
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
                implementation("com.google.devtools.ksp:symbol-processing:1.9.10-1.0.13")
                // ksp("androidx.room:room-compiler:2.7.1")
                add("ksp", "androidx.room:room-compiler:2.7.1")
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("androidx.room:room-testing:2.7.1")
                add("kspJvm", "androidx.room:room-compiler:2.7.1")
                // If tests use KSP:
                // kspJvm("androidx.room:room-compiler:2.6.0")
            }
        }
    }
}

configure<com.google.devtools.ksp.gradle.KspExtension> {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}