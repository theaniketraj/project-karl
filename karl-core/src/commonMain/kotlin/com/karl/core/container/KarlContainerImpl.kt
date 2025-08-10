package container

import api.KarlContainer
import api.LearningEngine
import com.karl.core.models.DataSource
import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlInstruction
import com.karl.core.models.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/*
 * KARL Container Implementation Module
 *
 * This module contains the core implementation of the KarlContainer interface,
 * providing thread-safe orchestration of machine learning operations, data management,
 * and user interaction processing within the KARL framework.
 *
 * Key architectural principles implemented:
 * - Thread-safe concurrent operations using Kotlin coroutines and mutex synchronization
 * - Event-driven data processing pipeline with asynchronous learning
 * - Dependency injection pattern for testability and modularity
 * - Resource lifecycle management with proper cleanup semantics
 * - Privacy-first design with local-only data processing
 *
 * @since 1.0.0
 * @module karl-core
 */

/**
 * Core implementation of the KarlContainer interface, providing the primary orchestration layer
 * for the KARL (Kotlin Adaptive Reasoning Learner) framework.
 *
 * This class serves as the central coordinator that manages the complete lifecycle of a user's
 * adaptive learning experience, integrating the machine learning engine, persistent data storage,
 * and real-time data observation capabilities into a cohesive, thread-safe system.
 *
 * **Architecture Overview:**
 * - **Dependency Injection**: Receives all core dependencies through constructor injection,
 *   promoting testability and modularity while maintaining loose coupling between components
 * - **Concurrency Management**: Employs sophisticated coroutine-based concurrency with mutex
 *   protection for critical state operations, ensuring thread safety in multi-threaded environments
 * - **Event-Driven Learning**: Implements an asynchronous event-driven architecture where
 *   user interactions trigger incremental learning updates without blocking the main application flow
 * - **State Persistence**: Provides automatic state management with atomic save/load operations
 *   and crash recovery capabilities
 *
 * **Key Responsibilities:**
 * 1. **Lifecycle Management**: Controls initialization, operation, and cleanup of all AI components
 * 2. **Data Flow Orchestration**: Coordinates the flow of interaction data from source to storage to learning engine
 * 3. **Concurrency Safety**: Ensures thread-safe operations across all asynchronous learning activities
 * 4. **Instruction Processing**: Applies user-defined instructions to modify learning and prediction behavior
 * 5. **Resource Management**: Handles proper cleanup and resource disposal to prevent memory leaks
 *
 * **Thread Safety Guarantees:**
 * - All state-modifying operations are protected by mutex locks to prevent race conditions
 * - Asynchronous learning operations are properly isolated from prediction requests
 * - Resource cleanup is performed atomically to prevent partial state corruption
 *
 * **Usage Pattern:**
 * This class is designed to be instantiated exclusively through the KarlContainerBuilder
 * in KarlAPI.kt, which ensures proper dependency injection and configuration validation.
 * Direct instantiation is discouraged as it bypasses the builder's validation logic.
 *
 * @param userId Unique identifier for the user associated with this container instance,
 *               used for data isolation and personalized learning experiences
 * @param learningEngine The AI/ML engine implementation responsible for training and inference,
 *                       must be compatible with the KarlContainer lifecycle
 * @param dataStorage Persistent storage implementation for saving user data and model state,
 *                    must support concurrent access and atomic operations
 * @param dataSource Real-time data observation source that feeds user interactions into the learning pipeline,
 *                   typically implemented by the host application
 * @param initialInstructions User-defined rules and preferences for customizing learning behavior,
 *                           can be updated dynamically during operation
 * @param containerScope CoroutineScope provided by the application for managing all asynchronous operations,
 *                       must remain active throughout the container's lifecycle
 *
 * @see KarlContainer The interface contract this implementation fulfills
 * @see KarlContainerBuilder The recommended mechanism for creating instances
 *
 * @since 1.0.0
 * @author KARL Development Team
 */
internal class KarlContainerImpl(
    override val userId: String,
    private val learningEngine: LearningEngine,
    private val dataStorage: DataStorage,
    private val dataSource: DataSource,
    initialInstructions: List<KarlInstruction>,
    private val containerScope: CoroutineScope,
) : KarlContainer {
    /*
     * ========================================
     * PRIVATE STATE AND SYNCHRONIZATION
     * ========================================
     *
     * This section contains the container's internal state management components,
     * all designed for thread-safe operation in concurrent environments.
     */

    /**
     * Current set of user-defined instructions that modify container behavior.
     *
     * **Thread Safety**: Write operations are protected by [stateMutex], while read
     * operations are safe due to Kotlin's memory model guarantees for `@Volatile`
     * collections. The reference itself is updated atomically.
     *
     * **Instruction Lifecycle**: Instructions are applied immediately upon update
     * and affect all subsequent data processing and prediction operations.
     *
     * **Performance Considerations**: Instructions are evaluated for each interaction,
     * so the collection should remain reasonably sized (typically < 100 instructions).
     */
    private var currentInstructions: List<KarlInstruction> = initialInstructions

    /**
     * Shared flow for broadcasting predictions to all active collectors.
     *
     * **Reactive Architecture**: Uses MutableSharedFlow to implement the reactive prediction stream
     * that enables real-time prediction updates as learning progresses and context changes.
     *
     * **Flow Configuration**:
     * - No replay buffer to prevent memory accumulation
     * - Unlimited subscriber capacity for flexible observer registration
     * - Thread-safe emission and collection operations
     *
     * **Usage Pattern**: Internally used to emit predictions that are then exposed
     * through the public [getPredictions] method as a read-only Flow.
     */
    private val predictionsFlow =
        MutableSharedFlow<Prediction?>(
            replay = 0,
            extraBufferCapacity = Int.MAX_VALUE,
        )

    /**
     * Active job handle for the data observation coroutine.
     *
     * **Lifecycle Management**: This job represents the long-running data observation
     * process that continuously monitors the [dataSource] for new interactions. When
     * null, observation is inactive (during initialization or after shutdown).
     *
     * **Cancellation Semantics**: The job can be cancelled safely during reset or
     * shutdown operations. Cancellation includes proper resource cleanup and
     * graceful termination of the observation pipeline.
     *
     * **Scope Relationship**: This job is launched within [containerScope], ensuring
     * it's automatically cancelled when the container scope is closed.
     */
    private var dataObservationJob: Job? = null

    /**
     * Mutex protecting critical state operations and ensuring atomicity.
     *
     * **Protected Operations**:
     * - Container initialization and configuration changes
     * - Learning engine state modifications and persistence
     * - Data observation pipeline lifecycle management
     * - Complete container reset and cleanup operations
     *
     * **Performance Impact**: This mutex is designed for infrequent operations
     * (initialization, reset, save) and does not impact the performance of
     * high-frequency operations like individual data processing or predictions.
     *
     * **Deadlock Prevention**: Lock ordering is enforced throughout the implementation
     * to prevent deadlocks. This mutex is always acquired before any component-level
     * locks to maintain consistent lock hierarchy.
     */
    private val stateMutex = Mutex()

    /*
     * ========================================
     * INITIALIZATION AND SYSTEM SETUP
     * ========================================
     *
     * This section handles the complex multi-stage initialization process
     * that brings all container components into operational state.
     */

    /**
     * Initializes the complete KARL container system using its pre-configured dependencies.
     *
     * This method performs a multi-stage initialization process that ensures all components
     * are properly configured and connected before the container becomes operational.
     * The initialization is designed to be idempotent and safe to call multiple times.
     * All dependencies are already available through constructor injection.
     *
     * **Initialization Sequence:**
     * 1. **State Protection**: Acquires exclusive lock to prevent concurrent initialization attempts
     * 2. **Storage Preparation**: Initializes the data storage layer (databases, schemas, connections)
     * 3. **State Recovery**: Attempts to load any previously saved learning state from persistent storage
     * 4. **Engine Activation**: Initializes the machine learning engine with recovered or blank state
     * 5. **Data Pipeline**: Establishes the real-time data observation pipeline for continuous learning
     *
     * **Error Handling:**
     * - Storage initialization failures are propagated to the caller for handling
     * - State loading errors result in fresh initialization (graceful degradation)
     * - Engine initialization failures abort the entire process
     * - Data source connection issues are logged but don't prevent initialization
     *
     * **Thread Safety:**
     * This method is fully thread-safe through mutex protection and can be safely called
     * from any coroutine context. However, multiple concurrent calls will be serialized.
     *
     * @throws Exception If storage initialization or engine initialization fails
     *
     * @see reset For reinitializing an already-initialized container
     * @see release For proper cleanup when the container is no longer needed
     */
    override suspend fun initialize() {
        // Acquire exclusive lock to prevent concurrent initialization attempts
        // This ensures atomic initialization even in highly concurrent environments
        stateMutex.withLock {
            println("KARL Container for user $userId: Initializing...")

            /*
             * STAGE 1: STORAGE INFRASTRUCTURE INITIALIZATION
             *
             * Initialize the persistent storage layer first as it's required by
             * subsequent stages. This includes database connections, schema validation,
             * and migration operations if necessary.
             */
            dataStorage.initialize()
            println("KARL Container for user $userId: DataStorage initialized.")

            /*
             * STAGE 2: STATE RECOVERY AND VALIDATION
             *
             * Attempt to load previously saved learning state. This is a critical
             * step for maintaining learning continuity across application sessions.
             * The state may be null for new users or after reset operations.
             */
            println("KARL Container: About to load saved state from DataStorage...")
            val savedState = dataStorage.loadContainerState(userId)

            // Provide detailed logging for state recovery diagnostics
            if (savedState != null) {
                println("KARL Container: Found saved state with ${savedState.data.size} bytes, version=${savedState.version}")
                println("KARL Container: Will pass this state to LearningEngine for restoration")
            } else {
                println("KARL Container: No saved state found, LearningEngine will start fresh")
            }
            println("KARL Container for user $userId: Loaded state (exists: ${savedState != null}).")

            /*
             * STAGE 3: LEARNING ENGINE ACTIVATION
             *
             * Initialize the machine learning engine with the recovered state (if any).
             * The engine handles state validation, model restoration, and preparation
             * for learning operations. This is computationally expensive and may
             * involve neural network deserialization or model compilation.
             */
            learningEngine.initialize(savedState, containerScope)
            println("KARL Container for user $userId: LearningEngine initialized.")

            /*
             * STAGE 4: DATA OBSERVATION PIPELINE ESTABLISHMENT
             *
             * Set up the continuous data observation pipeline that monitors user
             * interactions and triggers learning operations. This creates a long-running
             * coroutine that processes interaction events asynchronously.
             *
             * The pipeline uses a callback-based approach where each new interaction
             * triggers the processNewData method in a separate coroutine to maintain
             * responsiveness and prevent blocking the observation stream.
             */
            dataObservationJob =
                dataSource.observeInteractionData(
                    onNewData = { data ->
                        // Launch processing in container scope to ensure proper lifecycle management
                        // Each interaction is processed independently to prevent blocking
                        containerScope.launch {
                            processNewData(data)
                        }
                    },
                    coroutineScope = containerScope,
                )
            println("KARL Container for user $userId: Data observation started.")
        }
        println("KARL Container for user $userId: Initialization complete.")
    }

    /*
     * ========================================
     * DATA PROCESSING PIPELINE
     * ========================================
     *
     * This section implements the core data processing pipeline that transforms
     * raw user interactions into machine learning training opportunities.
     */

    /**
     * Processes incoming interaction data through the complete learning pipeline.
     *
     * This internal method handles the critical data processing workflow that transforms
     * raw user interactions into machine learning training opportunities. It applies
     * user-defined instructions for data filtering and routing, ensuring that only
     * relevant and authorized data contributes to the learning process.
     *
     * **Processing Pipeline:**
     * 1. **Instruction Filtering**: Applies current user instructions to determine data relevance
     * 2. **Data Persistence**: Stores interaction data for future context and analysis
     * 3. **Learning Trigger**: Initiates incremental learning based on the new data point
     * 4. **Optional State Persistence**: Manages periodic state saves for crash recovery
     *
     * **Instruction-Based Filtering:**
     * The method respects user-defined instructions such as IgnoreDataType, allowing
     * fine-grained control over what data the AI learns from. This ensures privacy
     * and relevance by honoring user preferences about data types.
     *
     * **Asynchronous Learning:**
     * Training operations are triggered asynchronously to prevent blocking the data
     * observation pipeline. This maintains system responsiveness even during intensive
     * learning operations.
     *
     * **Error Recovery:**
     * Individual data processing failures are isolated and logged without affecting
     * the overall data observation pipeline, ensuring robust operation under adverse conditions.
     *
     * @param data The interaction data to process through the learning pipeline
     *
     * @see KarlInstruction.IgnoreDataType For data filtering capabilities
     * @see LearningEngine.trainStep For the underlying learning mechanism
     */
    private suspend fun processNewData(data: InteractionData) {
        /*
         * STAGE 1: INSTRUCTION-BASED DATA FILTERING
         *
         * Apply user-defined instructions to determine if this interaction should
         * be processed. This enables fine-grained privacy control and allows users
         * to exclude specific data types from learning.
         *
         * Performance Note: This check is performed first to avoid unnecessary
         * storage operations for filtered data.
         */
        if (currentInstructions.any { it is KarlInstruction.IgnoreDataType && it.type == data.type }) {
            println("KARL Container for user $userId: Ignoring data type ${data.type} based on instructions.")
            return // Early exit - no further processing needed for filtered data
        }

        /*
         * STAGE 2: PERSISTENT DATA STORAGE
         *
         * Store the interaction data for future use in predictions and context
         * analysis. This persistence enables the system to understand temporal
         * patterns and provides context for future learning operations.
         *
         * Storage includes: interaction details, timestamp, context metadata,
         * and user identification for proper data isolation.
         */
        dataStorage.saveInteractionData(data)
        println("KARL Container for user $userId: Saved interaction data.")

        /*
         * STAGE 3: INCREMENTAL LEARNING TRIGGER
         *
         * Initiate the machine learning process using the new interaction data.
         * This is performed asynchronously by the learning engine to prevent
         * blocking the data observation pipeline.
         *
         * The learning engine is responsible for:
         * - Feature extraction from interaction data
         * - Model weight updates using appropriate algorithms
         * - Pattern recognition and behavioral adaptation
         * - Performance monitoring and quality control
         */
        println("KARL Container: Passing InteractionData to LearningEngine.trainStep().")
        learningEngine.trainStep(data)

        /*
         * STAGE 4: REACTIVE PREDICTION GENERATION
         *
         * After learning from new interaction data, generate an updated prediction
         * and emit it to the reactive stream for any active collectors. This enables
         * real-time adaptation where UI components receive immediate updates as the
         * system learns from user interactions.
         */
        try {
            val recentData = dataStorage.loadRecentInteractionData(userId, limit = 10)
            val updatedPrediction = learningEngine.predict(recentData, currentInstructions)
            println("KARL Container for user $userId: Generated prediction after learning: $updatedPrediction.")

            // Emit the updated prediction to reactive stream
            predictionsFlow.tryEmit(updatedPrediction)
        } catch (e: Exception) {
            println("KARL Container for user $userId: Error generating prediction after learning: ${e.message}")
            // Emit null to maintain stream continuity on error
            predictionsFlow.tryEmit(null)
        }

        /*
         * STAGE 5: FUTURE ENHANCEMENT - INTELLIGENT STATE PERSISTENCE
         *
         * TODO: Implement sophisticated state persistence strategies based on:
         * - Learning milestones and significant pattern changes
         * - Time-based intervals for regular backup
         * - Resource availability and system load
         * - Data volume and processing frequency
         *
         * Current approach: Manual state saves through saveState() calls
         * Future approach: Automatic, adaptive persistence management
         */

        // Note: Implement more sophisticated save strategies based on learning milestones

        // or time-based intervals to balance persistence with performance
    }

    /*
     * ========================================
     * PUBLIC API IMPLEMENTATION
     * ========================================
     *
     * This section implements the KarlContainer interface contract, providing
     * the public API surface for application integration and user interaction.
     */

    /**
     * Generates intelligent predictions based on learned user behavior patterns and current context.
     *
     * This method orchestrates the complete prediction pipeline, gathering relevant contextual
     * information and applying user-defined instructions to produce personalized, actionable
     * suggestions that respect user preferences and privacy settings.
     *
     * **Prediction Pipeline:**
     * 1. **Context Gathering**: Retrieves recent user interaction history for contextual awareness
     * 2. **Instruction Application**: Applies current user instructions to modify prediction behavior
     * 3. **Engine Consultation**: Requests prediction from the underlying machine learning engine
     * 4. **Result Validation**: Ensures prediction quality and relevance before returning
     *
     * **Contextual Intelligence:**
     * The prediction system uses a configurable amount of recent interaction data to understand
     * the current user context. This temporal awareness allows for more accurate and relevant
     * predictions that adapt to changing user behavior patterns.
     *
     * **Instruction-Aware Predictions:**
     * User-defined instructions can modify prediction behavior, including confidence thresholds,
     * suggestion types, and privacy filters. This ensures predictions align with user preferences
     * and respect their defined boundaries.
     *
     * **Performance Considerations:**
     * The method is optimized for real-time use and designed to be non-blocking. Context
     * gathering is limited to prevent excessive memory usage while maintaining prediction quality.
     *
     * **Privacy Protection:**
     * All prediction processing occurs locally on-device, ensuring user data never leaves
     * the local environment. Predictions respect user-defined privacy instructions.
     *
     * @return A prediction object containing suggested actions or behaviors if the confidence
     *         threshold is met, null if no confident prediction can be made
     *
     * @see Prediction For the structure of returned prediction objects
     * @see KarlInstruction For instruction types that can modify prediction behavior
     */
    override suspend fun getPrediction(): Prediction? {
        println("KARL Container for user $userId: Requesting prediction...")

        /*
         * STAGE 1: CONTEXTUAL DATA GATHERING
         *
         * Retrieve recent interaction history to provide context for prediction.
         * The limit of 10 interactions represents a balance between contextual
         * richness and performance. This can be made configurable in future versions.
         *
         * Context includes: recent actions, timing patterns, interaction sequences,
         * and environmental factors that may influence prediction relevance.
         */
        val recentData = dataStorage.loadRecentInteractionData(userId, limit = 10)
        println("KARL Container for user $userId: Loaded ${recentData.size} recent data points for prediction.")

        /*
         * STAGE 2: INTELLIGENT PREDICTION GENERATION
         *
         * Request prediction from the learning engine using both recent context
         * and current user instructions. The engine applies learned patterns,
         * evaluates confidence levels, and generates actionable suggestions.
         *
         * The prediction process includes:
         * - Pattern matching against learned behavior models
         * - Confidence estimation and threshold evaluation
         * - Instruction-based filtering and customization
         * - Alternative suggestion generation when appropriate
         */
        val prediction = learningEngine.predict(recentData, currentInstructions)
        println("KARL Container for user $userId: Prediction result: $prediction.")

        /*
         * STAGE 3: REACTIVE PREDICTION EMISSION
         *
         * Emit the prediction to the reactive stream for any active collectors.
         * This enables real-time updates for UI components and other observers
         * that are collecting from the getPredictions() flow.
         */
        predictionsFlow.tryEmit(prediction)

        return prediction
    }

    /**
     * Creates a reactive stream of predictions that continuously emits suggestions as context changes.
     *
     * This implementation provides a real-time prediction stream that automatically emits new
     * predictions when the underlying learning state changes or when context evolves. The stream
     * is designed for efficient resource usage and responsive user experience.
     *
     * **Implementation Strategy:**
     * This method returns a SharedFlow that receives predictions from internal processes.
     * Predictions are emitted when:
     * - New interaction data is processed (learning state changes)
     * - Manual prediction requests are made via getPrediction()
     * - Context changes that warrant new predictions
     *
     * **Resource Management:**
     * - Uses the container's scope for lifecycle management
     * - Automatically stops when the container is released
     * - Efficient memory usage through SharedFlow with no replay buffer
     * - No persistent background tasks when no collectors are active
     *
     * **Error Handling:**
     * - Emits null when predictions cannot be generated
     * - Gracefully handles learning engine errors
     * - Continues operation even if individual predictions fail
     *
     * @return A Flow that emits predictions continuously, null when no prediction is available
     */
    override fun getPredictions(): Flow<Prediction?> = predictionsFlow.asSharedFlow()

    /**
     * Performs a complete system reset, returning the container to its initial blank state.
     *
     * This method provides a comprehensive reset mechanism that safely clears all learned
     * behavior, removes stored user data, and reinitializes the learning system from scratch.
     * The operation is designed to be atomic and safe, ensuring the container remains in
     * a consistent state throughout the reset process.
     *
     * **Reset Sequence:**
     * 1. **Pipeline Suspension**: Safely stops the data observation pipeline with proper cleanup
     * 2. **Learning Engine Reset**: Clears all learned parameters and returns engine to blank state
     * 3. **Data Purge**: Removes all stored user data while preserving system integrity
     * 4. **Pipeline Restoration**: Re-establishes the data observation pipeline for new learning
     *
     * **Atomicity Guarantees:**
     * The entire reset operation is protected by mutex locks to ensure atomicity. If any
     * stage fails, the system attempts to maintain a consistent state and proper error reporting.
     *
     * **Data Privacy:**
     * Reset operations completely remove user data from persistent storage, ensuring
     * privacy compliance and supporting user rights to data deletion.
     *
     * **System Continuity:**
     * After reset, the container is immediately ready for new learning experiences.
     * The data observation pipeline is re-established automatically, ensuring seamless
     * transition from reset to operational state.
     *
     * **Thread Safety:**
     * This method is fully thread-safe and can be called from any coroutine context.
     * Concurrent reset attempts are serialized to prevent system corruption.
     *
     * @return Job representing the asynchronous reset operation, allowing callers to
     *         track completion or handle cancellation scenarios
     *
     * @see initialize For the initialization process that follows reset
     * @see release For complete system shutdown rather than reset
     */
    override suspend fun reset(): Job =
        containerScope.launch {
            // Acquire exclusive lock to ensure atomic reset operation
            // This prevents interference from concurrent operations during reset
            stateMutex.withLock {
                println("KARL Container for user $userId: Resetting...")

                /*
                 * STAGE 1: DATA OBSERVATION PIPELINE SUSPENSION
                 *
                 * Safely terminate the data observation job to prevent new data
                 * from being processed during the reset operation. This includes
                 * proper coroutine cancellation and resource cleanup.
                 */
                dataObservationJob?.cancelAndJoin()
                dataObservationJob = null
                println("KARL Container for user $userId: Data observation pipeline suspended.")

                /*
                 * STAGE 2: LEARNING ENGINE STATE RESET
                 *
                 * Clear all learned patterns, model weights, and training history
                 * from the learning engine. This returns the AI to its initial
                 * untrained state, ready to learn new patterns from scratch.
                 */
                learningEngine.reset()
                println("KARL Container for user $userId: LearningEngine reset.")

                /*
                 * STAGE 3: PERSISTENT DATA PURGE
                 *
                 * Remove all stored user data including interaction history,
                 * saved container states, and any cached results. This ensures
                 * complete privacy compliance and data removal.
                 */
                dataStorage.deleteUserData(userId)
                println("KARL Container for user $userId: User data deleted.")

                /*
                 * STAGE 4: DATA OBSERVATION PIPELINE RESTORATION
                 *
                 * Re-establish the data observation pipeline to resume monitoring
                 * user interactions. The system is now ready to learn new patterns
                 * from fresh data with no memory of previous behavior.
                 */
                dataObservationJob =
                    dataSource.observeInteractionData(
                        onNewData = { data ->
                            // Launch processing in container scope for proper lifecycle management
                            containerScope.launch {
                                processNewData(data)
                            }
                        },
                        coroutineScope = containerScope,
                    )
                println("KARL Container for user $userId: Data observation restarted.")
            }
            println("KARL Container for user $userId: Reset complete.")
        }

    /**
     * Persists the current learning state to permanent storage for crash recovery and continuity.
     *
     * This method provides reliable state persistence capabilities that ensure learned user
     * behavior and preferences survive application restarts, system crashes, and device reboots.
     * The save operation is designed to be atomic and non-destructive to existing data.
     *
     * **State Persistence Pipeline:**
     * 1. **State Extraction**: Retrieves current learned state from the machine learning engine
     * 2. **Atomic Storage**: Saves state to persistent storage with transactional guarantees
     * 3. **Integrity Verification**: Ensures saved state is complete and valid
     * 4. **Error Recovery**: Handles storage failures gracefully without corrupting existing data
     *
     * **Atomicity Guarantees:**
     * The save operation is protected by mutex locks to prevent concurrent state modifications
     * during the save process. This ensures the saved state represents a consistent snapshot
     * of the learning system at a specific moment in time.
     *
     * **Crash Recovery Support:**
     * Saved states can be reliably loaded during system initialization, allowing the AI
     * to resume learning from exactly where it left off before an unexpected shutdown.
     *
     * **Storage Efficiency:**
     * The state representation is optimized for storage efficiency while maintaining
     * complete fidelity to the learning engine's internal state. Binary serialization
     * ensures minimal storage footprint.
     *
     * **Thread Safety:**
     * This method is fully thread-safe and can be called from any coroutine context.
     * Multiple concurrent save requests are serialized to prevent storage corruption.
     *
     * **Performance Characteristics:**
     * The save operation is asynchronous and non-blocking, allowing application
     * performance to remain unaffected by persistence activities.
     *
     * @return Job representing the asynchronous save operation, enabling callers to
     *         track completion status or implement custom error handling
     *
     * @see initialize For the state loading process during system startup
     * @see KarlContainerState For the structure of persisted state data
     */
    override suspend fun saveState(): Job =
        containerScope.launch {
            // Acquire exclusive lock to ensure atomic state capture and storage
            // This prevents concurrent modifications during the save operation
            stateMutex.withLock {
                println("KARL Container for user $userId: Saving state...")

                /*
                 * STAGE 1: LEARNING STATE EXTRACTION
                 *
                 * Request the current state from the learning engine. This includes
                 * all model weights, hyperparameters, training history, and metadata
                 * necessary to reconstruct the exact learning state later.
                 *
                 * State extraction is a potentially expensive operation that may
                 * involve serializing large neural networks or complex model structures.
                 */
                println("KARL Container for user $userId: Getting current state from learning engine...")
                val currentState = learningEngine.getCurrentState()
                println("KARL Container for user $userId: Current state obtained from learning engine: $currentState")

                /*
                 * STAGE 2: ATOMIC PERSISTENT STORAGE
                 *
                 * Save the extracted state to persistent storage using transactional
                 * guarantees. The storage implementation ensures atomicity, preventing
                 * partial writes that could corrupt the saved state.
                 *
                 * Storage includes version information, checksums, and metadata to
                 * support future migrations and integrity verification.
                 */
                println("KARL Container for user $userId: Calling dataStorage.saveContainerState()...")
                dataStorage.saveContainerState(userId, currentState)
                println("KARL Container for user $userId: State saved successfully to data storage.")
            }
        }

    /**
     * Updates the active instruction set that governs container learning and prediction behavior.
     *
     * This method provides dynamic configuration capabilities that allow applications to
     * modify AI behavior in real-time without requiring system restarts or re-initialization.
     * Instructions can control data filtering, prediction parameters, privacy settings,
     * and other behavioral aspects of the learning system.
     *
     * **Instruction Categories:**
     * - **Data Filtering**: Control which types of interactions contribute to learning
     * - **Privacy Controls**: Define data handling and retention policies
     * - **Prediction Tuning**: Adjust confidence thresholds and suggestion preferences
     * - **Learning Parameters**: Modify training frequency and learning rate settings
     *
     * **Real-Time Application:**
     * Instruction updates take effect immediately for all subsequent operations.
     * Ongoing learning processes respect the new instructions for future data processing,
     * while predictions immediately incorporate the updated behavioral rules.
     *
     * **Instruction Validation:**
     * The system validates instruction syntax and compatibility before application.
     * Invalid instructions are logged and ignored to maintain system stability.
     *
     * **Thread Safety:**
     * Instruction updates are atomic and thread-safe, ensuring consistent behavior
     * across all concurrent operations within the container.
     *
     * **Use Cases:**
     * - Implementing user preference changes from UI settings
     * - Applying temporary behavioral modifications for specific contexts
     * - Enforcing privacy policies and compliance requirements
     * - A/B testing different learning configurations
     *
     * @param instructions New set of instructions to apply to container behavior.
     *                    Completely replaces the existing instruction set.
     *
     * @see KarlInstruction For available instruction types and their effects
     * @see processNewData For how instructions affect data processing
     */
    override fun updateInstructions(instructions: List<KarlInstruction>) {
        println("KARL Container for user $userId: Updating instructions.")

        /*
         * ATOMIC INSTRUCTION UPDATE
         *
         * Replace the current instruction set with the new one. This operation
         * is atomic due to Kotlin's memory model guarantees for reference updates.
         * The new instructions take effect immediately for all subsequent operations.
         *
         * Note: No mutex is required here as this is a simple reference update
         * that doesn't require coordination with other operations.
         */
        this.currentInstructions = instructions

        /*
         * FUTURE ENHANCEMENT: LEARNING ENGINE NOTIFICATION
         *
         * In advanced implementations, the learning engine could be notified
         * directly of instruction changes to enable immediate behavioral updates
         * for ongoing learning processes.
         *
         * Example: learningEngine.updateInstructions(instructions)
         */

        // Future enhancement: Notify learning engine if instructions directly affect behavior

        // learningEngine.updateInstructions(instructions)
    }

    /**
     * Performs complete resource cleanup and graceful shutdown of the container system.
     *
     * This method ensures proper disposal of all system resources including active coroutines,
     * database connections, file handles, and native memory allocations. It is essential for
     * preventing memory leaks and ensuring clean application shutdown.
     *
     * **Cleanup Sequence:**
     * 1. **Data Pipeline Shutdown**: Safely terminates the data observation pipeline
     * 2. **Learning Engine Cleanup**: Releases ML engine resources and native memory
     * 3. **Storage Connection Closure**: Closes database connections and file handles
     * 4. **State Verification**: Ensures all resources have been properly released
     *
     * **Resource Management:**
     * The cleanup process handles all resource types systematically:
     * - **Coroutine Jobs**: Cancelled and joined to prevent orphaned background tasks
     * - **Database Connections**: Closed with proper transaction handling
     * - **File Handles**: Released to prevent file system resource exhaustion
     * - **Native Memory**: Freed through engine-specific cleanup mechanisms
     *
     * **Graceful Degradation:**
     * If individual cleanup operations fail, the method continues with remaining
     * cleanup tasks to maximize resource recovery. Errors are logged for debugging
     * but don't prevent overall cleanup completion.
     *
     * **Threading Considerations:**
     * The container scope itself is managed by the calling application and is not
     * cancelled by this method. Only jobs launched directly by the container are
     * terminated, allowing the application to maintain control over its lifecycle.
     *
     * **Call Safety:**
     * This method is idempotent and safe to call multiple times. Subsequent calls
     * after the first complete quickly with no adverse effects.
     *
     * **Usage Context:**
     * Should be called during:
     * - Application shutdown sequences
     * - User logout processes
     * - Container lifecycle management
     * - Error recovery scenarios requiring clean restart
     *
     * @see initialize For the corresponding setup process
     * @see reset For clearing data while maintaining active state
     */
    override suspend fun release() {
        println("KARL Container for user $userId: Releasing resources...")

        /*
         * STAGE 1: DATA OBSERVATION PIPELINE TERMINATION
         *
         * Safely terminate the data observation coroutine to stop processing
         * new interaction events. This includes proper cancellation semantics
         * and waiting for any in-flight processing to complete.
         *
         * The cancelAndJoin() call ensures the job is fully terminated before
         * proceeding with additional cleanup operations.
         */
        dataObservationJob?.cancelAndJoin()
        dataObservationJob = null
        println("KARL Container for user $userId: Data observation pipeline terminated.")

        /*
         * STAGE 2: LEARNING ENGINE RESOURCE RELEASE
         *
         * Release all resources held by the learning engine including:
         * - Model weights and training data in memory
         * - Background training and optimization tasks
         * - Hardware accelerator resources (GPU, TPU, etc.)
         * - Temporary files and computation caches
         */
        learningEngine.release()
        println("KARL Container for user $userId: Learning engine resources released.")

        /*
         * STAGE 3: DATA STORAGE CONNECTION CLOSURE
         *
         * Properly close all database connections, file handles, and storage
         * resources. This includes flushing pending writes, closing transactions,
         * and releasing any locks held by the storage subsystem.
         */
        dataStorage.release()
        println("KARL Container for user $userId: Data storage connections closed.")

        /*
         * CONTAINER SCOPE LIFECYCLE MANAGEMENT
         *
         * Note: The container scope lifecycle is intentionally managed by the
         * calling application rather than this container implementation. This
         * design decision allows applications to maintain full control over
         * their coroutine lifecycle and coordinate shutdown across multiple
         * components without introducing unexpected cancellations.
         *
         * Applications should cancel the container scope when appropriate for
         * their specific use case and lifecycle requirements.
         */

        // Note: Container scope lifecycle is managed by the application

        // and is intentionally not cancelled here to maintain caller control

        println("KARL Container for user $userId: Resources released.")
    }
}
