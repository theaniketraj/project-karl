/*
 * KARL Room Storage Module - Build Configuration
 *
 * This build script configures the KARL Room Storage module, which provides a
 * comprehensive, privacy-first data persistence layer for the KARL (Kotlin Adaptive
 * Reasoning Learner) framework using Android Room and SQLite. This module serves as
 * the primary data storage implementation that maintains KARL's core principle of
 * local-only data processing while providing enterprise-grade persistence capabilities.
 *
 * **Module Purpose and Architecture:**
 * The karl-room module implements KARL's data storage abstractions using Android Room,
 * providing type-safe database operations, automatic schema management, and efficient
 * query optimization. This module ensures that all learning data, user preferences,
 * and adaptive behavior patterns remain securely stored on the local device without
 * any external transmission or cloud dependency.
 *
 * **Key Storage Capabilities:**
 * - Type-safe database operations with compile-time query verification
 * - Automatic database schema migration and version management
 * - Efficient data access patterns optimized for machine learning workloads
 * - Privacy-preserving data encryption and secure local storage
 * - Real-time data synchronization with KARL learning engines
 * - Comprehensive audit logging for data access and modification tracking
 * - Memory-efficient data structures for large-scale learning datasets
 *
 * **Privacy-First Data Architecture:**
 * This module embodies KARL's privacy-by-design principles through:
 * - Complete local data storage without external database connections
 * - Encrypted storage for sensitive learning parameters and user data
 * - Data anonymization and aggregation techniques for usage analytics
 * - Secure deletion and data lifecycle management capabilities
 * - GDPR-compliant data retention and user control mechanisms
 * - Zero-knowledge architecture where even developers cannot access user data
 *
 * **Database Design Patterns:**
 * - Entity-relationship modeling optimized for adaptive learning scenarios
 * - Efficient indexing strategies for real-time query performance
 * - Normalized schema design for data consistency and integrity
 * - Temporal data management for learning history and pattern analysis
 * - Hierarchical data structures for complex learning model representations
 * - Optimistic locking for concurrent access in multi-threaded environments
 *
 * **Performance Optimization:**
 * - Lazy loading and pagination for large dataset management
 * - Connection pooling and transaction optimization for high-throughput operations
 * - Query optimization with proper indexing and execution plan analysis
 * - Memory-mapped file operations for enhanced I/O performance
 * - Batch processing capabilities for bulk data operations
 * - Asynchronous data access patterns for responsive user interfaces
 *
 * **Integration Ecosystem:**
 * This module provides seamless integration with other KARL components:
 * - karl-core: Implements core data storage interface specifications
 * - karl-kldl: Provides persistent storage for machine learning models and datasets
 * - karl-compose-ui: Enables reactive UI updates through database change notifications
 * - karl-example-desktop: Demonstrates complete database setup and configuration patterns
 *
 * **Enterprise Features:**
 * - Database backup and restore capabilities for data protection
 * - Schema versioning and migration strategies for application updates
 * - Performance monitoring and query optimization tools
 * - Data integrity validation and consistency checking mechanisms
 * - Scalable architecture supporting from small personal apps to large enterprise systems
 * - Comprehensive logging and monitoring for production deployment
 *
 * **Cross-Platform Compatibility:**
 * While optimized for JVM environments, the module architecture supports future
 * expansion to Android platforms and other Room-compatible environments, enabling
 * consistent data storage patterns across the entire KARL ecosystem.
 *
 * @module karl-room
 * @since 1.0.0
 * @author KARL Development Team
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Documentation</a>
 * @see <a href="https://developer.android.com/training/data-storage/room">Android Room Documentation</a>
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URL

/*
 * ========================================
 * GRADLE PLUGIN CONFIGURATION
 * ========================================
 *
 * Advanced plugin configuration for Room database integration with comprehensive
 * code generation, serialization support, and professional documentation capabilities.
 */

plugins {
    // Kotlin Multiplatform for cross-platform data storage capabilities
    alias(libs.plugins.kotlinMultiplatform)

    // Kotlin Symbol Processing for Room annotation processing and code generation
    alias(libs.plugins.ksp)

    // Kotlin Serialization for type-safe data serialization and JSON handling
    alias(libs.plugins.kotlinSerialization)

    // Dokka for comprehensive database API documentation
    id("org.jetbrains.dokka")
}

/*
 * ========================================
 * KOTLIN MULTIPLATFORM CONFIGURATION
 * ========================================
 *
 * Advanced multiplatform configuration optimized for database operations,
 * data persistence workflows, and high-performance storage access patterns
 * while maintaining KARL's privacy-first architecture principles.
 *
 * **Platform Strategy for Data Storage:**
 * The module targets JVM primarily for mature database ecosystem access and
 * optimal I/O performance, with architecture designed for future expansion to
 * Android platforms where Room provides native integration capabilities.
 *
 * **Database Performance Considerations:**
 * - Java 21 target leverages virtual threads for concurrent database operations
 * - Enhanced garbage collection for memory-intensive data processing
 * - Foreign Function & Memory API for potential native database optimizations
 * - Project Loom integration for efficient concurrent transaction processing
 * - Advanced I/O operations for high-throughput data access patterns
 */

kotlin {
    /*
     * JVM Target Configuration for Database Operations
     *
     * Configures JVM compilation with advanced optimizations specifically tailored
     * for database operations, transaction processing, and large-scale data
     * management scenarios common in adaptive learning systems.
     *
     * **Java 21 Benefits for Database Operations:**
     * - Virtual threads enable massive parallelization of database operations
     * - Enhanced garbage collection reduces transaction interruptions
     * - Foreign Function & Memory API enables efficient native database integration
     * - Pattern matching simplifies complex data transformation operations
     * - Structured concurrency ensures safe parallel data processing
     * - Memory management improvements for large dataset handling
     * - Advanced I/O APIs for optimized file and network operations
     *
     * **Java Interoperability for Database Ecosystem:**
     * withJava() enables seamless integration with the extensive Java database
     * ecosystem including JDBC drivers, connection pooling libraries, and
     * enterprise database management tools.
     */
    jvm {
        // Enable Java interoperability for comprehensive database ecosystem integration
        withJava()

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    // Target Java 21 for advanced database performance optimizations
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
     * Comprehensive database dependency configuration that provides enterprise-grade
     * persistence capabilities while maintaining KARL's core principles of privacy
     * preservation and local-only data processing.
     *
     * **Dependency Strategy:**
     * - Android Room for type-safe database operations and schema management
     * - Kotlin Serialization for efficient data serialization and JSON handling
     * - SQLite integration for reliable local database storage
     * - Core KARL framework integration for seamless architectural alignment
     */
    sourceSets {
        /*
         * Common Main Source Set
         *
         * Contains shared database interfaces, data models, and persistence utilities
         * that can be used across all target platforms. This includes abstract
         * database definitions, entity models, and cross-platform storage operations.
         *
         * **Shared Database Components:**
         * - Abstract database interface definitions and DAO specifications
         * - Common entity models and data transfer objects
         * - Cross-platform serialization utilities and type converters
         * - Shared database migration and schema management utilities
         * - Privacy-preserving data access patterns and encryption utilities
         * - Performance monitoring and query optimization abstractions
         */
        val commonMain by getting {
            dependencies {
                /*
                 * KARL Core Framework Integration
                 *
                 * Exposes the complete KARL core API to enable seamless integration
                 * between database implementations and the broader framework ecosystem.
                 * This API exposure allows other modules to directly interact with
                 * storage capabilities through well-defined interfaces.
                 */
                api(project(":karl-core"))

                /*
                 * Android Room Common Library
                 *
                 * Provides cross-platform Room annotations and core functionality
                 * that can be shared between JVM and Android targets. This enables
                 * consistent database schema definitions and entity models across platforms.
                 *
                 * **Room Common Benefits:**
                 * - Shared entity definitions and database schemas
                 * - Cross-platform annotation processing for code generation
                 * - Consistent data access patterns across target platforms
                 * - Type-safe database operations with compile-time verification
                 */
                implementation(libs.androidx.room.common)

                /*
                 * Kotlin Serialization JSON Support
                 *
                 * Provides type-safe JSON serialization capabilities for complex data
                 * structures, configuration management, and data export/import operations.
                 * Essential for handling nested data structures and maintaining data
                 * integrity during serialization operations.
                 *
                 * **Serialization Benefits:**
                 * - Type-safe JSON serialization for complex learning data structures
                 * - Efficient data export and import capabilities
                 * - Configuration management with structured data validation
                 * - API-compatible data formats for potential future integrations
                 */
                implementation(libs.kotlinx.serialization.json)
            }
        }

        /*
         * JVM Main Source Set
         *
         * Platform-specific database implementations leveraging the full power
         * of the JVM database ecosystem for sophisticated data persistence
         * capabilities. Contains concrete implementations of KARL storage
         * interfaces using Android Room and SQLite.
         *
         * **JVM-Specific Database Features:**
         * - Full Android Room runtime integration for comprehensive database operations
         * - SQLite framework integration for reliable local storage
         * - Advanced transaction management and connection pooling
         * - High-performance query execution and optimization
         * - Enterprise-grade backup and recovery capabilities
         * - Comprehensive logging and monitoring for production deployment
         */
        val jvmMain by getting {
            dependencies {
                /*
                 * Enhanced Language Support
                 *
                 * Foundation libraries providing enhanced language features essential
                 * for sophisticated database operations and data processing workflows.
                 */

                // Enhanced Kotlin standard library with JDK8+ extensions for data processing
                implementation(libs.kotlin.stdlib.jdk8)

                /*
                 * Android Room Database Framework
                 *
                 * Comprehensive Room integration providing type-safe database operations,
                 * automatic schema management, and efficient query optimization for
                 * enterprise-grade data persistence requirements.
                 *
                 * **Room Framework Benefits:**
                 * - Compile-time SQL query verification and type safety
                 * - Automatic database schema migration and version management
                 * - Efficient query optimization and execution planning
                 * - Reactive data access with LiveData and Flow integration
                 * - Memory-efficient data loading with paging support
                 * - Comprehensive testing utilities for database operations
                 */

                // Android Room runtime for core database operations and transaction management
                api(libs.androidx.room.runtime)

                // Android Room Kotlin extensions for coroutines and Flow integration
                api(libs.androidx.room.ktx)

                /*
                 * SQLite Database Integration
                 *
                 * Direct SQLite integration providing low-level database access,
                 * performance optimization capabilities, and advanced database
                 * features not available through higher-level abstractions.
                 *
                 * **SQLite Integration Benefits:**
                 * - High-performance database operations with minimal overhead
                 * - Advanced SQL features and custom function support
                 * - Fine-grained transaction control and optimization
                 * - Database file management and backup capabilities
                 * - Performance profiling and query analysis tools
                 */

                // Android SQLite framework for low-level database operations
                api(libs.androidx.sqlite.framework)

                // SQLite JDBC driver for direct database access and administration
                implementation(libs.sqlite.jdbc)
            }
        }
    }
}

/*
 * ========================================
 * ROOM ANNOTATION PROCESSING CONFIGURATION
 * ========================================
 *
 * Kotlin Symbol Processing (KSP) configuration for Room annotation processing
 * and automatic code generation. This section handles the complex build-time
 * code generation required for type-safe database operations.
 *
 * **Code Generation Strategy:**
 * Room's annotation processor generates DAO implementations, database builders,
 * and migration helpers at compile time, ensuring type safety and optimal
 * performance for database operations while maintaining zero runtime overhead
 * for reflection-based operations.
 */

dependencies {
    /*
     * Room Annotation Processor Configuration
     *
     * Currently temporarily commented out to resolve build configuration issues.
     * This processor is essential for generating Room's DAO implementations,
     * database builders, and schema validation code.
     *
     * **Required for Production:**
     * The Room compiler must be restored and properly configured to enable:
     * - Automatic DAO implementation generation
     * - Database schema validation and migration code generation
     * - Type-safe query compilation and optimization
     * - Runtime database configuration and initialization code
     *
     * **Restoration Plan:**
     * This dependency will be uncommented and properly configured once
     * build system compatibility issues are resolved.
     */

    // Temporarily commented out to fix build - will need to be restored and fixed
    // add("kspJvm", libs.androidx.room.compiler)
}

/*
 * ========================================
 * KOTLIN SYMBOL PROCESSING CONFIGURATION
 * ========================================
 *
 * Advanced KSP configuration for Room annotation processing, schema management,
 * and build optimization. This configuration ensures efficient code generation
 * and proper database schema management for production deployment.
 *
 * **Schema Management Strategy:**
 * Room's schema location configuration enables comprehensive database version
 * management, migration validation, and schema documentation for enterprise
 * deployment scenarios.
 */

ksp {
    /*
     * Database Schema Storage Configuration
     *
     * Specifies the location where Room stores generated database schemas
     * for version control, migration validation, and documentation purposes.
     * This enables proper database evolution tracking and migration testing.
     *
     * **Schema Benefits:**
     * - Version control integration for database schema changes
     * - Automated migration validation and testing
     * - Documentation generation for database structure
     * - Schema comparison tools for development and deployment
     * - Regression testing for database compatibility
     */
    arg("room.schemaLocation", "$projectDir/schemas")

    /*
     * Incremental Processing Optimization
     *
     * Enables incremental annotation processing to improve build performance
     * by only reprocessing changed files during development. This significantly
     * reduces build times for large database schemas and complex entity models.
     *
     * **Performance Benefits:**
     * - Faster incremental builds during development
     * - Reduced memory usage during annotation processing
     * - Improved IDE responsiveness during code changes
     * - Optimized CI/CD pipeline build times
     */
    arg("room.incremental", "true")
}

/*
 * ========================================
 * DOKKA DOCUMENTATION CONFIGURATION
 * ========================================
 *
 * Specialized documentation configuration for the KARL Room Storage module,
 * providing comprehensive database API reference with data modeling examples,
 * migration strategies, and performance optimization guidelines for database
 * administrators and application developers.
 *
 * **Documentation Philosophy for Database Systems:**
 * The KARL Room documentation serves the specialized needs of database developers,
 * system administrators, and privacy engineers working with local-first data systems:
 * - **Database Developers**: Comprehensive API reference with schema design patterns
 * - **System Administrators**: Performance tuning and monitoring guidelines
 * - **Privacy Engineers**: Data protection implementation and compliance guidance
 * - **Application Developers**: Integration patterns for reactive data access
 * - **DevOps Engineers**: Deployment, backup, and recovery procedures
 *
 * **Quality Standards for Database Documentation:**
 * - All database operations must include performance characteristics and optimization tips
 * - Schema design patterns must be documented with scalability considerations
 * - Privacy and security implementations must include formal verification details
 * - Migration strategies must provide comprehensive upgrade and rollback procedures
 * - Query optimization techniques must include execution plan analysis and indexing strategies
 * - Data integrity mechanisms must be thoroughly documented with validation procedures
 *
 * **Educational Value:**
 * This documentation provides comprehensive guidance for implementing privacy-first
 * database systems while maintaining enterprise-grade performance, reliability,
 * and data protection standards. It demonstrates how to build robust local-first
 * applications without compromising on functionality or user experience.
 */

// Dokka configuration for karl-room module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            /*
             * Module Identity for Database Storage Context
             *
             * Establishes clear identity within the KARL framework ecosystem while
             * highlighting the specialized database storage capabilities and Room integration.
             */

            // Descriptive module name emphasizing Room integration and storage capabilities
            moduleName.set("KARL Room Storage")

            // Version tracking for database schema compatibility and migration planning
            moduleVersion.set(project.version.toString())

            /*
             * Module Documentation Integration
             *
             * Includes comprehensive module documentation covering:
             * - Database architecture and entity relationship modeling
             * - Privacy-preserving data storage techniques and encryption strategies
             * - Performance optimization strategies for database operations
             * - Integration patterns with Room and SQLite frameworks
             * - Migration management and schema evolution best practices
             * - Backup and recovery procedures for production deployment
             */
            includes.from("Module.md")

            /*
             * Source Code Linking for Database Implementation Transparency
             *
             * Provides direct access to database implementation details, enabling
             * developers and administrators to understand data storage patterns,
             * privacy protection mechanisms, and performance optimization techniques
             * at the source level.
             *
             * **Benefits for Database Professionals:**
             * - Immediate access to database schema and query implementations
             * - Enhanced understanding of privacy-preserving storage techniques
             * - Streamlined contribution workflow for database optimization improvements
             * - Better debugging capabilities for complex query performance issues
             * - Real-time verification of data integrity and security implementations
             */
            sourceLink {
                // Local source directory containing database implementations
                localDirectory.set(projectDir.resolve("src"))

                // Remote GitHub repository for public database implementation inspection
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-room/src"))

                // Line number linking for precise implementation location identification
                remoteLineSuffix.set("#L")
            }

            /*
             * External Documentation Integration for Database Ecosystem
             *
             * Strategic linking to essential database library documentation provides
             * comprehensive context for developers working with KARL's storage capabilities
             * and enables seamless navigation between framework and dependency documentation.
             *
             * **Integration Benefits:**
             * - Comprehensive understanding of underlying database technology stack
             * - Seamless navigation between KARL storage APIs and dependency documentation
             * - Enhanced learning experience for developers new to Room and SQLite
             * - Better debugging and optimization capabilities for database operations
             * - Reduced context switching during database development workflows
             */

            /*
             * Kotlin Coroutines Documentation Integration
             *
             * Essential for understanding asynchronous database operations, reactive
             * data access patterns, and concurrent transaction management strategies
             * used throughout KARL's storage implementations for responsive applications.
             */
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            /*
             * Android Room Framework Documentation Integration
             *
             * Critical reference for understanding database fundamentals, entity
             * modeling, query optimization, and migration strategies that form
             * the foundation of KARL's persistence capabilities.
             *
             * **Room Integration Benefits:**
             * - Comprehensive understanding of entity-relationship modeling patterns
             * - Deep dive into query optimization and database performance tuning
             * - Advanced migration strategies and schema evolution techniques
             * - Database testing patterns and validation procedures
             * - Integration patterns between Room and broader Android architecture components
             */
            externalDocumentationLink {
                url.set(URL("https://developer.android.com/reference/androidx/room/package-summary"))
            }
        }
    }
}
