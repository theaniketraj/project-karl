plugins {
    // Apply plugins using aliases from the version catalog
    // The 'alias(...)' function is automatically available.
    alias(libs.plugins.jetbrainsCompose) apply false
    // alias(libs.plugins.sqldelight) apply false // If you still use it
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    id("com.google.devtools.ksp") apply false
    // KSP plugin is usually applied in modules, not root with 'apply false'
}

allprojects {
    tasks.withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven("https.maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

tasks.register("clean") {
    delete(rootProject.layout.buildDirectory)
}