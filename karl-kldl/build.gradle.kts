/*
 * KARL KotlinDL Engine Module - Build Configuration
 *
 * This build script configures the KARL KotlinDL Engine module, which provides a
 * comprehensive machine learning implementation for the KARL (Kotlin Adaptive Reasoning
 * Learner) framework using KotlinDL and TensorFlow Lite. This module bridges the gap
 * between KARL's privacy-first architecture and powerful deep learning capabilities.
 *
 * **Module Purpose and Architecture:**
 * The karl-kldl module serves as a production-ready machine learning engine that
 * implements KARL's learning interfaces using industry-standard deep learning libraries
 * while maintaining strict privacy guarantees through local-only processing. This module
 * demonstrates how to integrate sophisticated ML capabilities without compromising
 * the framework's core privacy-first principles.
 *
 * **Key Capabilities and Features:**
 * - Deep learning model training and inference using KotlinDL
 * - TensorFlow Lite integration for optimized mobile and edge deployment
 * - GPU acceleration support for enhanced performance on compatible hardware
 * - Comprehensive dataset management and preprocessing pipelines
 * - Privacy-preserving learning algorithms with local-only data processing
 * - Real-time model adaptation and incremental learning capabilities
 * - Memory-efficient model architectures optimized for desktop applications
 *
 * **Machine Learning Architecture:**
 * This module provides concrete implementations of KARL's learning abstractions:
 * - Neural network architectures for pattern recognition and adaptive behavior
 * - Reinforcement learning algorithms for user preference optimization
 * - Transfer learning capabilities for domain-specific adaptations
 * - Online learning algorithms for continuous model improvement
 * - Feature engineering pipelines for multimodal data processing
 * - Model compression techniques for efficient resource utilization
 *
 * **Privacy and Security Design:**
 * - All model training occurs locally without external data transmission
 * - Federated learning patterns for collaborative learning without data sharing
 * - Differential privacy techniques for additional privacy protection
 * - Secure model storage with encryption for sensitive learning parameters
 * - Data anonymization and aggregation for usage analytics
 * - Compliance with GDPR, CCPA, and other privacy regulations
 *
 * **Performance Optimization:**
 * - GPU acceleration through TensorFlow Lite GPU delegate
 * - Efficient memory management for large-scale learning tasks
 * - Asynchronous training pipelines for responsive user interfaces
 * - Model quantization for reduced memory footprint and faster inference
 * - Batch processing optimization for improved throughput
 * - Multi-threading support for parallel data processing
 *
 * **Integration Ecosystem:**
 * This module integrates seamlessly with other KARL components:
 * - karl-core: Implements core learning engine interfaces
 * - karl-compose-ui: Provides ML progress visualization components
 * - karl-room: Enables persistent model storage and dataset management
 * - karl-example-desktop: Demonstrates real-world ML integration patterns
 *
 * **Target Applications:**
 * - Intelligent user interfaces with personalized behavior adaptation
 * - Recommendation systems with privacy-preserving collaborative filtering
 * - Adaptive content delivery based on user interaction patterns
 * - Predictive text and autocomplete systems with local learning
 * - Image and document classification for personal productivity tools
 * - Natural language processing for local-only virtual assistants
 *
 * @module karl-kldl
 * @since 1.0.0
 * @author KARL Development Team
 * @see <a href="https://github.com/theaniketraj/project-karl">KARL Framework Documentation</a>
 * @see <a href="https://kotlin.github.io/kotlindl/">KotlinDL Documentation</a>
 */

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URL

/*
 * ========================================
 * GRADLE PLUGIN CONFIGURATION
 * ========================================
 *
 * Essential plugins for building a sophisticated machine learning module
 * with cross-platform compatibility and comprehensive documentation.
 */

plugins {
    // Kotlin Multiplatform for cross-platform ML capabilities
    alias(libs.plugins.kotlinMultiplatform)

    // Dokka for comprehensive API documentation with ML-specific examples
    id("org.jetbrains.dokka")
}

/*
 * ========================================
 * KOTLIN MULTIPLATFORM CONFIGURATION
 * ========================================
 *
 * Advanced multiplatform configuration optimized for machine learning workloads
 * and high-performance computing while maintaining KARL's privacy-first architecture.
 *
 * **Platform Strategy for Machine Learning:**
 * The module targets JVM primarily for access to mature ML libraries and optimal
 * performance, with architecture designed for future expansion to Android Native
 * and potentially other platforms supporting TensorFlow Lite operations.
 *
 * **Performance Considerations:**
 * - Java 21 target leverages virtual threads for ML pipeline parallelization
 * - Enhanced garbage collection for memory-intensive training operations
 * - Foreign Function & Memory API integration for native ML library optimization
 * - Vector API support for accelerated mathematical operations
 * - Project Loom integration for efficient concurrent model training
 */

kotlin {
    /*
     * JVM Target Configuration for Machine Learning
     *
     * Configures JVM compilation with advanced optimizations specifically tailored
     * for machine learning workloads, mathematical computations, and large-scale
     * data processing operations.
     *
     * **Java 21 Benefits for Machine Learning:**
     * - Virtual threads enable massive parallelization of ML training pipelines
     * - Vector API provides SIMD operations for accelerated linear algebra
     * - Foreign Function & Memory API enables efficient native library integration
     * - Pattern matching simplifies complex ML algorithm implementations
     * - Enhanced garbage collection reduces training interruptions
     * - Structured concurrency ensures safe parallel data processing
     * - Memory management improvements for large dataset handling
     *
     * **Java Interoperability for ML Ecosystem:**
     * withJava() enables seamless integration with the vast Java ML ecosystem
     * including TensorFlow, Deeplearning4j, Weka, and other established libraries.
     */
    jvm {
        // Enable Java interoperability for extensive ML library ecosystem integration
        withJava()

        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    // Target Java 21 for cutting-edge ML performance optimizations
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
     * Comprehensive ML dependency configuration that balances powerful capabilities
     * with KARL's core principles of privacy preservation and local-only processing.
     *
     * **Dependency Strategy:**
     * - KotlinDL for high-level, Kotlin-native deep learning capabilities
     * - TensorFlow Lite for optimized inference and mobile deployment
     * - Core KARL framework integration for seamless architectural alignment
     * - Minimal external dependencies to maintain security and privacy guarantees
     */
    sourceSets {
        /*
         * Common Main Source Set
         *
         * Contains shared machine learning interfaces, algorithms, and utilities
         * that can be used across all target platforms. This includes abstract
         * ML model definitions, training algorithms, and privacy-preserving techniques.
         *
         * **Shared ML Components:**
         * - Abstract neural network architectures and layer definitions
         * - Common training algorithms and optimization strategies
         * - Data preprocessing and feature engineering utilities
         * - Privacy-preserving learning algorithm implementations
         * - Model serialization and deserialization utilities
         * - Cross-platform ML utility functions and mathematical operations
         */
        val commonMain by getting {
            dependencies {
                /*
                 * KARL Core Framework Integration
                 *
                 * Exposes the complete KARL core API to enable seamless integration
                 * between machine learning implementations and the broader framework
                 * ecosystem. This API exposure allows other modules to directly
                 * interact with ML capabilities through well-defined interfaces.
                 */
                api(project(":karl-core"))
            }
        }

        /*
         * JVM Main Source Set
         *
         * Platform-specific machine learning implementations leveraging the full
         * power of the JVM ecosystem for sophisticated deep learning capabilities.
         * Contains concrete implementations of KARL learning interfaces using
         * industry-standard ML libraries and frameworks.
         *
         * **JVM-Specific ML Features:**
         * - Full KotlinDL integration for comprehensive deep learning capabilities
         * - TensorFlow Lite GPU acceleration for enhanced performance
         * - Advanced dataset management and preprocessing pipelines
         * - High-performance mathematical operations and linear algebra
         * - Sophisticated model architectures and training strategies
         * - Integration with Java ML ecosystem for specialized algorithms
         */
        val jvmMain by getting {
            dependencies {
                /*
                 * Core Language and Concurrency Support
                 *
                 * Foundation libraries providing enhanced language features and
                 * asynchronous programming capabilities essential for ML workloads.
                 */

                // Enhanced Kotlin standard library with JDK8+ extensions for ML data processing
                implementation(libs.kotlin.stdlib.jdk8)

                // Kotlin Coroutines for asynchronous ML training and reactive model updates
                implementation(libs.kotlinx.coroutines.core)

                /*
                 * KotlinDL Deep Learning Framework
                 *
                 * Kotlin-native deep learning library providing high-level abstractions
                 * for neural network construction, training, and inference while
                 * maintaining excellent interoperability with the Kotlin ecosystem.
                 *
                 * **KotlinDL Benefits:**
                 * - Kotlin-first API design for type safety and developer productivity
                 * - Comprehensive neural network layer implementations
                 * - Built-in support for common ML tasks and model architectures
                 * - Seamless integration with Kotlin coroutines for async training
                 * - Memory-efficient implementations optimized for JVM performance
                 */

                // KotlinDL core API for neural network construction and training
                implementation(libs.kotlindl.api)

                // KotlinDL dataset utilities for data loading and preprocessing
                implementation(libs.kotlindl.dataset)

                /*
                 * TensorFlow Lite Integration
                 *
                 * Optimized TensorFlow runtime for efficient model inference and
                 * optional GPU acceleration. TensorFlow Lite provides production-ready
                 * performance for deployed models while maintaining compatibility
                 * with the broader TensorFlow ecosystem.
                 *
                 * **TensorFlow Lite GPU Benefits:**
                 * - Hardware-accelerated inference on compatible GPUs
                 * - Optimized memory usage for mobile and edge deployment
                 * - Reduced latency for real-time ML applications
                 * - Energy-efficient processing for battery-powered devices
                 * - Seamless integration with existing TensorFlow models
                 * - Support for quantized models for enhanced performance
                 */

                // TensorFlow Lite with GPU acceleration for optimized inference
                implementation("org.tensorflow:tensorflow-lite-gpu:2.9.0")
            }
        }
    }
}

/*
 * ========================================
 * DOKKA DOCUMENTATION CONFIGURATION
 * ========================================
 *
 * Specialized documentation configuration for the KARL KotlinDL Engine module,
 * providing comprehensive API reference with machine learning examples, integration
 * patterns, and performance optimization guidelines for ML practitioners.
 *
 * **Documentation Philosophy for Machine Learning:**
 * The KARL KotlinDL documentation serves the unique needs of ML practitioners
 * and framework integrators working with privacy-first adaptive learning systems:
 * - **ML Engineers**: Detailed API reference with training and inference examples
 * - **Privacy Engineers**: Comprehensive privacy-preserving ML implementation guidance
 * - **Performance Engineers**: Optimization strategies and benchmarking guidelines
 * - **Framework Integrators**: Integration patterns for ML-powered adaptive interfaces
 * - **Research Scientists**: Implementation details for novel privacy-preserving algorithms
 *
 * **Quality Standards for ML Documentation:**
 * - All ML algorithms must include mathematical foundations and implementation notes
 * - Performance characteristics must be documented with benchmarks and profiling data
 * - Privacy guarantees must be explicitly stated with formal analysis references
 * - Code examples must demonstrate real-world ML workflows and best practices
 * - Integration patterns must show complete end-to-end ML pipeline implementations
 * - Error handling strategies must address ML-specific failure modes and recovery
 *
 * **Educational Value:**
 * This documentation provides comprehensive guidance for implementing privacy-first
 * machine learning systems while maintaining enterprise-grade performance and
 * reliability standards. It bridges the gap between academic ML research and
 * production-ready privacy-preserving implementations.
 */

// Dokka configuration for karl-kldl module
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets {
        configureEach {
            /*
             * Module Identity for Machine Learning Context
             *
             * Establishes clear identity within the KARL framework ecosystem while
             * highlighting the specialized machine learning capabilities and KotlinDL integration.
             */

            // Descriptive module name emphasizing KotlinDL integration and ML capabilities
            moduleName.set("KARL KotlinDL Engine")

            // Version tracking for ML model compatibility and API evolution
            moduleVersion.set(project.version.toString())

            /*
             * Module Documentation Integration
             *
             * Includes comprehensive module documentation covering:
             * - Machine learning architecture and algorithm implementations
             * - Privacy-preserving ML techniques and formal guarantees
             * - Performance optimization strategies for ML workloads
             * - Integration patterns with KotlinDL and TensorFlow Lite
             * - Training pipeline configuration and best practices
             * - Model deployment and inference optimization guidelines
             */
            includes.from("Module.md")

            /*
             * Source Code Linking for ML Implementation Transparency
             *
             * Provides direct access to ML algorithm implementations, enabling
             * researchers and practitioners to understand privacy-preserving techniques,
             * performance optimizations, and integration patterns at the source level.
             *
             * **Benefits for ML Practitioners:**
             * - Immediate access to algorithm implementations for verification
             * - Enhanced understanding of privacy-preserving ML techniques
             * - Streamlined contribution workflow for ML algorithm improvements
             * - Better debugging capabilities for complex ML training pipelines
             * - Real-time verification of mathematical correctness against implementation
             */
            sourceLink {
                // Local source directory containing ML implementations
                localDirectory.set(projectDir.resolve("src"))

                // Remote GitHub repository for public ML algorithm inspection
                remoteUrl.set(URL("https://github.com/theaniketraj/project-karl/tree/main/karl-kldl/src"))

                // Line number linking for precise algorithm location identification
                remoteLineSuffix.set("#L")
            }

            /*
             * External Documentation Integration for ML Ecosystem
             *
             * Strategic linking to essential ML library documentation provides
             * comprehensive context for developers working with KARL's ML capabilities
             * and enables seamless navigation between framework and dependency docs.
             *
             * **Integration Benefits:**
             * - Comprehensive understanding of underlying ML technology stack
             * - Seamless navigation between KARL ML APIs and dependency documentation
             * - Enhanced learning experience for ML practitioners new to KotlinDL
             * - Better debugging and optimization capabilities for ML workloads
             * - Reduced context switching during ML algorithm development
             */

            /*
             * Kotlin Coroutines Documentation Integration
             *
             * Essential for understanding asynchronous ML training patterns, reactive
             * model updates, and concurrent data processing strategies used throughout
             * the KARL ML implementations for responsive user interfaces.
             */
            externalDocumentationLink {
                url.set(URL("https://kotlinlang.org/api/kotlinx.coroutines/"))
            }

            /*
             * KotlinDL Framework Documentation Integration
             *
             * Critical reference for understanding deep learning fundamentals,
             * neural network architectures, training strategies, and optimization
             * techniques that form the foundation of KARL's ML capabilities.
             *
             * **KotlinDL Integration Benefits:**
             * - Comprehensive understanding of neural network layer implementations
             * - Deep dive into training algorithm configurations and hyperparameters
             * - Advanced model architecture patterns and design strategies
             * - Performance optimization techniques for Kotlin-based ML workflows
             * - Integration patterns between KotlinDL and broader ML ecosystem
             */
            externalDocumentationLink {
                url.set(URL("https://kotlin.github.io/kotlindl/"))
            }
        }
    }
}
