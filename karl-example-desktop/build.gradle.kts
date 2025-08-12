/*
 * KARL Desktop Example Application - Build Configuration
 *
 * This build script configures a complete desktop example application that demonstrates
 * the full capabilities of the KARL (Kotlin Adaptive Reasoning Learner) framework.
 * This example serves as both a functional demonstration and a comprehensive integration
 * reference for developers implementing KARL in production desktop applications.
 *
 * **Application Purpose and Architecture:**
 * The KARL Desktop Example showcases real-world integration patterns for privacy-first
 * adaptive learning systems in desktop environments. It demonstrates how to build
 * responsive, intelligent user interfaces that adapt to user behavior while maintaining
 * strict local-only data processing and privacy preservation.
 *
 * **Key Demonstration Features:**
 * - Complete KARL framework integration from initialization to runtime
 * - Reactive UI components with real-time learning progress visualization
 * - Local SQLite database integration for persistent learning state
 * - Professional desktop application packaging and distribution
 * - Cross-platform deployment with native installers
 * - Performance-optimized UI with smooth animations and responsive interactions
 *
 * **Educational Value:**
 * This example provides production-ready patterns for:
 * - KARL container lifecycle management in desktop applications
 * - StateFlow integration with Jetpack Compose for reactive UIs
 * - Local database setup and configuration for learning data persistence
 * - Professional desktop application architecture and design patterns
 * - Error handling and graceful degradation in adaptive systems
 * - Performance optimization techniques for machine learning UIs
 *
 * **Target Audience:**
 * - Framework integrators learning KARL implementation patterns
 * - Desktop application developers seeking adaptive UI examples
 * - Enterprise architects evaluating KARL for production use
 * - Open source contributors understanding framework capabilities
 * - Privacy-conscious developers building local-only intelligent applications
 *
 * **Deployment and Distribution:**
 * Configured for professional desktop application distribution with native
 * installers for Windows (MSI), macOS (DMG), and Linux (DEB, RPM) platforms.
 * Demonstrates enterprise-grade packaging and deployment strategies.
 *
 * @module karl-example-desktop
 * @since 1.0.0
 * @author KARL Development Team
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Documentation</a>
 */

// karl-project/karl-example-desktop/build.gradle.kts
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

/*
 * ========================================
 * GRADLE PLUGIN CONFIGURATION
 * ========================================
 *
 * Essential plugins for building a professional desktop application with
 * Jetpack Compose UI, modern Kotlin features, and comprehensive packaging.
 */

plugins {
    // Kotlin JVM plugin for desktop application development
    alias(libs.plugins.kotlinJvm)

    // Jetbrains Compose plugin for declarative UI development
    alias(libs.plugins.jetbrainsCompose)

    // Compose Compiler plugin for optimized Compose compilation
    alias(libs.plugins.compose.compiler)
}

/*
 * ========================================
 * PROJECT IDENTITY CONFIGURATION
 * ========================================
 *
 * Establishes clear project identity for the KARL desktop example application,
 * ensuring proper package management and version tracking for distribution.
 */

// Maven group identifier for the example application
group = "com.karl.example"

// Inherit unified version from root (VERSION file)
// version set in root build (frameworkVersion)

/*
 * ========================================
 * DEPENDENCY CONFIGURATION
 * ========================================
 *
 * Comprehensive dependency setup that demonstrates complete KARL framework
 * integration along with essential desktop application infrastructure.
 *
 * **Dependency Categories:**
 * - Core Framework: Complete KARL module ecosystem integration
 * - UI Framework: Jetpack Compose Desktop with Material Design components
 * - Data Persistence: SQLite integration for local learning state storage
 * - Concurrency: Kotlin Coroutines for reactive programming patterns
 * - Standard Libraries: Enhanced Kotlin standard library with JDK8+ features
 *
 * **Architecture Principles:**
 * - Local-only processing: No external network dependencies
 * - Privacy-first design: All data remains on user's device
 * - Performance optimized: Minimal overhead for responsive UI
 * - Enterprise ready: Production-grade dependency selection
 */

dependencies {
    /*
     * Core Language and Concurrency Support
     *
     * Foundation libraries providing essential language features and
     * asynchronous programming capabilities for responsive desktop applications.
     */

    // Enhanced Kotlin standard library with JDK8+ extensions for collections and streams
    implementation(libs.kotlin.stdlib.jdk8)

    // Kotlin Coroutines for reactive programming and StateFlow integration
    implementation(libs.kotlinx.coroutines.core)

    /*
     * Jetpack Compose Desktop UI Framework
     *
     * Modern declarative UI framework providing responsive, accessible, and
     * professional desktop application interfaces with Material Design compliance.
     */

    // Complete Compose Desktop runtime for current operating system
    implementation(compose.desktop.currentOs)

    // Extended Material Design icon library for professional UI design
    implementation(compose.materialIconsExtended)

    /*
     * KARL Framework Integration
     *
     * Complete integration of all KARL framework modules, demonstrating
     * the full ecosystem of privacy-first adaptive learning capabilities.
     *
     * **Module Integration:**
     * - karl-core: Fundamental framework interfaces and orchestration
     * - karl-kldl: Learning Definition Language for declarative configurations
     * - karl-compose-ui: Reactive UI components for KARL integration
     */

    // KARL Core framework providing fundamental interfaces and container orchestration
    implementation(project(":karl-core"))

    // KARL Learning Definition Language for declarative learning configurations
    implementation(project(":karl-kldl"))

    // KARL Compose UI components for reactive framework integration
    implementation(project(":karl-compose-ui"))

    /*
     * Local Data Persistence
     *
     * SQLite-based local storage solution ensuring all learning data remains
     * on the user's device while providing reliable persistence capabilities.
     *
     * **Privacy Benefits:**
     * - Zero external data transmission
     * - Full user control over data storage location
     * - Encrypted storage capabilities for sensitive learning data
     * - GDPR and privacy regulation compliance through local-only processing
     */

    // SQLite JDBC driver for local database operations
    implementation(libs.sqlite.jdbc)

    // SQLDelight JDBC driver for type-safe database operations
    implementation(libs.sqldelight.driver.jdbc)
}

/*
 * ========================================
 * KOTLIN COMPILATION CONFIGURATION
 * ========================================
 *
 * Advanced Kotlin compilation settings optimized for desktop application
 * performance and modern JVM feature utilization.
 *
 * **Compilation Strategy:**
 * - Java 21 target for access to cutting-edge JVM capabilities
 * - Performance optimizations for machine learning workloads
 * - Memory-efficient compilation for responsive desktop applications
 * - Advanced optimization flags for production deployment
 */

// Configure all Kotlin compilation tasks with modern JVM targeting
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        /*
         * Java 21 Target Benefits for KARL Desktop Applications:
         *
         * - Virtual threads for improved concurrency in learning algorithms
         * - Pattern matching for cleaner state management and data processing
         * - Record classes for immutable data transfer objects and events
         * - Enhanced garbage collection for smoother UI animations
         * - Foreign Function & Memory API for potential native optimizations
         * - Structured concurrency for safer async learning operations
         * - Performance improvements in collection processing and memory management
         */
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

/*
 * JVM Toolchain Configuration
 *
 * Ensures consistent Java 21 runtime environment across development,
 * testing, and production deployment scenarios. This guarantees that
 * all modern JVM features are available and optimally configured.
 *
 * **Toolchain Benefits:**
 * - Consistent runtime behavior across different development environments
 * - Automatic Java 21 distribution management
 * - Optimal JVM performance settings for desktop applications
 * - Simplified deployment and distribution pipeline
 */
kotlin {
    // Specify Java 21 toolchain for consistent development and runtime environment
    jvmToolchain(21)
}

/*
 * ========================================
 * COMPOSE DESKTOP APPLICATION CONFIGURATION
 * ========================================
 *
 * Professional desktop application packaging and distribution configuration
 * for cross-platform deployment with native installers and optimal user experience.
 *
 * **Deployment Strategy:**
 * This configuration enables enterprise-grade desktop application distribution
 * with platform-specific installers that provide native integration and
 * professional installation experience across Windows, macOS, and Linux.
 *
 * **User Experience Considerations:**
 * - Native look and feel integration with operating system
 * - Proper application lifecycle management and system integration
 * - Professional branding and package identification
 * - Streamlined installation and update processes
 * - System resource optimization for desktop environments
 */

compose.desktop {
    application {
        /*
         * Application Entry Point Configuration
         *
         * Specifies the main class containing the application entry point.
         * This class demonstrates complete KARL framework initialization,
         * UI setup, and application lifecycle management.
         */
        mainClass = "com.karl.example.DesktopExampleAppKt"

        /*
         * Native Distribution Configuration
         *
         * Professional cross-platform packaging configuration that generates
         * native installers for major desktop operating systems. This ensures
         * optimal user experience and seamless system integration.
         *
         * **Platform Coverage:**
         * - Windows: MSI installer with Windows-specific optimizations
         * - macOS: DMG package with Apple Silicon and Intel support
         * - Linux: DEB package for Debian/Ubuntu distributions
         * - Linux: RPM package for Red Hat/Fedora/SUSE distributions
         *
         * **Enterprise Benefits:**
         * - Silent installation capabilities for enterprise deployment
         * - Digital signature support for security and trust
         * - Custom branding and icon integration
         * - Automatic update mechanism support
         * - System integration with proper file associations
         *
         * **User Experience Features:**
         * - Native operating system look and feel
         * - Proper application menu and shortcut integration
         * - System tray and notification support
         * - Platform-specific keyboard shortcuts and conventions
         * - Accessibility compliance with operating system standards
         */
        nativeDistributions {
            /*
             * Target Platform Configuration
             *
             * Specifies the native installer formats to generate for each
             * supported operating system, ensuring comprehensive platform coverage.
             */
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Rpm)

            /*
             * Package Identity Configuration
             *
             * Establishes clear application identity for operating system
             * integration, package management, and user identification.
             */

            // User-friendly application name displayed in system menus and installers
            packageName = "KarlExampleApp"

            // Package version follows unified framework version
            packageVersion = project.version.toString()
        }
    }
}
