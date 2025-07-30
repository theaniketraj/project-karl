// karl-project/karl-compose-ui/build.gradle.kts

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":karl-core"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material) // Add Material 2 dependency
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.uiTooling)
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
                implementation(compose.uiTooling)
            }
        }
    }
}
