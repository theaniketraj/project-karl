// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" // Ensure version is explicit if not in settings.gradle.kts pluginManagement
}

// Access versions
val roomVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext

// ---  TOP-LEVEL DEPENDENCIES BLOCK ---
//      dependencies
            // Declare KSP annotation processors at this top level.
            // This is the most common and robust way for KMP modules.
//          ksp("androidx.room:room-compiler:$roomVersion") // <-- KSP PROCESSOR DECLARED HERE
//      }
// --- END TOP-LEVEL DEPENDENCIES BLOCK ---

//kotlin {
    // ...
//    sourceSets {
        // ...
//        val jvmMain by getting {
//            dependencies {
                // ...
//                kspJvm("androidx.room:room-compiler:$roomVersion")
//            }
//        }
//    }
//}

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
        // val jvmTest by getting {
        //     dependencies {
        //         kspJvmTest("androidx.room:room-compiler:$roomVersion")
        //         implementation("androidx.room:room-testing:$roomVersion")
        //     }
        // }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

afterEvaluate {
    println("Available configurations in :karl-room:")
    project.configurations.forEach { conf ->
        println("- ${conf.name}")
    }

    kotlin.sourceSets.forEach { sourceSet ->
        println("KSP configurations for sourceSet ${sourceSet.name}:")
        try {
            // Try to find KSP configurations by convention
            // This is highly dependent on KSP internal naming
            val kspConfigName = "ksp${sourceSet.name.capitalize()}" // e.g., kspJvm, kspCommonMain
            project.configurations.findByName(kspConfigName)?.let {
                println("  - Found: $kspConfigName")
            }
            val kspMetadataConfigName = "ksp${sourceSet.name.capitalize()}Metadata" // e.g., kspCommonMainMetadata
            project.configurations.findByName(kspMetadataConfigName)?.let {
                println("  - Found: $kspMetadataConfigName")
            }
        } catch (e: Exception) {
            println("  - Error inspecting KSP configurations for ${sourceSet.name}: ${e.message}")
        }
    }
}