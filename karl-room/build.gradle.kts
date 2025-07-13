import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm {
        compilations.named("main") {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
        compilations.named("test") {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
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

                // KSP processor for the JVM target
                implementation("com.google.devtools.ksp:symbol-processing-api:1.9.23-1.0.19")
                implementation("com.google.devtools.ksp:symbol-processing:1.9.23-1.0.19")
            }
        }
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}
