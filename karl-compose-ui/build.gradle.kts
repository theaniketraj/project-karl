/*
 * KARL Compose UI Module - Build Configuration
 *
 * This build script configures the KARL Compose UI module, which provides Jetpack Compose
 * components and UI utilities specifically designed for integrating KARL (Kotlin Adaptive
 * Reasoning Learner) containers into modern Android and desktop applications.
 *
 * **Module Purpose and Architecture:**
 * The karl-compose-ui module serves as the primary UI integration layer for the KARL framework,
 * offering reactive Compose components that seamlessly connect with KARL containers to provide
 * real-time feedback on learning progress, prediction display, and user interaction controls.
 *
 * **Key Capabilities:**
 * - Reactive state management components for KARL container integration
 * - Professional UI components for learning progress visualization
 * - Interactive controls for demonstration and testing of KARL functionality
 * - Cross-platform support for Android and desktop applications
 * - Material Design compliance with both Material 2 and Material 3 support
 *
 * **Target Platforms:**
 * - JVM (Desktop applications using Compose Desktop)
 * - Android (Mobile applications using Jetpack Compose) - Future extension
 * - Web (Compose for Web) - Future extension capability
 *
 * **Integration Patterns:**
 * This module demonstrates and provides production-ready patterns for:
 * - StateFlow integration with Compose for reactive UI updates
 * - KARL container lifecycle management within Compose applications
 * - Professional UI components suitable for enterprise applications
 * - Accessibility-conscious design following Material Design guidelines
 *
 * **Dependencies and Technology Stack:**
 * - Kotlin Multiplatform for cross-platform compatibility
 * - Jetpack Compose for modern declarative UI development
 * - Material Design components for professional appearance
 * - Kotlin Coroutines for asynchronous state management
 * - KARL Core module for framework integration
 *
 * @module karl-compose-ui
 * @since 1.0.0
 * @author KARL Development Team
 */

import java.net.URL

/*
 * ========================================
 * GRADLE PLUGIN CONFIGURATION
 * ========================================
 *
 * Configure essential Gradle plugins required for Kotlin Multiplatform
 * development with Jetpack Compose support and comprehensive documentation.
 */

plugins {
    // Kotlin Multiplatform plugin for cross-platform development support
    kotlin("multiplatform")

    // Jetpack Compose plugin for declarative UI development
    id("org.jetbrains.compose")

    // Compose Compiler plugin for Compose-specific Kotlin compilation
    id("org.jetbrains.kotlin.plugin.compose")

    // Dokka plugin for comprehensive API documentation generation
    id("org.jetbrains.dokka")
}

/*
 * ========================================
 * KOTLIN MULTIPLATFORM CONFIGURATION
 * ========================================
 *
 * This section configures the Kotlin Multiplatform setup for the karl-compose-ui module,
 * defining target platforms, compiler options, and dependency management for each platform.
 *
 * **Platform Strategy:**
 * Currently targeting JVM for desktop applications with future expansion planned for
 * Android and Web platforms. The architecture supports seamless addition of new targets
 * without breaking existing integrations.
 *
 * **Compiler Configuration:**
 * JVM target is set to Java 21 to leverage modern Java features while maintaining
 * compatibility with enterprise development environments and ensuring optimal
 * performance for KARL container operations.
 */

kotlin {
    /*
     * JVM Target Configuration
     *
     * Configures JVM compilation for desktop applications using Compose Desktop.
     * Java 21 target provides access to modern language features and performance
     * optimizations while maintaining enterprise compatibility.
     *
     * **Performance Considerations:**
     * - Virtual threads support for improved concurrency in KARL processing
     * - Enhanced garbage collection for better UI responsiveness
     * - Pattern matching for cleaner KARL state management code
     * - Record classes for immutable state representation
     */
    jvm {
        compilerOptions {
            // Target Java 21 for modern language features and performance optimizations
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    /*
     * ========================================
     * SOURCE SET DEPENDENCY CONFIGURATION
     * ========================================
     *
     * Define dependencies for each source set, ensuring proper module integration
     * and comprehensive library support for Compose UI development.
     *
     * **Dependency Strategy:**
     * - Core KARL framework integration for container communication
     * - Complete Compose ecosystem for professional UI development
     * - Material Design components for consistent user experience
     * - Coroutines support for reactive state management
     */
    sourceSets {
        /*
         * Common Main Source Set
         *
         * Contains shared code that runs on all target platforms. This includes
         * the core Compose UI components, KARL integration utilities, and shared
         * business logic for UI state management.
         *
         * **Key Dependencies:**
         * - karl-core: Core KARL framework for container integration
         * - Compose Runtime: Fundamental reactive programming model
         * - Compose Foundation: Basic building blocks for custom components
         * - Material Design: Professional UI component library
         * - Kotlin Coroutines: Asynchronous programming and state management
         */
        val commonMain by getting {
            dependencies {
                // KARL Core framework integration for container communication
                implementation(project(":karl-core"))

                // Compose Runtime for reactive state management and composition
                implementation(compose.runtime)

                // Compose Foundation for basic UI building blocks and layouts
                implementation(compose.foundation)

                // Material Design 2 components for backward compatibility
                implementation(compose.material) // Add Material 2 dependency

                // Material Design 3 components for modern, accessible UI design
                implementation(compose.material3)

                // Core Compose UI primitives and drawing capabilities
                implementation(compose.ui)

                // UI tooling support for development and debugging
                implementation(compose.uiTooling)

                // Kotlin Coroutines for asynchronous operations and StateFlow integration
                implementation(libs.kotlinx.coroutines.core)
            }
        }

        /*
         * Common Test Source Set
         *
         * Shared testing infrastructure for all platforms. Includes unit tests
         * for UI components, integration tests for KARL container interaction,
         * and property-based testing for UI state management.
         *
         * **Testing Strategy:**
         * - Unit tests for individual Compose components
         * - Integration tests for KARL container communication
         * - Snapshot testing for UI regression prevention
         * - Performance tests for UI responsiveness under load
         */
        val commonTest by getting {
            dependencies {
                // Kotlin test framework for comprehensive testing capabilities
                implementation(kotlin("test"))
            }
        }

        /*
         * JVM Main Source Set
         *
         * Platform-specific code for JVM/Desktop applications. Contains desktop-specific
         * implementations, platform optimizations, and JVM-specific UI enhancements.
         *
         * **JVM-Specific Features:**
         * - Desktop-optimized UI components with keyboard navigation
         * - Window management and desktop integration utilities
         * - Performance optimizations for desktop hardware
         * - Platform-specific accessibility implementations
         *
         * **Dependencies:**
         * - Kotlin Standard Library JDK8 for enhanced collection operations
         * - Compose UI Tooling for desktop-specific development tools
         */
        val jvmMain by getting {
            dependencies {
                // Enhanced Kotlin standard library with JDK8 extensions for collections and streams
                implementation(libs.kotlin.stdlib.jdk8)

                // Advanced UI tooling support for desktop development and debugging
                implementation(compose.uiTooling)
            }
        }
    }
}

/*
 * ========================================
 * DOKKA DOCUMENTATION CONFIGURATION
 * ========================================
 *
 * Comprehensive configuration for generating professional API documentation
 * using Dokka. This setup ensures consistent, searchable, and navigable
 * documentation that integrates seamlessly with the overall KARL project
 * documentation ecosystem.
 *
 * **Documentation Strategy:**
 * - Professional API documentation with comprehensive examples
 * - Integration with external library documentation for better context
 * - Source code linking for enhanced developer experience
 * - Module-specific documentation with clear architectural guidance
 *
 * **Documentation Audience:**
 * - Library integrators implementing KARL UI components
 * - Framework contributors extending Compose functionality
 * - Enterprise developers seeking comprehensive API reference
 * - Open source contributors understanding component architecture
 *
 * **Quality Standards:**
 * - All public APIs must have comprehensive KDoc documentation
 * - Code examples must be production-ready and tested
 * - Integration patterns must be clearly documented with use cases
 * - Performance considerations must be documented for UI components
 */

// Dokka configuration for karl-compose-ui module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            /*
             * Module Identity Configuration
             *
             * Establishes clear module identity within the broader KARL documentation
             * ecosystem, ensuring proper categorization and navigation.
             */

            // Human-readable module name for documentation navigation
            moduleName.set("KARL Compose UI")

            // Version tracking for API stability and migration guidance
            moduleVersion.set(project.version.toString())

            /*
             * Module Documentation Integration
             *
             * Include the Module.md file which contains:
             * - Architectural overview and design principles
             * - Integration patterns and best practices
             * - Performance considerations and optimization tips
             * - Migration guides and version compatibility information
             */
            includes.from("Module.md")

            /*
             * Source Code Linking Configuration
             *
             * Provides direct links from API documentation to source code on GitHub,
             * enabling developers to quickly understand implementation details and
             * contribute improvements to the codebase.
             *
             * **Developer Benefits:**
             * - Immediate access to implementation details
             * - Enhanced understanding of component behavior
             * - Streamlined contribution workflow for open source development
             * - Better debugging capabilities through source inspection
             */
            sourceLink {
                // Local source directory for documentation generation
                localDirectory.set(projectDir.resolve("src"))

                // Remote GitHub URL for public source code access
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-compose-ui/src"))

                // Line number suffix for precise source location linking
                remoteLineSuffix.set("#L")
            }

            /*
             * External Documentation Integration
             *
             * Links to relevant external library documentation to provide comprehensive
             * context for developers working with KARL Compose UI components.
             *
             * **Integration Benefits:**
             * - Seamless navigation between KARL and dependency documentation
             * - Comprehensive understanding of underlying technology stack
             * - Reduced context switching during development
             * - Enhanced learning experience for developers new to the ecosystem
             */

            /*
             * Kotlin Coroutines Documentation Integration
             *
             * Essential for understanding StateFlow integration patterns,
             * asynchronous UI updates, and coroutine-based state management
             * used throughout the KARL Compose UI components.
             */
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            /*
             * Jetpack Compose Documentation Integration
             *
             * Critical reference for understanding Compose fundamentals,
             * component lifecycle, state management patterns, and UI best
             * practices that form the foundation of KARL UI components.
             */
            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/kotlin/androidx/compose/package-summary"))
            }
        }
    }
}
