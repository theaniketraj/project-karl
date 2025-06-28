// karl-project/karl-kldl/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"

            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                implementation(libs.kotlinx.coroutines.core) // Or 'api' if interfaces here use coroutine types
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
                implementation(libs.kotlindl.api)
                implementation(libs.kotlindl.dataset)
                // implementation(libs.kotlindl.tensorflow) // If needed
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
        }
    }
}