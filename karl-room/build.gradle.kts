// karl-project/karl-room/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp") version "2.2.0-Beta2-2.0.1" // explicit KSP plugin version :contentReference[oaicite:0]{index=0}
}

val roomVersion: String by rootProject.ext
val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8" // or "11"
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
                api("androidx.room:room-runtime:$roomVersion")   // runtime :contentReference[oaicite:1]{index=1}
                api("androidx.room:room-ktx:$roomVersion")       // coroutine extensions :contentReference[oaicite:2]{index=2}
                api("androidx.sqlite:sqlite-framework:2.4.0")
                implementation("org.xerial:sqlite-jdbc:3.43.0.0")

                // KSP processor for the JVM target in KMP
                add("kspJvm", "androidx.room:room-compiler:$roomVersion")  // code generation :contentReference[oaicite:3]{index=3}
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation("androidx.room:room-testing:$roomVersion") // testing utilities :contentReference[oaicite:4]{index=4}
                // if you really must process tests with Room's compiler:
                // add("kspJvmTest", "androidx.room:room-compiler:$roomVersion")
            }
        }
    }
}

ksp {
    // Room schema location & incremental processing
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}
