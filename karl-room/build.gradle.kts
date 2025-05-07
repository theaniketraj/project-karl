// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
}

// Access versions from root project
val roomVersion: String by rootProject.ext
// val kspVersion: String by rootProject.ext // Not directly used in dependency string here
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        // withJava() // Optional
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

                // Room Runtime & KTX for JVM
                api("androidx.room:room-runtime:$roomVersion")
                api("androidx.room:room-ktx:$roomVersion")

                // SQLite framework APIs
                api("androidx.sqlite:sqlite-framework:2.4.0")

                // SQLite JDBC driver for JVM
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")

                // KSP dependency for the JVM target
                // Use the explicit 'add' method with the configuration name string
                dependencies.add("kspJvm", "androidx.room:room-compiler:$roomVersion")
            }
        }
        // ... commonTest, jvmTest ...
        // Example for tests using 'add':
        // val jvmTest by getting {
        //     dependencies {
        //         dependencies.add("kspJvmTest", "androidx.room:room-compiler:$roomVersion")
        //         implementation("androidx.room:room-testing:$roomVersion")
        //     }
        // }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}