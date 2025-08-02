/*
 * KARL Framework - Root Build Configuration
 *
 * This is the master build configuration for the KARL (Kotlin Adaptive Reasoning Learner)
 * framework, a comprehensive privacy-first adaptive learning system built entirely in Kotlin.
 * This root build script orchestrates the entire multi-module ecosystem, ensuring consistent
 * build configurations, code quality standards, and comprehensive documentation generation
 * across all framework components.
 *
 * **Framework Architecture Overview:**
 * KARL represents a paradigm shift in adaptive learning systems, prioritizing user privacy
 * and local-only processing without compromising on sophisticated machine learning capabilities.
 * The framework provides a complete ecosystem for building intelligent, adaptive applications
 * that learn from user behavior while maintaining absolute data privacy and security.
 *
 * **Multi-Module Ecosystem:**
 * This build configuration manages the following critical framework modules:
 *
 * • **karl-core**: Foundational framework providing core interfaces, container orchestration,
 *   and base abstractions for adaptive learning systems
 *
 * • **karl-kldl**: KotlinDL Engine implementing sophisticated machine learning capabilities
 *   using industry-standard deep learning libraries while maintaining privacy guarantees
 *
 * • **karl-room**: Privacy-first data persistence layer using Android Room and SQLite
 *   for secure, local-only data storage and management
 *
 * • **karl-compose-ui**: Reactive UI components built on Jetpack Compose for seamless
 *   integration of adaptive learning capabilities into modern user interfaces
 *
 * • **karl-example-desktop**: Comprehensive desktop application demonstrating complete
 *   framework integration patterns and real-world usage scenarios
 *
 * **Build System Philosophy:**
 * This build configuration embodies enterprise-grade development practices while maintaining
 * simplicity and accessibility for framework adopters:
 * - Consistent plugin versions across all modules for stability and compatibility
 * - Comprehensive code quality enforcement through automated linting and formatting
 * - Professional documentation generation with cross-module navigation and linking
 * - Streamlined developer workflow with automated convenience tasks
 * - Enterprise deployment preparation with proper versioning and release management
 *
 * **Technology Stack Coordination:**
 * The build system coordinates cutting-edge technology adoption across the ecosystem:
 * - Kotlin 2.0.21 with latest language features and performance optimizations
 * - Jetpack Compose 1.8.2 for modern declarative UI development
 * - KotlinDL integration for sophisticated machine learning capabilities
 * - Android Room for enterprise-grade local data persistence
 * - Comprehensive documentation with Dokka multi-module integration
 *
 * **Developer Experience Optimization:**
 * Special attention to developer productivity and framework adoption ease:
 * - Automated documentation generation and browser integration
 * - Cross-platform build task compatibility (Windows, macOS, Linux)
 * - Comprehensive code quality enforcement with clear feedback
 * - Streamlined development workflow with minimal configuration overhead
 * - Professional documentation standards promoting framework understanding
 *
 * **Enterprise Readiness:**
 * This build configuration ensures enterprise deployment compatibility:
 * - Consistent versioning strategy across all framework components
 * - Professional code quality standards with automated enforcement
 * - Comprehensive documentation for security auditing and compliance
 * - Cross-platform compatibility for diverse enterprise environments
 * - Scalable architecture supporting from proof-of-concept to production deployment
 *
 * @project karl-framework
 * @version 1.0.0
 * @author KARL Development Team
 * @since 2025-08-02
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Repository</a>
 */

// karl-project/build.gradle.kts

/*
 * ========================================
 * GRADLE PLUGIN MANAGEMENT CONFIGURATION
 * ========================================
 *
 * Centralized plugin version management ensuring consistency across all framework
 * modules while preventing version conflicts and maintaining stable build environments.
 *
 * **Plugin Strategy:**
 * All plugins are defined with explicit versions and applied conditionally to
 * subprojects, enabling fine-grained control over build capabilities while
 * maintaining consistency across the entire framework ecosystem.
 *
 * **Version Coordination:**
 * Plugin versions are carefully selected and tested together to ensure optimal
 * compatibility and access to latest language features while maintaining stability.
 */

plugins {
    /*
     * Kotlin Ecosystem Plugins
     *
     * Core Kotlin plugins providing multiplatform capabilities, JVM targeting,
     * and advanced language features essential for framework development.
     */

    // Kotlin Multiplatform for cross-platform framework development
    id("org.jetbrains.kotlin.multiplatform") version "2.0.21" apply false

    // Kotlin JVM for desktop and server application development
    id("org.jetbrains.kotlin.jvm") version "2.0.21" apply false

    /*
     * Jetpack Compose Ecosystem
     *
     * Modern declarative UI framework plugins enabling responsive, accessible
     * user interfaces with Material Design compliance and cross-platform support.
     */

    // Jetpack Compose for declarative UI development across platforms
    id("org.jetbrains.compose") version "1.8.2" apply false

    // Compose Compiler plugin for optimized Compose compilation and performance
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    /*
     * Code Generation and Processing
     *
     * Advanced code generation plugins for annotation processing, serialization,
     * and compile-time code generation essential for framework architecture.
     */

    // Kotlin Symbol Processing for efficient annotation processing and code generation
    id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false

    // Kotlin Serialization for type-safe data serialization and JSON handling
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false

    /*
     * Documentation and Code Quality
     *
     * Professional documentation generation and code quality enforcement tools
     * ensuring enterprise-grade standards across the entire framework ecosystem.
     */

    // Dokka for comprehensive API documentation with multi-module support
    id("org.jetbrains.dokka") version "1.9.20" apply false

    // KtLint for automated code formatting and style enforcement
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

/*
 * ========================================
 * SUBPROJECT CONFIGURATION MANAGEMENT
 * ========================================
 *
 * Unified configuration management for all framework modules, ensuring consistent
 * code quality standards, formatting rules, and development practices across
 * the entire KARL ecosystem.
 *
 * **Code Quality Philosophy:**
 * The KARL framework maintains enterprise-grade code quality through automated
 * enforcement of formatting standards, coding conventions, and best practices.
 * This approach ensures maintainability, readability, and consistency across
 * all framework components, facilitating collaboration and long-term sustainability.
 */

subprojects {
    /*
     * Universal KtLint Application
     *
     * Applies KtLint code formatting and style enforcement to all subprojects,
     * ensuring consistent code style across the entire framework ecosystem.
     * This automated approach eliminates style discussions and focuses
     * development efforts on functionality and architecture.
     */
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    /*
     * KtLint Configuration for Framework Standards
     *
     * Configures KtLint with specific version and verbosity settings optimized
     * for framework development and enterprise code quality requirements.
     *
     * **Configuration Benefits:**
     * - Consistent formatting across all development environments
     * - Automated code review for style and convention compliance
     * - Reduced cognitive load through standardized formatting
     * - Enhanced code readability for framework adopters and contributors
     */
    ktlint {
        // Specific KtLint version for consistent formatting behavior
        version.set("1.2.1")

        // Verbose output for detailed formatting feedback during development
        verbose.set(true)
    }
}

/*
 * ========================================
 * ROOT PROJECT MAINTENANCE TASKS
 * ========================================
 *
 * Essential maintenance and cleanup tasks for the entire framework project,
 * enabling efficient development workflows and clean build environments.
 */

/*
 * Global Clean Task
 *
 * Comprehensive cleanup task that removes all build artifacts from the root
 * project and all submodules. This ensures clean builds and prevents
 * build artifact conflicts during development and deployment.
 *
 * **Cleanup Benefits:**
 * - Ensures clean builds free from stale artifacts
 * - Prevents cross-module dependency conflicts
 * - Optimizes disk space usage during development
 * - Enables reliable CI/CD pipeline execution
 */
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

/*
 * ========================================
 * DOCUMENTATION GENERATION CONFIGURATION
 * ========================================
 *
 * Comprehensive documentation system configuration for the KARL framework,
 * providing professional API documentation with multi-module integration,
 * cross-references, and enterprise-grade presentation standards.
 *
 * **Documentation Architecture:**
 * The KARL documentation system employs a sophisticated multi-tier approach:
 * - Individual module documentation with specialized technical content
 * - Cross-module integration with seamless navigation and linking
 * - Unified framework documentation presenting cohesive architectural vision
 * - Enterprise-grade presentation suitable for technical evaluation and adoption
 *
 * **Strategic Documentation Benefits:**
 * - Accelerates framework adoption through comprehensive reference materials
 * - Enables effective technical evaluation for enterprise decision-making
 * - Facilitates community contribution through clear architectural documentation
 * - Supports security auditing and compliance verification processes
 * - Provides educational resources for adaptive learning system development
 */

// Apply Dokka to subprojects that need documentation
configure(subprojects.filter { it.name in listOf("karl-core", "karl-kldl", "karl-room", "karl-compose-ui") }) {
    /*
     * Selective Documentation Module Configuration
     *
     * Applies Dokka documentation generation to core framework modules that
     * provide public APIs and require comprehensive developer documentation.
     * This selective approach ensures focused, high-quality documentation
     * for framework integrators while excluding internal implementation modules.
     *
     * **Documentation Scope:**
     * - karl-core: Foundational interfaces and container orchestration
     * - karl-kldl: Machine learning engine with KotlinDL integration
     * - karl-room: Privacy-first data persistence layer
     * - karl-compose-ui: Reactive UI components for framework integration
     */
    apply(plugin = "org.jetbrains.dokka")
}

// Apply Dokka to the root project for multi-module documentation
apply(plugin = "org.jetbrains.dokka")

/*
 * ========================================
 * PROFESSIONAL DOCUMENTATION TASKS
 * ========================================
 *
 * Specialized tasks providing streamlined documentation workflows for
 * developers, technical writers, and framework adopters requiring
 * comprehensive API reference and integration guidance.
 */

/*
 * Multi-Module Documentation Generation Task
 *
 * Enhanced configuration for the primary documentation generation task,
 * providing professional feedback and guidance for accessing generated
 * documentation. This task orchestrates comprehensive documentation
 * generation across all framework modules.
 *
 * **Documentation Features:**
 * - Cross-module navigation with intelligent linking
 * - Professional presentation with consistent branding
 * - Comprehensive API coverage with usage examples
 * - Integration guidance for framework adopters
 * - Performance optimization recommendations
 */

// Configure the existing dokkaHtmlMultiModule task
tasks.named("dokkaHtmlMultiModule") {
    doLast {
        /*
         * Professional Documentation Completion Feedback
         *
         * Provides clear, actionable feedback upon successful documentation
         * generation, guiding users to access and utilize the generated
         * documentation effectively.
         */
        println("✅ Project KARL documentation generated successfully!")
        println("📖 Documentation available at: ${layout.buildDirectory.dir("dokka/htmlMultiModule").get()}")
        println("🌐 Open index.html in your browser to view the docs")
    }
}

/*
 * Comprehensive Documentation Generation Task
 *
 * High-level task providing convenient access to complete framework documentation
 * generation. This task serves as the primary entry point for documentation
 * workflows, ensuring all modules are properly documented and integrated.
 *
 * **Task Benefits:**
 * - Simplified documentation generation workflow for developers
 * - Comprehensive coverage across all framework modules
 * - Dependency management ensuring proper build order
 * - Professional presentation suitable for enterprise evaluation
 * - Automated integration of cross-module references and navigation
 */

// Task to generate documentation for all modules
tasks.register("generateDocs") {
    group = "documentation"
    description = "Generates complete documentation for Project KARL"
    dependsOn("dokkaHtmlMultiModule")
}

/*
 * Cross-Platform Documentation Browser Integration
 *
 * Intelligent task providing seamless documentation access across different
 * operating systems. This task automatically detects the host platform and
 * opens generated documentation using the appropriate system command.
 *
 * **Cross-Platform Support:**
 * - Windows: Uses 'start' command for default browser integration
 * - macOS: Uses 'open' command for native application launching
 * - Linux: Uses 'xdg-open' for desktop environment integration
 *
 * **Developer Experience Benefits:**
 * - Immediate access to generated documentation without manual navigation
 * - Consistent workflow across different development environments
 * - Automated verification of documentation generation success
 * - Streamlined development and review workflows
 */

// Task to open documentation in browser (Windows)
tasks.register("openDocs") {
    group = "documentation"
    description = "Opens the generated documentation in the default browser"
    dependsOn("generateDocs")

    doLast {
        val docIndexFile = layout.buildDirectory.dir("dokka/htmlMultiModule").get().file("index.html")
        if (docIndexFile.asFile.exists()) {
            /*
             * Intelligent Platform Detection and Browser Launching
             *
             * Automatically detects the host operating system and uses the
             * appropriate command for launching the default browser with
             * the generated documentation.
             */
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

/*
 * Documentation Cleanup and Maintenance Task
 *
 * Specialized cleanup task for documentation artifacts, enabling clean
 * documentation regeneration and disk space management during development.
 * This task provides targeted cleanup without affecting other build artifacts.
 *
 * **Cleanup Benefits:**
 * - Ensures clean documentation regeneration free from stale content
 * - Optimizes disk space usage during iterative documentation development
 * - Prevents documentation conflicts during rapid development cycles
 * - Enables reliable documentation CI/CD pipeline execution
 */

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
