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
        withJava()
        compilations.all {
            kotlinOptions {
                jvmTarget = "21"
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