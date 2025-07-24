import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        compilations.named("main") {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
        compilations.named("test") {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
                implementation(libs.kotlinx.coroutines.core) // Implementation needs its own coroutines
                implementation(libs.kotlindl.api)
                implementation(libs.kotlindl.dataset)
            }
        }
    }
}
