// karl-project/karl-core/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.common)
                api(libs.kotlinx.coroutines.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
            }
        }
    }
}