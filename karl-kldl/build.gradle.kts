import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URL

plugins {
    alias(libs.plugins.kotlinMultiplatform)
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
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.kotlin.stdlib.jdk8)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlindl.api)
                implementation(libs.kotlindl.dataset)
                implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")
            }
        }
    }
}

// Dokka configuration for karl-kldl module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("KARL KotlinDL Engine")
            moduleVersion.set(project.version.toString())

            includes.from("Module.md")

            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-kldl/src"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlindl/"))
            }
        }
    }
}
