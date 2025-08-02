/*
 * KARL Framework - Gradle Settings Configuration
 *
 * This settings file defines the fundamental project structure and dependency resolution
 * strategy for the KARL (Kotlin Adaptive Reasoning Learner) framework ecosystem.
 * As the cornerstone of the build system architecture, this configuration ensures
 * reliable, secure, and performant dependency management across all framework modules
 * while maintaining enterprise-grade security and stability standards.
 *
 * **Project Architecture Overview:**
 * The KARL framework employs a sophisticated multi-module architecture designed for
 * scalability, maintainability, and clear separation of concerns. Each module serves
 * a specific purpose within the broader privacy-first adaptive learning ecosystem,
 * enabling flexible integration patterns and targeted functionality adoption.
 *
 * **Build System Philosophy:**
 * This configuration embodies enterprise-grade build system practices:
 * - Centralized dependency resolution preventing version conflicts
 * - Secure repository management with explicit source prioritization
 * - Fail-fast configuration preventing repository pollution and security risks
 * - Strategic repository ordering optimizing build performance and reliability
 * - Professional module organization enabling clear architectural understanding
 *
 * **Security and Reliability Considerations:**
 * The repository configuration prioritizes security and build reproducibility:
 * - Google Maven repository for Android ecosystem dependencies
 * - Maven Central for stable, widely-adopted open source libraries
 * - JetBrains Compose repository for cutting-edge UI framework components
 * - Gradle Plugin Portal for official and community-verified build plugins
 * - Strict repository mode preventing accidental dependency source pollution
 *
 * **Framework Module Ecosystem:**
 * The multi-module structure enables targeted adoption and clear architectural boundaries:
 * - Foundational modules providing core abstractions and interfaces
 * - Implementation modules delivering concrete functionality and integrations
 * - UI modules enabling seamless user interface integration
 * - Example modules demonstrating real-world usage patterns and best practices
 * - Extension modules supporting specialized use cases and domain adaptations
 *
 * **Enterprise Integration Strategy:**
 * This configuration supports diverse enterprise deployment scenarios:
 * - Modular adoption enabling incremental framework integration
 * - Clear dependency boundaries facilitating security auditing and compliance
 * - Consistent build behavior across development, staging, and production environments
 * - Professional documentation and example patterns accelerating enterprise adoption
 * - Scalable architecture supporting from pilot projects to enterprise-wide deployment
 *
 * @project karl-framework
 * @since 1.0.0
 * @author KARL Development Team
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Repository</a>
 */

/*
 * ========================================
 * PLUGIN MANAGEMENT CONFIGURATION
 * ========================================
 *
 * Centralized plugin repository management ensuring secure, reliable access to
 * build plugins while optimizing plugin resolution performance and maintaining
 * enterprise-grade security standards.
 *
 * **Repository Strategy:**
 * The plugin repository configuration follows a strategic hierarchy prioritizing
 * security, performance, and reliability. This approach ensures that build plugins
 * are sourced from trusted repositories while optimizing resolution speed and
 * maintaining build reproducibility across different environments.
 *
 * **Security Benefits:**
 * - Explicit repository listing prevents unknown source pollution
 * - Trusted repository prioritization reduces security attack surface
 * - Official plugin portal access ensures verified and audited plugins
 * - Google repository integration supports Android ecosystem compatibility
 */

pluginManagement {
    repositories {
        /*
         * Official Gradle Plugin Portal
         *
         * Primary source for official Gradle plugins and community-verified
         * plugins. This repository provides the most secure and stable access
         * to essential build system plugins with comprehensive vetting and
         * security scanning.
         *
         * **Plugin Categories:**
         * - Core Gradle plugins for fundamental build system functionality
         * - Official JetBrains plugins for Kotlin and IDE integration
         * - Community plugins with verified security and functionality
         * - Code quality and analysis plugins for enterprise standards
         */
        gradlePluginPortal()

        /*
         * Google Maven Repository
         *
         * Essential repository for Android ecosystem plugins and Google-developed
         * tools. Critical for Android development support and Google services
         * integration, providing access to official Android build tools and
         * development utilities.
         *
         * **Plugin Categories:**
         * - Android Gradle Plugin and related development tools
         * - Google Play services and Firebase integration plugins
         * - Android testing and deployment automation tools
         * - Google Cloud and enterprise service integration plugins
         */
        google()

        /*
         * Maven Central Repository
         *
         * The world's largest repository of open source libraries and plugins.
         * Provides access to stable, widely-adopted plugins with comprehensive
         * community support and extensive documentation.
         *
         * **Plugin Categories:**
         * - Open source build and automation plugins
         * - Code quality and analysis tools
         * - Documentation generation and publishing plugins
         * - Testing and deployment automation utilities
         */
        mavenCentral()

        /*
         * JetBrains Compose Development Repository
         *
         * Specialized repository providing access to cutting-edge Jetpack Compose
         * development tools and plugins. Essential for modern declarative UI
         * development with the latest Compose features and optimizations.
         *
         * **Plugin Categories:**
         * - Jetpack Compose compiler and development plugins
         * - Compose Desktop and multiplatform development tools
         * - UI testing and debugging utilities for Compose applications
         * - Performance profiling and optimization tools for Compose UIs
         */
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

/*
 * ========================================
 * DEPENDENCY RESOLUTION MANAGEMENT
 * ========================================
 *
 * Enterprise-grade dependency resolution configuration implementing strict
 * security policies and performance optimizations for reliable, reproducible
 * builds across all development and deployment environments.
 *
 * **Dependency Resolution Philosophy:**
 * The KARL framework employs a centralized dependency resolution strategy that
 * prioritizes security, performance, and build reproducibility. This approach
 * prevents dependency conflicts, ensures consistent library versions across
 * modules, and maintains enterprise-grade security standards.
 *
 * **Security and Reliability Benefits:**
 * - Centralized repository management preventing dependency source pollution
 * - Fail-fast configuration immediately detecting repository configuration issues
 * - Explicit repository ordering optimizing resolution performance
 * - Consistent dependency resolution across all development environments
 * - Enhanced security through controlled dependency source management
 */

dependencyResolutionManagement {
    /*
     * Strict Repository Mode Configuration
     *
     * Enforces centralized repository management by preventing individual projects
     * from declaring their own repositories. This security-focused approach ensures
     * all dependencies are sourced from approved, trusted repositories while
     * preventing accidental inclusion of untrusted or malicious dependency sources.
     *
     * **Security Benefits:**
     * - Prevents accidental dependency source pollution in subprojects
     * - Ensures all dependencies are sourced from approved repositories
     * - Eliminates potential for supply chain attacks through rogue repositories
     * - Maintains consistent dependency resolution behavior across all modules
     * - Enables comprehensive security auditing of dependency sources
     */
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        /*
         * Google Maven Repository
         *
         * Primary repository for Android ecosystem libraries and Google-developed
         * components. Essential for Android development support, Google services
         * integration, and access to Google's comprehensive library ecosystem.
         *
         * **Library Categories:**
         * - Android SDK libraries and support components
         * - AndroidX libraries for modern Android development
         * - Google Play services and Firebase SDK components
         * - Material Design components and UI libraries
         * - Android architecture components and lifecycle management
         * - Testing libraries and development utilities
         */
        google()

        /*
         * Maven Central Repository
         *
         * The world's largest repository of open source Java and Kotlin libraries.
         * Serves as the primary source for stable, production-ready dependencies
         * with comprehensive community support and extensive documentation.
         *
         * **Library Categories:**
         * - Core Kotlin and Java standard libraries
         * - Popular open source frameworks and utilities
         * - Database drivers and persistence frameworks
         * - Networking and HTTP client libraries
         * - JSON parsing and serialization libraries
         * - Testing frameworks and assertion libraries
         * - Logging and monitoring utilities
         */
        mavenCentral()

        /*
         * JetBrains Compose Development Repository
         *
         * Specialized repository providing access to cutting-edge Jetpack Compose
         * libraries and development tools. Critical for modern declarative UI
         * development with the latest Compose features and performance optimizations.
         *
         * **Library Categories:**
         * - Jetpack Compose runtime and foundation libraries
         * - Compose Desktop and multiplatform UI components
         * - Material Design 3 components for Compose
         * - Animation and graphics libraries for Compose
         * - Navigation and lifecycle components for Compose applications
         * - Testing utilities and debugging tools for Compose UIs
         */
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

/*
 * ========================================
 * PROJECT STRUCTURE CONFIGURATION
 * ========================================
 *
 * Comprehensive multi-module project structure defining the complete KARL framework
 * ecosystem architecture. This configuration establishes clear module boundaries,
 * dependency relationships, and architectural patterns that enable scalable,
 * maintainable, and enterprise-ready adaptive learning system development.
 *
 * **Architectural Design Principles:**
 * The KARL framework module structure embodies sophisticated software architecture
 * principles designed for long-term maintainability and enterprise adoption:
 * - Clear separation of concerns with well-defined module responsibilities
 * - Layered architecture enabling flexible integration and testing strategies
 * - Dependency inversion principles supporting extensibility and customization
 * - Privacy-first design with local-only processing capabilities
 * - Enterprise-grade scalability from proof-of-concept to production deployment
 *
 * **Module Dependency Architecture:**
 * The module structure follows a carefully designed dependency hierarchy:
 * - Foundation modules (karl-core) providing essential interfaces and abstractions
 * - Implementation modules (karl-kldl, karl-room) delivering concrete functionality
 * - Integration modules (karl-compose-ui) enabling seamless UI framework integration
 * - Example modules (karl-example-desktop) demonstrating real-world usage patterns
 * - Future extension modules supporting specialized use cases and integrations
 */

/*
 * Root Project Identity
 *
 * Establishes the canonical name for the KARL framework project, serving as
 * the foundation for all module naming conventions and project identification
 * across development, documentation, and deployment contexts.
 *
 * **Naming Convention:**
 * The "project-karl" name reflects the framework's comprehensive nature as
 * a complete project ecosystem rather than a single library, emphasizing
 * its role as a foundation for building adaptive learning applications.
 */
rootProject.name = "project-karl"

/*
 * Multi-Module Framework Architecture
 *
 * Comprehensive module inclusion defining the complete KARL framework ecosystem.
 * Each module serves a specific architectural role while maintaining clear
 * boundaries and well-defined interfaces for maximum flexibility and maintainability.
 *
 * **Module Architecture Overview:**
 * The framework follows a layered architecture pattern with clear dependency
 * flows and separation of concerns, enabling targeted adoption and integration
 * flexibility for diverse enterprise requirements and use cases.
 */
include(
    /*
     * KARL Core Framework Module
     *
     * Foundational module providing essential interfaces, abstractions, and
     * orchestration capabilities for the entire KARL ecosystem. This module
     * defines the architectural contracts and base implementations that all
     * other modules depend upon.
     *
     * **Core Responsibilities:**
     * - Fundamental learning engine interfaces and base implementations
     * - Container orchestration system for component lifecycle management
     * - Privacy-first data processing abstractions and utilities
     * - Cross-platform compatibility foundations and platform abstractions
     * - Extension point definitions for domain-specific implementations
     * - State management utilities built on Kotlin coroutines and reactive patterns
     *
     * **Architectural Role:**
     * Serves as the dependency root for all other framework modules, providing
     * stable APIs and architectural foundations that enable consistent
     * implementation patterns across the entire ecosystem.
     */
    ":karl-core",
    /*
     * KARL KotlinDL Machine Learning Engine Module
     *
     * Sophisticated machine learning implementation module providing concrete
     * learning capabilities using KotlinDL and TensorFlow Lite while maintaining
     * KARL's privacy-first architecture principles.
     *
     * **Machine Learning Capabilities:**
     * - Deep learning model training and inference using KotlinDL
     * - TensorFlow Lite integration for optimized mobile and edge deployment
     * - GPU acceleration support for enhanced performance on compatible hardware
     * - Privacy-preserving learning algorithms with local-only data processing
     * - Real-time model adaptation and incremental learning capabilities
     * - Comprehensive dataset management and preprocessing pipelines
     *
     * **Architectural Role:**
     * Provides concrete implementation of KARL core learning interfaces,
     * demonstrating how to integrate sophisticated ML capabilities while
     * maintaining privacy guarantees and enterprise-grade performance.
     */
    ":karl-kldl",
    /*
     * KARL Room Data Persistence Module
     *
     * Privacy-first data persistence layer implementing KARL storage abstractions
     * using Android Room and SQLite for secure, local-only data management
     * with enterprise-grade reliability and performance.
     *
     * **Data Persistence Capabilities:**
     * - Type-safe database operations with compile-time query verification
     * - Automatic database schema migration and version management
     * - Privacy-preserving data encryption and secure local storage
     * - Efficient data access patterns optimized for machine learning workloads
     * - Real-time data synchronization with KARL learning engines
     * - Comprehensive audit logging for data access and modification tracking
     *
     * **Architectural Role:**
     * Implements KARL core data storage interfaces, providing production-ready
     * persistence capabilities that maintain privacy guarantees while delivering
     * enterprise-grade performance and reliability standards.
     */
    ":karl-room",
    /*
     * KARL Compose UI Integration Module
     *
     * Reactive user interface components built on Jetpack Compose for seamless
     * integration of KARL adaptive learning capabilities into modern declarative
     * user interfaces with Material Design compliance.
     *
     * **UI Integration Capabilities:**
     * - Reactive state management components for KARL container integration
     * - Professional UI components for learning progress visualization
     * - Interactive controls for demonstration and testing of KARL functionality
     * - Material Design compliance with both Material 2 and Material 3 support
     * - Cross-platform support for Android and desktop applications
     * - Accessibility-conscious design following Material Design guidelines
     *
     * **Architectural Role:**
     * Bridges the gap between KARL framework capabilities and modern UI development,
     * providing production-ready components that demonstrate best practices for
     * integrating adaptive learning systems into user-facing applications.
     */
    ":karl-compose-ui",
    /*
     * KARL Desktop Example Application Module
     *
     * Comprehensive desktop application demonstrating complete KARL framework
     * integration patterns, real-world usage scenarios, and best practices for
     * building privacy-first adaptive learning applications.
     *
     * **Example Application Features:**
     * - Complete KARL framework integration from initialization to runtime
     * - Reactive UI components with real-time learning progress visualization
     * - Local SQLite database integration for persistent learning state
     * - Professional desktop application packaging and distribution
     * - Cross-platform deployment with native installers
     * - Performance-optimized UI with smooth animations and responsive interactions
     *
     * **Architectural Role:**
     * Serves as the definitive reference implementation for KARL framework
     * integration, providing developers with comprehensive examples of production-ready
     * patterns and demonstrating the framework's capabilities in real-world scenarios.
     */
    ":karl-example-desktop",
)
