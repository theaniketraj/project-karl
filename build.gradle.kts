// karl-project/build.gradle.kts

plugins {
    id("org.jetbrains.kotlin.multiplatform") version "2.0.21" apply false
    id("org.jetbrains.kotlin.jvm") version "2.0.21" apply false
    id("org.jetbrains.compose") version "1.8.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("1.2.1")
        verbose.set(true)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Apply Dokka to subprojects that need documentation
configure(subprojects.filter { it.name in listOf("karl-core", "karl-kldl", "karl-room", "karl-compose-ui") }) {
    apply(plugin = "org.jetbrains.dokka")
}

// Apply Dokka to the root project for multi-module documentation
apply(plugin = "org.jetbrains.dokka")

// Configure the existing dokkaHtmlMultiModule task
tasks.named("dokkaHtmlMultiModule") {
    doLast {
        println("✅ Project KARL documentation generated successfully!")
        println("📖 Documentation available at: ${layout.buildDirectory.dir("dokka/htmlMultiModule").get()}")
        println("🌐 Open index.html in your browser to view the docs")
    }
}

// Task to generate documentation for all modules
tasks.register("generateDocs") {
    group = "documentation"
    description = "Generates complete documentation for Project KARL"
    dependsOn("dokkaHtmlMultiModule")
}

// Task to open documentation in browser (Windows)
tasks.register("openDocs") {
    group = "documentation"
    description = "Opens the generated documentation in the default browser"
    dependsOn("generateDocs")

    doLast {
        val docIndexFile = layout.buildDirectory.dir("dokka/htmlMultiModule").get().file("index.html")
        if (docIndexFile.asFile.exists()) {
            val os = System.getProperty("os.name").lowercase()
            when {
                os.contains("windows") -> {
                    exec {
                        commandLine("cmd", "/c", "start", docIndexFile.asFile.absolutePath)
                    }
                }
                os.contains("mac") -> {
                    exec {
                        commandLine("open", docIndexFile.asFile.absolutePath)
                    }
                }
                else -> {
                    exec {
                        commandLine("xdg-open", docIndexFile.asFile.absolutePath)
                    }
                }
            }
            println("📖 Opening documentation in your default browser...")
        } else {
            println("❌ Documentation not found. Run 'generateDocs' first.")
        }
    }
}

// Task to clean documentation output
tasks.register("cleanDocs") {
    group = "documentation"
    description = "Cleans the generated documentation"

    doLast {
        val docsDir = layout.buildDirectory.dir("dokka").get().asFile
        if (docsDir.exists()) {
            docsDir.deleteRecursively()
            println("🧹 Documentation cleaned successfully!")
        }
    }
}
