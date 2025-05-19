// karl-project/karl-room/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization) // For MapConverter with kotlinx.serialization
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
                api(project(":karl-core"))
                api(libs.kotlinx.coroutines.core)
                api(libs.androidx.room.common)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
                api(libs.androidx.room.runtime)
                api(libs.androidx.room.ktx)
                api(libs.androidx.sqlite.framework)
                implementation(libs.sqlite.jdbc)

                kspJvm(libs.androidx.room.compiler)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.androidx.room.testing)
                // kspJvmTest(libs.androidx.room.compiler) // If tests use KSP
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}