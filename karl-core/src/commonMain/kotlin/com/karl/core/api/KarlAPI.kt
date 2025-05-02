package com.karl.core.api

import api.KarlContainer
import api.LearningEngine
import container.KarlContainerImpl // We'll implement this class next
import com.karl.core.data.KarlInstruction
import kotlinx.coroutines.CoroutineScope

/**
 * The main entry point for interacting with the KARL library.
 * Use the `Karl.forUser()` builder to create and configure a KarlContainer instance
 * for a specific user.
 */
object Karl {

    /**
     * Starts the configuration process for a new or existing KarlContainer for a specific user.
     *
     * @param userId The unique identifier for the user. KARL data (state, interactions) is
     *               scoped to this user ID.
     * @return A builder to configure the KarlContainer.
     */
    fun forUser(userId: String): KarlContainerBuilder {
        return KarlContainerBuilder(userId)
    }
}

/**
 * Builder class for configuring and creating a KarlContainer.
 *
 * To build a container, you MUST provide implementations for:
 * 1. LearningEngine (e.g., from karl-kldl module)
 * 2. DataStorage (e.g., from karl-sqldelight module)
 * 3. DataSource (Implemented by your application to feed data to KARL)
 * 4. A CoroutineScope (Managed by your application, e.g., from a ViewModel or lifecycle)
 *
 * After calling `build()`, you MUST call `container.initialize()` to load state and start processes.
 */
class KarlContainerBuilder internal constructor(private val userId: String) { // Internal constructor hides the builder creation detail

    private var learningEngine: LearningEngine? = null
    private var dataStorage: DataStorage? = null
    private var dataSource: DataSource? = null
    private var instructions: List<KarlInstruction> = emptyList()
    private var coroutineScope: CoroutineScope? = null

    /**
     * Sets the [LearningEngine] implementation for the container.
     * This engine handles the AI model training and prediction logic.
     * @param engine The LearningEngine instance (e.g., created from the karl-kldl module).
     */
    fun withLearningEngine(engine: LearningEngine): KarlContainerBuilder = apply { this.learningEngine = engine }

    /**
     * Sets the [DataStorage] implementation for the container.
     * This handles persistence of the AI state and interaction data.
     * @param storage The DataStorage instance (e.g., created from the karl-sqldelight module).
     */
    fun withDataStorage(storage: DataStorage): KarlContainerBuilder = apply { this.dataStorage = storage }

    /**
     * Sets the [DataSource] implementation provided by your application.
     * This is how your application feeds user interaction data into KARL.
     * @param source Your application's implementation of DataSource.
     */
    fun withDataSource(source: DataSource): KarlContainerBuilder = apply { this.dataSource = source }

    /**
     * Sets the initial user-defined instructions for the container.
     * These instructions influence how KARL processes data or makes predictions.
     * Can be updated later via `KarlContainer.updateInstructions()`.
     * @param instructions A list of initial KarlInstruction objects. Defaults to empty list.
     */
    fun withInstructions(instructions: List<KarlInstruction>): KarlContainerBuilder = apply { this.instructions = instructions }

    /**
     * Sets the [CoroutineScope] that the KarlContainer will use for its asynchronous operations.
     * This scope should be managed by your application and ideally tied to a lifecycle
     * (e.g., a ViewModel's scope, an Activity/Fragment lifecycle scope) so that KARL's background
     * tasks are cancelled when the relevant component is destroyed.
     * @param scope The CoroutineScope managed by your application.
     */
    fun withCoroutineScope(scope: CoroutineScope): KarlContainerBuilder = apply { this.coroutineScope = scope }


    /**
     * Builds and returns a new instance of [api.KarlContainer].
     *
     * The returned container is NOT initialized yet. You MUST call `container.initialize()`
     * afterwards within the provided [CoroutineScope] to load the AI state and start processes.
     *
     * @throws IllegalStateException if LearningEngine, DataStorage, DataSource, or CoroutineScope are not provided.
     * @return A new instance of KarlContainer.
     */
    fun build(): KarlContainer {
        // Perform validation to ensure all required dependencies are set
        val engine = learningEngine ?: throw IllegalStateException("LearningEngine must be provided using withLearningEngine().")
        val storage = dataStorage ?: throw IllegalStateException("DataStorage must be provided using withDataStorage().")
        val source = dataSource ?: throw IllegalStateException("DataSource must be provided using withDataSource().")
        val scope = coroutineScope ?: throw IllegalStateException("CoroutineScope must be provided using withCoroutineScope().")

        // Return the concrete implementation, which lives within the core module
        // We pass all configured dependencies and the user ID to its constructor
        return KarlContainerImpl(
            userId = userId,
            learningEngine = engine,
            dataStorage = storage,
            dataSource = source,
            initialInstructions = instructions,
            containerScope = scope // Pass the provided scope to the implementation
        )
    }
}


// Re-export interfaces here for easy access from the API entry point
// This isn't strictly necessary if developers import KarlCoreInterfaces.kt directly,
// but can make the public API cleaner.
// import com.karl.core.api.KarlCoreInterfaces.* // Alternative: import all

//typealias LearningEngine = LearningEngine
typealias DataStorage = com.karl.core.data.DataStorage
typealias DataSource = com.karl.core.data.DataSource
//typealias KarlContainer = KarlContainer