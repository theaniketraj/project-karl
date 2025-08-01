// karl-project/karl-compose-ui/build.gradle.kts

import java.net.URL

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.dokka")
}

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":karl-core"))
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material) // Add Material 2 dependency
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.uiTooling)
                implementation(libs.kotlinx.coroutines.core)
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
                implementation(compose.uiTooling)
            }
        }
    }
}

// Dokka configuration for karl-compose-ui module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            moduleName.set("KARL Compose UI")
            moduleVersion.set(project.version.toString())

            includes.from("Module.md")

            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-compose-ui/src"))
                remoteLineSuffix.set("#L")
            }

            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/kotlin/androidx/compose/package-summary"))
            }
        }
    }
}
