import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    kotlin("plugin.serialization") version "1.9.10"
}

// sourceSets["main"].java.srcDir("build/generated/ksp/main/kotlin")

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
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

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")
                api("androidx.room:room-runtime:2.6.1")
                api("androidx.room:room-ktx:2.6.1")
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.10-1.0.13")
                implementation("com.google.devtools.ksp:symbol-processing:1.9.10-1.0.13")
                // ksp("androidx.room:room-compiler:2.7.1")
                //add("ksp", "androidx.room:room-compiler:2.6.1")
                implementation(libs.androidx.room.compiler)
            }
        }

        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation("androidx.room:room-testing:2.6.1")
                //ksp("androidx.room:room-compiler:2.6.1")
                implementation(libs.androidx.room.compiler)
                // If tests use KSP:
                // kspJvm("androidx.room:room-compiler:2.6.0")
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}