// karl-project/karl-core/build.gradle.kts
plugins {
    kotlin("multiplatform") // Version inherited from settings or root
}

val kotlinxCoroutinesVersion: String by rootProject.ext

kotlin {
    jvm { // This target ensures JVM artifacts are published
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }
    // Add other targets (androidTarget, iosX64, etc.) if karl-core is truly multiplatform

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                // Use 'api' so that modules depending on :karl-core also get these transitive types
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                // commonMain is implicitly a dependency here
            }
        }
    }
}