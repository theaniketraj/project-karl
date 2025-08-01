import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URL

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinSerialization)
    id("org.jetbrains.dokka")
}

kotlin {
    jvm {
        withJava()
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":karl-core"))
                // Using room-common for commonMain
                implementation(libs.androidx.room.common)
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
            }
        }
    }
}

dependencies {
    // Temporarily commented out to fix build - will need to be restored and fixed
    // add("kspJvm", libs.androidx.room.compiler)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

// Dokka configuration for karl-room module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("KARL Room Storage")
            moduleVersion.set(project.version.toString())

            includes.from("Module.md")

            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-room/src"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/androidx/room/package-summary"))
            }
        }
    }
}
