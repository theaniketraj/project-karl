// karl-project/build.gradle.kts

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.0.21" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.21" apply false
    id("org.jetbrains.compose") version "1.8.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("1.2.1")
        verbose.set(true)
    }
}

// allprojects {
//     repositories {
//         mavenCentral()
//         google()
//         maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//     }
// }

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
