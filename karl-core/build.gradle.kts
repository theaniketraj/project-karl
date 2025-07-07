// karl-project/karl-core/build.gradle.kts

plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

kotlin {
    jvm {
        // withJava() // Optional, only if you mix Java and Kotlin source files in this module
        compilations.all {
            kotlinOptions {
                jvmTarget = "20"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.common)
                api(libs.kotlinx.coroutines.core)
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
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
        }
    }
}
