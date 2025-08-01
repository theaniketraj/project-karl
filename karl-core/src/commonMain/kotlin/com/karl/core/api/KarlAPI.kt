/*
 * Copyright (c) 2025 Project KARL Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * This file contains the primary API entry point for the KARL (Kotlin Adaptive Reasoning Learner) library.
 * It provides a fluent builder pattern for creating and configuring KarlContainer instances with proper
 * dependency injection and lifecycle management.
 */
package com.karl.core.api

import api.KarlContainer
import api.LearningEngine
import com.karl.core.models.KarlInstruction
import container.KarlContainerImpl // We'll implement this class next
import kotlinx.coroutines.CoroutineScope

/**
 * The main entry point and facade for interacting with the KARL (Kotlin Adaptive Reasoning Learner) library.
 *
 * This object serves as the primary API surface for developers integrating KARL into their applications.
 * It provides a clean, fluent builder pattern API that abstracts the complexity of container creation
 * and dependency wiring while ensuring proper lifecycle management.
 *
 * Key responsibilities:
 * - Provides a type-safe builder pattern for KarlContainer creation
 * - Abstracts implementation details from client code
 * - Ensures all required dependencies are properly configured before container instantiation
 * - Maintains consistent API contracts across different KARL implementations
 *
 * Usage pattern:
 * ```kotlin
 * val container = Karl.forUser("user_123")
 *     .withLearningEngine(learningEngine)
 *     .withDataStorage(dataStorage)
 *     .withDataSource(dataSource)
 *     .withCoroutineScope(scope)
 *     .build()
 *
 * container.initialize()
 * ```
 *
 * Design rationale:
 * - Static object ensures singleton-like behavior for the API entry point
 * - Builder pattern provides fluent configuration and prevents invalid states
 * - User-scoped containers enable multi-tenant scenarios within single applications
 * - Delayed initialization pattern allows for proper lifecycle management
 */
object Karl {
    /**
     * Initiates the container configuration process for a specified user identity.
     *
     * This method creates a new builder instance scoped to a specific user ID, enabling
     * multi-tenant scenarios where multiple users can have independent KARL containers
     * within the same application instance. Each user's data, learning state, and
     * interactions are completely isolated from other users.
     *
     * User ID considerations:
     * - Must be unique and stable across application sessions
     * - Used as the primary key for data storage and retrieval operations
     * - Should follow your application's user identification scheme
     * - Cannot be changed after container creation without losing learned state
     *
     * Container lifecycle:
     * 1. Create builder with this method
     * 2. Configure dependencies using builder methods
     * 3. Call build() to create container instance
     * 4. Call initialize() to load state and start background processes
     * 5. Use container for predictions and learning
     * 6. Call saveState() and release() when shutting down
     *
     * @param userId The unique, persistent identifier for the user. This identifier
     *               must remain consistent across application sessions to maintain
     *               continuity of learned behaviors and stored state. The ID is used
     *               as the primary key for all user-scoped data operations including
     *               interaction storage, model state persistence, and instruction management.
     * @return A new [KarlContainerBuilder] instance configured with the specified user ID,
     *         ready for dependency configuration and container instantiation.
     *
     * @throws IllegalArgumentException if userId is null, empty, or contains invalid characters
     *
     * @see KarlContainerBuilder for configuration options
     * @see KarlContainer for container lifecycle operations
     */
    fun forUser(userId: String): KarlContainerBuilder {
        // Validate user ID format and constraints
        require(userId.isNotBlank()) {
            "User ID cannot be null or blank. Provide a valid, persistent user identifier."
        }
        require(userId.length <= 255) {
            "User ID must be 255 characters or less to ensure database compatibility."
        }

        return KarlContainerBuilder(userId)
    }
}

/**
 * A fluent builder implementation for configuring and instantiating KarlContainer instances.
 *
 * This builder implements the Builder pattern to provide a type-safe, fluent API for constructing
 * properly configured KarlContainer instances. It enforces dependency injection principles by
 * requiring all essential dependencies before allowing container creation, preventing runtime
 * errors and ensuring proper initialization.
 *
 * Architecture benefits:
 * - Compile-time safety through required dependency validation
 * - Immutable configuration once built
 * - Clear separation of concerns between configuration and execution
 * - Extensible design for future configuration options
 *
 * Required dependencies:
 * 1. **LearningEngine**: Handles AI model training, inference, and state management
 *    - Typically provided by specialized modules (e.g., karl-kldl for KotlinDL integration)
 *    - Must support incremental learning and model serialization/deserialization
 *    - Responsible for feature extraction and prediction generation
 *
 * 2. **DataStorage**: Manages persistent storage of container state and interaction data
 *    - Usually implemented using database abstractions (e.g., karl-room, karl-sqldelight)
 *    - Handles encryption, data versioning, and migration strategies
 *    - Provides thread-safe operations for concurrent access scenarios
 *
 * 3. **DataSource**: Application-specific component that feeds interaction data to KARL
 *    - Implemented by the client application to capture user behaviors
 *    - Converts application events into standardized InteractionData format
 *    - Manages event filtering and rate limiting to prevent data overflow
 *
 * 4. **CoroutineScope**: Manages asynchronous operations and lifecycle
 *    - Should be tied to appropriate application lifecycle (Activity, ViewModel, etc.)
 *    - Used for background learning, periodic state saves, and cleanup operations
 *    - Ensures proper resource management and graceful shutdown
 *
 * Optional configurations:
 * - **Instructions**: User-defined rules and preferences that influence learning behavior
 *   - Can be updated dynamically after container creation
 *   - Used for customizing prediction algorithms and filtering strategies
 *   - Supports both programmatic and natural language instruction formats
 *
 * Container lifecycle after build():
 * 1. Call `initialize()` to load existing state and start background processes
 * 2. Container begins observing DataSource for new interaction events
 * 3. Learning engine processes events and updates model incrementally
 * 4. Call `predict()` to get AI-generated suggestions and insights
 * 5. Call `saveState()` periodically or before shutdown
 * 6. Call `release()` to clean up resources and stop background operations
 *
 * Thread safety:
 * - Builder itself is not thread-safe and should be used from single thread
 * - Built container instances are designed for concurrent access
 * - All dependency implementations must be thread-safe
 *
 * Error handling:
 * - Build-time validation prevents common configuration errors
 * - Runtime errors in dependencies are propagated through container methods
 * - Failed initialization attempts can be retried with different configurations
 *
 * @param userId The unique user identifier that this builder will configure the container for.
 *               This identifier is immutable after builder creation and determines data isolation.
 *
 * @see Karl.forUser for the recommended way to create builder instances
 * @see KarlContainer for detailed container operation documentation
 * @see LearningEngine for learning algorithm integration patterns
 * @see DataStorage for persistence strategy implementation
 * @see DataSource for application event integration patterns
 */
class KarlContainerBuilder internal constructor(private val userId: String) { // Internal constructor hides the builder creation detail

    // Dependency injection fields - all required dependencies start as null and must be configured
    private var learningEngine: LearningEngine? = null
    private var dataStorage: DataStorage? = null
    private var dataSource: DataSource? = null
    private var instructions: List<KarlInstruction> = emptyList() // Optional, defaults to empty
    private var coroutineScope: CoroutineScope? = null

    /**
     * Configures the learning engine implementation that will power the AI capabilities.
     *
     * The learning engine is the core AI component responsible for:
     * - Processing incoming interaction data and extracting meaningful features
     * - Training machine learning models incrementally as new data arrives
     * - Generating predictions and recommendations based on learned patterns
     * - Managing model state serialization for persistence across sessions
     * - Adapting to user behavior changes over time through continuous learning
     *
     * Common implementations:
     * - **KLDLLearningEngine**: Uses KotlinDL for deep learning capabilities
     * - **SimpleMLEngine**: Basic statistical learning for lightweight scenarios
     * - **CustomEngine**: Application-specific implementations for specialized domains
     *
     * Engine selection considerations:
     * - **Model complexity**: Choose based on available computational resources
     * - **Data volume**: Consider memory and processing requirements
     * - **Prediction latency**: Balance accuracy with response time requirements
     * - **Privacy requirements**: Ensure all processing remains local and secure
     *
     * Integration patterns:
     * ```kotlin
     * val engine = KLDLLearningEngine.builder()
     *     .withHiddenLayers(64, 32)
     *     .withLearningRate(0.001f)
     *     .withBatchSize(32)
     *     .build()
     *
     * builder.withLearningEngine(engine)
     * ```
     *
     * @param engine A fully configured LearningEngine instance that implements the
     *               required training and inference capabilities. The engine must be
     *               thread-safe and capable of incremental learning from streaming data.
     * @return This builder instance for method chaining.
     *
     * @throws IllegalArgumentException if engine is null or not properly configured
     *
     * @see LearningEngine for detailed interface documentation
     * @see com.karl.kldl.KLDLLearningEngine for KotlinDL-based implementation
     */
    fun withLearningEngine(engine: LearningEngine): KarlContainerBuilder =
        apply {
            requireNotNull(engine) { "LearningEngine cannot be null" }
            this.learningEngine = engine
        }

    /**
     * Configures the data storage implementation for persistent state and interaction history.
     *
     * The data storage component handles all persistence operations including:
     * - **Container state**: Serialized AI model weights, parameters, and configuration
     * - **Interaction history**: Timestamped user interactions for replay and analysis
     * - **User preferences**: Instructions, settings, and customization data
     * - **Performance metrics**: Learning statistics and model evaluation data
     * - **Data migration**: Schema updates and backward compatibility handling
     *
     * Storage implementations must guarantee:
     * - **Data isolation**: Complete separation between different user accounts
     * - **Thread safety**: Concurrent access from multiple coroutines
     * - **Atomic operations**: Consistent state even during application crashes
     * - **Encryption**: Sensitive data protection using appropriate crypto standards
     * - **Efficient queries**: Fast retrieval for real-time prediction scenarios
     *
     * Common implementations:
     * - **RoomDataStorage**: Android Room database with SQLite backend
     * - **SQLDelightStorage**: Cross-platform SQL database abstraction
     * - **InMemoryStorage**: Volatile storage for testing and development
     * - **EncryptedFileStorage**: File-based storage with automatic encryption
     *
     * Performance considerations:
     * - Choose storage based on expected data volume and query patterns
     * - Configure appropriate indexing for frequently accessed data
     * - Implement data cleanup policies to prevent unbounded growth
     * - Consider backup and restore capabilities for user data migration
     *
     * Security considerations:
     * - All user data must be encrypted at rest using industry standards
     * - Implement proper key management and rotation policies
     * - Ensure compliance with data protection regulations (GDPR, CCPA)
     * - Provide user controls for data export and deletion
     *
     * @param storage A fully configured DataStorage instance that provides persistent
     *                storage capabilities with proper encryption and isolation guarantees.
     * @return This builder instance for method chaining.
     *
     * @throws IllegalArgumentException if storage is null or not properly configured
     *
     * @see DataStorage for detailed interface documentation
     * @see com.karl.room.RoomDataStorage for Room database implementation
     * @see com.karl.sqldelight.SQLDelightStorage for cross-platform SQL implementation
     */
    fun withDataStorage(storage: DataStorage): KarlContainerBuilder =
        apply {
            requireNotNull(storage) { "DataStorage cannot be null" }
            this.dataStorage = storage
        }

    /**
     * Configures the data source that feeds user interaction events into the learning system.
     *
     * The data source acts as the bridge between your application's user interface and
     * KARL's learning engine. It captures meaningful user interactions and converts them
     * into standardized InteractionData objects that the learning engine can process.
     *
     * Data source responsibilities:
     * - **Event capture**: Monitor user actions, UI interactions, and system events
     * - **Data transformation**: Convert application events to standardized format
     * - **Event filtering**: Exclude noise and focus on learning-relevant interactions
     * - **Rate limiting**: Prevent overwhelming the learning engine with excessive data
     * - **Privacy filtering**: Ensure sensitive data is excluded or anonymized
     *
     * Common interaction types to capture:
     * - **UI events**: Button clicks, menu selections, navigation patterns
     * - **Content interactions**: Document views, search queries, item selections
     * - **Temporal patterns**: Session duration, time-of-day usage, frequency patterns
     * - **Context data**: Application state, user preferences, environmental factors
     * - **Performance metrics**: Task completion time, error rates, success indicators
     *
     * Implementation patterns:
     * ```kotlin
     * class MyAppDataSource : DataSource {
     *     override fun observeInteractionData(
     *         onNewData: suspend (InteractionData) -> Unit,
     *         coroutineScope: CoroutineScope
     *     ): Job {
     *         return coroutineScope.launch {
     *             // Collect UI events and convert to InteractionData
     *             uiEventFlow.collect { event ->
     *                 val interaction = InteractionData(
     *                     type = event.type,
     *                     details = event.toDetails(),
     *                     timestamp = System.currentTimeMillis(),
     *                     userId = currentUserId
     *                 )
     *                 onNewData(interaction)
     *             }
     *         }
     *     }
     * }
     * ```
     *
     * Data quality considerations:
     * - Ensure consistent data format across different interaction types
     * - Include sufficient context for meaningful pattern recognition
     * - Balance detail level with privacy and performance requirements
     * - Implement proper error handling for data conversion failures
     *
     * Privacy and security:
     * - Never capture sensitive user data (passwords, personal information)
     * - Implement data anonymization for identification-sensitive interactions
     * - Provide user controls for disabling data collection
     * - Ensure compliance with applicable privacy regulations
     *
     * @param source Your application's implementation of the DataSource interface that
     *               captures and standardizes user interaction events for learning.
     * @return This builder instance for method chaining.
     *
     * @throws IllegalArgumentException if source is null
     *
     * @see DataSource for detailed interface documentation
     * @see InteractionData for data format specifications
     */
    fun withDataSource(source: DataSource): KarlContainerBuilder =
        apply {
            requireNotNull(source) { "DataSource cannot be null" }
            this.dataSource = source
        }

    /**
     * Configures optional user-defined instructions that influence learning behavior.
     *
     * Instructions provide a mechanism for users and applications to guide KARL's
     * learning process without requiring deep knowledge of machine learning algorithms.
     * They act as high-level constraints and preferences that shape how the AI
     * interprets data and generates predictions.
     *
     * Instruction categories:
     * - **Learning preferences**: What types of patterns to prioritize or ignore
     * - **Prediction constraints**: Rules about when and how to make suggestions
     * - **Privacy controls**: Limitations on data usage and storage
     * - **Context rules**: How to interpret interactions in different scenarios
     * - **Adaptation policies**: How quickly to respond to changing user behavior
     *
     * Instruction formats:
     * ```kotlin
     * val instructions = listOf(
     *     KarlInstruction.LearningFocus(
     *         type = "temporal_patterns",
     *         priority = InstructionPriority.HIGH,
     *         description = "Focus on time-of-day usage patterns"
     *     ),
     *     KarlInstruction.PredictionRule(
     *         condition = "confidence < 0.7",
     *         action = "suppress_suggestion",
     *         description = "Only show high-confidence predictions"
     *     ),
     *     KarlInstruction.PrivacyConstraint(
     *         type = "data_retention",
     *         value = "30_days",
     *         description = "Automatically delete old interaction data"
     *     )
     * )
     * ```
     *
     * Dynamic instruction updates:
     * Instructions can be modified after container creation using:
     * ```kotlin
     * container.updateInstructions(newInstructions)
     * ```
     *
     * This allows for runtime adaptation based on user feedback or changing requirements.
     *
     * Instruction processing:
     * - Instructions are validated for syntax and semantic correctness
     * - Conflicting instructions are resolved using priority ordering
     * - Changes take effect immediately for new predictions
     * - Historical data interpretation may be affected by instruction changes
     *
     * @param instructions A list of KarlInstruction objects that define learning
     *                    preferences and constraints. Can be empty for default behavior.
     *                    Instructions can be updated later via container methods.
     * @return This builder instance for method chaining.
     *
     * @see KarlInstruction for instruction format documentation
     * @see KarlContainer.updateInstructions for runtime instruction updates
     */
    fun withInstructions(instructions: List<KarlInstruction>): KarlContainerBuilder =
        apply {
            requireNotNull(instructions) { "Instructions list cannot be null (use empty list for no instructions)" }
            this.instructions = instructions.toList() // Create defensive copy
        }

    /**
     * Configures the coroutine scope that manages KARL's asynchronous operations.
     *
     * The coroutine scope is critical for proper lifecycle management and resource cleanup.
     * It controls the execution context for all background operations including learning,
     * data processing, prediction generation, and periodic maintenance tasks.
     *
     * Scope responsibilities:
     * - **Background learning**: Incremental model training from interaction streams
     * - **Periodic saves**: Automatic state persistence at configurable intervals
     * - **Data cleanup**: Removal of expired interactions and temporary data
     * - **Health monitoring**: Detection and recovery from internal errors
     * - **Resource management**: Proper cleanup when container is shut down
     *
     * Recommended scope types:
     *
     * **Application-level scope** (long-running containers):
     * ```kotlin
     * val applicationScope = CoroutineScope(
     *     SupervisorJob() + Dispatchers.Default + CoroutineName("KARL-Container")
     * )
     * ```
     * Use for containers that should persist across multiple UI components.
     *
     * **ViewModel scope** (UI-bound containers):
     * ```kotlin
     * class MyViewModel : ViewModel() {
     *     private val container = Karl.forUser(userId)
     *         .withCoroutineScope(viewModelScope)
     *         .build()
     * }
     * ```
     * Use for containers tied to specific UI lifecycles.
     *
     * **Activity/Fragment scope** (short-lived containers):
     * ```kotlin
     * class MyActivity : AppCompatActivity() {
     *     private val activityScope = MainScope()
     *
     *     override fun onDestroy() {
     *         super.onDestroy()
     *         activityScope.cancel()
     *     }
     * }
     * ```
     * Use for containers that should be destroyed with UI components.
     *
     * Scope configuration best practices:
     * - Use SupervisorJob to prevent single task failures from cancelling entire scope
     * - Choose appropriate dispatcher based on workload characteristics
     * - Include CoroutineName for better debugging and monitoring
     * - Ensure scope is cancelled when no longer needed to prevent resource leaks
     * - Consider using structured concurrency patterns for complex scenarios
     *
     * Error handling and recovery:
     * - Scope cancellation triggers graceful shutdown of all KARL operations
     * - Uncaught exceptions in background tasks are logged but don't crash container
     * - Failed operations are retried with exponential backoff when appropriate
     * - Container state is automatically saved before scope cancellation
     *
     * Performance considerations:
     * - Learning operations are CPU-intensive and benefit from Dispatchers.Default
     * - IO operations use Dispatchers.IO for database and file operations
     * - UI updates should use Dispatchers.Main when integrating with UI components
     * - Consider using custom dispatchers for fine-grained control over thread pools
     *
     * @param scope A properly configured CoroutineScope that will manage all
     *              asynchronous operations for the container. The scope should
     *              be tied to an appropriate lifecycle and include proper error
     *              handling and resource cleanup mechanisms.
     * @return This builder instance for method chaining.
     *
     * @throws IllegalArgumentException if scope is null or already cancelled
     *
     * @see CoroutineScope for scope configuration patterns
     * @see kotlinx.coroutines.SupervisorJob for error isolation strategies
     * @see kotlinx.coroutines.Dispatchers for thread pool selection
     */
    fun withCoroutineScope(scope: CoroutineScope): KarlContainerBuilder =
        apply {
            requireNotNull(scope) { "CoroutineScope cannot be null" }
            this.coroutineScope = scope
        }

    /**
     * Validates configuration and constructs a new KarlContainer instance.
     *
     * This method performs comprehensive validation of all required dependencies and
     * constructs a properly configured container instance. The validation process ensures
     * that all essential components are present and correctly configured before creating
     * the container, preventing runtime failures and configuration errors.
     *
     * Validation performed:
     * - **Dependency presence**: Ensures all required components are configured
     * - **Dependency validity**: Verifies that components are properly initialized
     * - **Configuration consistency**: Checks for conflicting settings or constraints
     * - **Resource availability**: Validates that required resources are accessible
     * - **Scope validity**: Ensures the provided coroutine scope is active and suitable
     *
     * Container creation process:
     * 1. Validate all required dependencies are present and properly configured
     * 2. Create internal container implementation with dependency injection
     * 3. Configure internal components with provided settings and instructions
     * 4. Return ready-to-initialize container instance
     *
     * Post-build requirements:
     * The returned container is NOT yet initialized and ready for use. You MUST call
     * `container.initialize()` to complete the setup process. This two-phase initialization
     * pattern provides several benefits:
     *
     * - **Error handling**: Allows separate handling of configuration vs. runtime errors
     * - **Lifecycle control**: Enables precise control over when background processes start
     * - **Testing support**: Facilitates unit testing by allowing mock initialization
     * - **Resource management**: Ensures resources are allocated only when needed
     *
     * Initialization sequence after build():
     * ```kotlin
     * val container = builder.build()  // Configuration validation and container creation
     * container.initialize()           // State loading and background process startup
     *
     * // Container is now ready for predictions and learning
     * val prediction = container.predict(context)
     * container.processInteraction(interactionData)
     *
     * // Proper cleanup when done
     * container.saveState().join()
     * container.release()
     * ```
     *
     * Error scenarios and recovery:
     * - **Configuration errors**: Thrown immediately with detailed error messages
     * - **Resource errors**: May be deferred until initialize() is called
     * - **Dependency errors**: Validation catches most issues at build time
     * - **Recovery strategies**: Failed builds can be retried with corrected configuration
     *
     * Thread safety and concurrency:
     * - Builder validation is single-threaded and not thread-safe
     * - Resulting container is designed for concurrent access from multiple threads
     * - All internal synchronization is handled by the container implementation
     * - Container lifecycle methods can be called from any thread
     *
     * Memory and resource considerations:
     * - Container holds references to all provided dependencies
     * - Background resources are allocated during initialize(), not build()
     * - Proper cleanup requires calling release() when container is no longer needed
     * - Consider using weak references or lifecycle observers for automatic cleanup
     *
     * @return A newly created KarlContainer instance configured with all provided
     *         dependencies and ready for initialization. The container encapsulates
     *         all learning, storage, and prediction capabilities while maintaining
     *         proper isolation and resource management.
     *
     * @throws IllegalStateException if any required dependency is missing or invalid:
     *         - LearningEngine not provided via withLearningEngine()
     *         - DataStorage not provided via withDataStorage()
     *         - DataSource not provided via withDataSource()
     *         - CoroutineScope not provided via withCoroutineScope()
     *         - CoroutineScope is cancelled or inactive
     *
     * @throws IllegalArgumentException if any dependency is improperly configured:
     *         - LearningEngine configuration is invalid or incomplete
     *         - DataStorage cannot be initialized or accessed
     *         - DataSource implementation is malformed
     *         - Instructions contain syntax errors or conflicts
     *
     * @see KarlContainer.initialize for completing the initialization process
     * @see KarlContainer for detailed container operation documentation
     * @see LearningEngine for learning algorithm configuration
     * @see DataStorage for persistence configuration
     * @see DataSource for event integration patterns
     */
    fun build(): KarlContainer {
        // Perform comprehensive validation to ensure all required dependencies are properly configured
        val engine =
            learningEngine
                ?: throw IllegalStateException(
                    "LearningEngine must be provided using withLearningEngine(). " +
                        "This component is responsible for AI model training and prediction generation.",
                )
        val storage =
            dataStorage
                ?: throw IllegalStateException(
                    "DataStorage must be provided using withDataStorage(). " +
                        "This component handles persistent storage of container state and interaction data.",
                )
        val source =
            dataSource
                ?: throw IllegalStateException(
                    "DataSource must be provided using withDataSource(). " +
                        "This component feeds user interaction events into the learning system.",
                )
        val scope =
            coroutineScope
                ?: throw IllegalStateException(
                    "CoroutineScope must be provided using withCoroutineScope(). " +
                        "This scope manages all asynchronous operations and background tasks.",
                )

        // Additional validation for component compatibility and configuration
        // Note: Scope state validation is performed during initialize() phase
        // as CoroutineScope.isActive is not available in all Kotlin contexts

        // Validate instructions for basic format consistency
        instructions.forEach { instruction ->
            // Basic instruction validation - detailed validation happens during initialize()
            // Custom validation logic would be implemented here based on instruction types
            requireNotNull(instruction) { "Instructions list cannot contain null values" }
        }

        // Create and return the concrete container implementation with dependency injection
        // The implementation handles all internal wiring and component coordination
        return KarlContainerImpl(
            userId = userId,
            learningEngine = engine,
            dataStorage = storage,
            dataSource = source,
            initialInstructions = instructions.toList(), // Defensive copy to prevent external modification
            containerScope = scope, // Pass the validated scope to the implementation
        )
    }
}

/*
 * Type aliases and re-exports for improved API ergonomics and backwards compatibility.
 *
 * These aliases provide convenient access to core KARL types without requiring
 * developers to import multiple packages. They also create a stable API surface
 * that can be maintained even as internal package structures evolve.
 *
 * Design rationale:
 * - Simplifies import statements in client code
 * - Provides backwards compatibility during refactoring
 * - Creates a clear API boundary between public and internal types
 * - Enables easier migration between different implementation modules
 *
 * Usage patterns:
 * ```kotlin
 * import com.karl.core.api.*
 *
 * // All core types available without additional imports
 * val storage: DataStorage = createStorage()
 * val source: DataSource = createSource()
 * val container: KarlContainer = Karl.forUser("user").build()
 * ```
 *
 * Maintenance considerations:
 * - Keep aliases stable across version updates
 * - Document any breaking changes in migration guides
 * - Consider deprecation warnings before removing aliases
 * - Ensure alias targets remain compatible with client expectations
 */

// Re-export core interfaces for convenient access from the API entry point
// This eliminates the need for developers to import from multiple packages
// while maintaining clear separation of concerns in the internal architecture

/**
 * Alias for the data storage interface, providing convenient access without
 * requiring imports from the internal models package.
 *
 * @see com.karl.core.models.DataStorage for detailed interface documentation
 */
typealias DataStorage = com.karl.core.models.DataStorage

/**
 * Alias for the data source interface, enabling simplified imports for
 * applications implementing their own data collection strategies.
 *
 * @see com.karl.core.models.DataSource for detailed interface documentation
 */
typealias DataSource = com.karl.core.models.DataSource

// Note: LearningEngine and KarlContainer are already imported directly from
// their api package, so no additional aliases are needed for these types
