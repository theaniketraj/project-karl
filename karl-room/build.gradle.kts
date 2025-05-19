// karl-project/karl-room/build.gradle.kts
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    // DO NOT apply alias(libs.plugins.androidxRoom) here unless it's an actual plugin
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
                api(libs.kotlinx.coroutines.core) // Reference from catalog
                api(libs.androidx.room.common)    // Reference from catalog
                implementation(libs.kotlinx.serialization.json) // Reference from catalog
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8) // Reference from catalog
                api(libs.androidx.room.runtime)
                api(libs.androidx.room.ktx)
                api(libs.androidx.sqlite.framework)
                implementation(libs.sqlite.jdbc)

                // KSP processor for the JVM target
                kspJvm(libs.androidx.room.compiler) // Reference from catalog
            }
        }
        // ... jvmTest ...
    }
}

ksp { // This is the KSP extension configuration block
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}