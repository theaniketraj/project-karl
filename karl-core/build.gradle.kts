/*
 * KARL Core Module - Build Configuration
 *
 * This build script configures the KARL Core module, which serves as the foundational
 * framework for KARL (Kotlin Adaptive Reasoning Learner) - a privacy-first, local-only
 * machine learning and adaptive reasoning system built entirely in Kotlin.
 *
 * **Framework Architecture and Design Philosophy:**
 * KARL Core provides the essential abstractions, interfaces, and base implementations
 * that define the framework's architecture. This module embodies the core principles
 * of privacy-first design, local-only processing, and adaptive learning capabilities
 * without requiring external cloud services or data transmission.
 *
 * **Core Responsibilities:**
 * - Fundamental interfaces for learning engines, data storage, and data sources
 * - Container orchestration system for coordinating framework components
 * - State management abstractions for reactive programming patterns
 * - Thread-safe operations and lifecycle management utilities
 * - Privacy-preserving data processing primitives
 * - Extensible plugin architecture for domain-specific implementations
 *
 * **Key Design Principles:**
 * - **Privacy by Design**: All data processing occurs locally without external transmission
 * - **Adaptive Learning**: Framework adapts to usage patterns and user preferences
 * - **Reactive Architecture**: Built on Kotlin coroutines and StateFlow for responsive UIs
 * - **Modular Extensibility**: Clean interfaces enable domain-specific implementations
 * - **Enterprise Readiness**: Thread-safe, performance-optimized, and production-ready
 * - **Cross-Platform Compatibility**: Supports JVM, Android, and future platform expansion
 *
 * **Integration Ecosystem:**
 * This core module is designed to be extended by:
 * - karl-compose-ui: Jetpack Compose UI integration components
 * - karl-room: Android Room database implementation for data persistence
 * - karl-kldl: KARL Learning Definition Language for declarative learning configurations
 * - Domain-specific implementations for various machine learning use cases
 *
 * **Target Applications:**
 * - Intelligent user interfaces with adaptive behavior
 * - Privacy-conscious recommendation systems
 * - Local-only personal assistants and automation
 * - Educational applications with personalized learning paths
 * - Enterprise applications requiring data privacy compliance
 *
 * @module karl-core
 * @since 1.0.0
 * @author KARL Development Team
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Documentation</a>
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URL

/*
 * ========================================
 * GRADLE PLUGIN CONFIGURATION
 * ========================================
 *
 * Essential plugins for building a robust, cross-platform Kotlin library
 * with comprehensive documentation and testing capabilities.
 */

plugins {
    // Kotlin Multiplatform for cross-platform framework development
    alias(libs.plugins.kotlinMultiplatform)

    // Dokka for generating comprehensive API documentation
    id("org.jetbrains.dokka")
}

/*
 * ========================================
 * KOTLIN MULTIPLATFORM CONFIGURATION
 * ========================================
 *
 * Configures the core KARL framework for cross-platform compatibility while
 * maintaining optimal performance and modern language feature support.
 *
 * **Platform Strategy:**
 * The core module targets JVM primarily with architecture designed for future
 * expansion to Android, Native, and Web platforms. This enables KARL to run
 * on desktop applications, server environments, Android devices, and potentially
 * in browser environments for web-based adaptive interfaces.
 *
 * **Performance Considerations:**
 * - Java 21 target leverages virtual threads for enhanced concurrency
 * - Modern JVM optimizations for machine learning workloads
 * - Memory-efficient data structures for local processing
 * - GC-friendly design patterns for responsive user interfaces
 */

kotlin {
    /*
     * JVM Target Configuration
     *
     * Configures JVM compilation with Java interoperability and modern language features.
     * The Java 21 target provides access to cutting-edge JVM capabilities while ensuring
     * compatibility with enterprise development environments.
     *
     * **Java 21 Benefits for KARL Framework:**
     * - Virtual threads for improved concurrency in learning algorithms
     * - Pattern matching for cleaner state management code
     * - Record classes for immutable data transfer objects
     * - Enhanced garbage collection for better UI responsiveness
     * - Foreign Function & Memory API for potential native optimizations
     * - Structured concurrency for safer async operations
     *
     * **Java Interoperability:**
     * withJava() enables seamless integration with existing Java libraries
     * and enterprise systems, crucial for adoption in mixed-language environments.
     */
    jvm {
        // Enable Java interoperability for enterprise integration scenarios
        withJava()

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    // Target Java 21 for modern language features and performance optimizations
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }
        }
    }

    /*
     * ========================================
     * SOURCE SET DEPENDENCY CONFIGURATION
     * ========================================
     *
     * Carefully curated dependencies that provide essential functionality while
     * maintaining the framework's core principle of minimal external dependencies
     * and maximum privacy preservation.
     *
     * **Dependency Philosophy:**
     * - Minimal external dependencies to reduce security surface area
     * - Standard library focus for maximum compatibility and stability
     * - Coroutines as the primary concurrency mechanism for reactive programming
     * - No external networking libraries to ensure local-only processing
     */
    sourceSets {
        /*
         * Common Main Source Set
         *
         * Contains the core KARL framework implementation that runs across all
         * target platforms. This includes fundamental interfaces, base classes,
         * and shared business logic for adaptive learning and reasoning.
         *
         * **Core Components:**
         * - Learning engine interfaces and base implementations
         * - Data storage abstractions for privacy-preserving persistence
         * - Container orchestration system for component lifecycle management
         * - State management utilities built on Kotlin coroutines
         * - Privacy-first data processing utilities
         * - Extensible plugin architecture for domain-specific functionality
         *
         * **Key Dependencies:**
         * - Kotlin Standard Library: Essential language features and collections
         * - Kotlin Coroutines: Reactive programming and asynchronous operations
         */
        val commonMain by getting {
            dependencies {
                // Kotlin standard library for essential language features and collections
                implementation(libs.kotlin.stdlib.common)

                // Kotlin Coroutines API for reactive programming and state management
                // This is exposed as API to allow dependent modules to work with coroutines
                api(libs.kotlinx.coroutines.core)
            }
        }

        /*
         * Common Test Source Set
         *
         * Comprehensive testing infrastructure for the KARL core framework.
         * Includes unit tests, integration tests, property-based testing, and
         * performance benchmarks to ensure framework reliability and correctness.
         *
         * **Testing Strategy:**
         * - Unit tests for individual components and algorithms
         * - Integration tests for component interaction and lifecycle management
         * - Property-based testing for learning algorithm correctness
         * - Performance tests for memory usage and processing efficiency
         * - Thread safety tests for concurrent access patterns
         * - Privacy compliance tests for data handling verification
         */
        val commonTest by getting {
            dependencies {
                // Kotlin test framework for comprehensive testing capabilities
                implementation(libs.kotlin.test)
            }
        }

        /*
         * JVM Main Source Set
         *
         * Platform-specific implementations and optimizations for JVM environments.
         * Contains JVM-specific performance optimizations, enterprise integration
         * utilities, and platform-specific implementations of core interfaces.
         *
         * **JVM-Specific Features:**
         * - File system integration for local data persistence
         * - JVM-optimized data structures and algorithms
         * - Enterprise integration utilities (logging, monitoring, configuration)
         * - Performance profiling and debugging utilities
         * - Native library integration capabilities for advanced computations
         */
        val jvmMain by getting {
            dependencies {
                // Enhanced Kotlin standard library with JDK8+ extensions
                // Provides advanced collection operations, stream processing, and IO utilities
                implementation(libs.kotlin.stdlib.jdk8)
            }
        }
    }
}

/*
 * ========================================
 * DOKKA DOCUMENTATION CONFIGURATION
 * ========================================
 *
 * Comprehensive API documentation configuration for the KARL Core module.
 * This setup generates professional, searchable documentation that serves as
 * the primary reference for framework integrators and contributors.
 *
 * **Documentation Philosophy:**
 * The KARL Core documentation serves multiple audiences and purposes:
 * - **Framework Integrators**: Detailed API reference with integration examples
 * - **Enterprise Architects**: Architectural guidance and design pattern documentation
 * - **Open Source Contributors**: Comprehensive codebase understanding and contribution guidelines
 * - **Security Auditors**: Privacy and security implementation transparency
 *
 * **Quality Standards:**
 * - All public APIs must have comprehensive KDoc documentation
 * - Code examples must demonstrate real-world usage patterns
 * - Architecture decisions must be documented with rationale
 * - Performance characteristics must be clearly specified
 * - Thread safety guarantees must be explicitly documented
 * - Privacy implications must be clearly stated for all components
 *
 * **Documentation Integration:**
 * Links to external library documentation provide comprehensive context
 * for developers working with the KARL framework ecosystem.
 */

// Dokka configuration for karl-core module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            /*
             * Module Identity and Versioning
             *
             * Establishes clear module identity within the KARL framework ecosystem
             * and provides version tracking for API stability and migration guidance.
             */

            // Human-readable module name for documentation navigation
            moduleName.set("KARL Core")

            // Version tracking for API stability and backward compatibility guidance
            moduleVersion.set(project.version.toString())

            /*
             * Module Documentation Integration
             *
             * Includes the Module.md file containing:
             * - Comprehensive architectural overview and design principles
             * - Integration patterns and best practices for framework adoption
             * - Performance characteristics and optimization guidelines
             * - Privacy and security implementation details
             * - Migration guides and version compatibility matrix
             * - Extensibility patterns for domain-specific implementations
             */
            includes.from("Module.md")

            /*
             * Source Code Linking Configuration
             *
             * Provides seamless integration between API documentation and source code,
             * enabling developers to quickly understand implementation details and
             * contribute to the framework development.
             *
             * **Benefits for Developers:**
             * - Immediate access to implementation details for better understanding
             * - Enhanced debugging capabilities through source inspection
             * - Streamlined contribution workflow for open source development
             * - Better architectural comprehension through code exploration
             * - Real-time verification of documentation accuracy against implementation
             */
            sourceLink {
                // Local source directory for documentation build process
                localDirectory.set(projectDir.resolve("src"))

                // Remote GitHub repository URL for public source code access
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-core/src"))

                // Line number suffix for precise source location linking
                remoteLineSuffix.set("#L")
            }

            /*
             * External Documentation Integration
             *
             * Strategic linking to external library documentation provides comprehensive
             * context for developers working with KARL framework components.
             *
             * **Integration Benefits:**
             * - Seamless navigation between KARL and dependency documentation
             * - Comprehensive understanding of underlying technology stack
             * - Reduced context switching during development workflows
             * - Enhanced learning experience for developers new to the ecosystem
             * - Better debugging and troubleshooting capabilities
             */

            // Bypass: A block comment may not be preceded by a block comment.

            /*
             * Kotlin Coroutines Documentation Integration
             *
             * Critical for understanding KARL's reactive architecture, StateFlow integration,
             * asynchronous learning operations, and thread-safe state management patterns
             * that form the foundation of the framework's responsive design.
             */
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            /*
             * Internal Package Suppression
             *
             * Hides internal implementation details from public API documentation
             * while maintaining clean separation between public interfaces and
             * private implementation details.
             *
             * **Privacy and Security Benefits:**
             * - Prevents accidental exposure of internal implementation details
             * - Maintains clear API boundaries for stable public interfaces
             * - Reduces documentation complexity for framework users
             * - Enables internal refactoring without affecting public documentation
             * - Supports security through obscurity for sensitive internal mechanisms
             */
            perPackageOption {
                // Suppress documentation for all internal packages
                matchingRegex.set(".*\\.internal.*")
                suppress.set(true)
            }
        }
    }
}
