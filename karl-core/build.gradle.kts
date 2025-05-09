plugins {
    kotlin("multiplatform") // Ensure version matches root
}

val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm {
        // withJava()
    }
    // NO OTHER KMP TARGETS HERE for now

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                // Expose coroutines types like Job and CoroutineScope used in its API
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
    }
}