package container

import api.KarlContainer
import api.LearningEngine
import com.karl.core.models.DataSource // Import the interfaces
import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlInstruction
import com.karl.core.models.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin // For releasing resources safely
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex // To prevent concurrent operations on state
import kotlinx.coroutines.sync.withLock

/**
 * Concrete implementation of the KarlContainer interface.
 * This class orchestrates the LearningEngine, DataStorage, and DataSource
 * for a specific user's AI container.
 *
 * This class is intended to be created via the KarlContainerBuilder in KarlAPI.kt.
 */
internal class KarlContainerImpl( // Use internal as it's not part of the public API surface directly
    override val userId: String, // User ID is part of the public interface
    private val learningEngine: LearningEngine,
    private val dataStorage: DataStorage,
    private val dataSource: DataSource,
    initialInstructions: List<KarlInstruction>,
    private val containerScope: CoroutineScope, // The scope provided by the application
) : KarlContainer {
    // State for the container
    private var currentInstructions: List<KarlInstruction> = initialInstructions
    private var dataObservationJob: Job? = null // Job to observe data from DataSource
    private val stateMutex = Mutex() // Mutex to protect state loading/saving/resetting

    // --- Initialization ---

    /**
     * Initializes the container. Loads state from storage, initializes the learning engine,
     * and starts observing data from the data source.
     * This *must* be called after creating the container via the builder.
     */
    override suspend fun initialize(
        learningEngine: LearningEngine, // We ignore these parameters here as they are injected via constructor
        dataStorage: DataStorage, // This override structure is slightly awkward due to the interface design,
        dataSource: DataSource, // but works. Alternatively, initialize could take no args if dependencies are constructor injected.
        instructions: List<KarlInstruction>, // Let's update instructions here during init too
        coroutineScope: CoroutineScope, // And ignore this as the constructor scope is primary
    ) {
        stateMutex.withLock { // Ensure only one init/reset/save operation happens at a time
            println("KARL Container for user $userId: Initializing...")
            this.currentInstructions = instructions // Update instructions during initialization

            // 1. Initialize Storage
            dataStorage.initialize()
            println("KARL Container for user $userId: DataStorage initialized.")

            // 2. Load Learning Engine State
            val savedState = dataStorage.loadContainerState(userId)
            println("KARL Container for user $userId: Loaded state (exists: ${savedState != null}).")

            // 3. Initialize Learning Engine with state
            learningEngine.initialize(savedState, containerScope) // Pass the app-provided scope to the engine
            println("KARL Container for user $userId: LearningEngine initialized.")

            // 4. Start Data Observation
            // We pass a callback that the DataSource will call for new data.
            // The callback launches trainStep within the container's scope.
            dataObservationJob =
                dataSource.observeInteractionData(
                    onNewData = { data ->
                        // Launch trainStep as a coroutine within the container's scope
                        // This ensures training doesn't block the data source observation
                        // and is managed by the container's lifecycle.
                        containerScope.launch {
                            processNewData(data) // Internal function to handle data processing
                        }
                    },
                    coroutineScope = containerScope, // Pass the app-provided scope to the data source observer
                )
            println("KARL Container for user $userId: Data observation started.")
        }
        println("KARL Container for user $userId: Initialization complete.")
    }

    // Internal helper function to process new incoming data
    private suspend fun processNewData(data: InteractionData) {
        // Apply instructions to data before processing/training (example: filtering)
        if (currentInstructions.any { it is KarlInstruction.IgnoreDataType && it.type == data.type }) {
            println("KARL Container for user $userId: Ignoring data type ${data.type} based on instructions.")
            return // Ignore this data point
        }

        // 1. Optionally save data (if storage needs to store raw interactions for context loading)
        // If the engine directly uses the DataStorage to load context, saving here is needed.
        dataStorage.saveInteractionData(data)
        println("KARL Container for user $userId: Saved interaction data.")

        // 2. Trigger learning step in the engine
        println("KARL Container: Passing InteractionData to LearningEngine.trainStep().")
        // The trainStep returns a Job, but we don't strictly need to manage it here
        // unless we wanted to implement queueing or priority training.
        // The LearningEngine should manage its own background training tasks.
        learningEngine.trainStep(data)

        // 3. Periodically save state (Optional, depends on desired save frequency)
        // A better approach might be a separate job that saves state periodically
        // or tying saves to significant learning milestones.
        // Example: saveState().join() // Wait for save (might block!) - use launch instead if not blocking init
        // containerScope.launch { saveState().join() } // Launch save asynchronously
    }

    // --- Public API Methods (Implementing KarlContainer interface) ---

    /**
     * Triggers a prediction from the learning engine.
     * Gathers necessary context data and applies current instructions.
     */
    override suspend fun getPrediction(): Prediction? {
        println("KARL Container for user $userId: Requesting prediction...")
        // Gather recent data from storage as context for the prediction
        // The limit (e.g., 10) depends on what the LearningEngine model expects/uses
        val recentData = dataStorage.loadRecentInteractionData(userId, limit = 10)
        println("KARL Container for user $userId: Loaded ${recentData.size} recent data points for prediction.")

        // Get prediction from the engine, passing recent data and current instructions
        val prediction = learningEngine.predict(recentData, currentInstructions)
        println("KARL Container for user $userId: Prediction result: $prediction.")
        return prediction
    }

    /**
     * Resets the container's learned state and deletes associated user data.
     * Returns a Job representing the asynchronous reset operation.
     */
    override suspend fun reset(): Job =
        containerScope.launch {
            stateMutex.withLock { // Ensure no other state ops interfere
                println("KARL Container for user $userId: Resetting...")
                // Stop current data observation temporarily
                dataObservationJob?.cancelAndJoin() // Wait for observer to stop cleanly
                dataObservationJob = null // Clear the old job

                // 1. Reset Learning Engine
                learningEngine.reset()
                println("KARL Container for user $userId: LearningEngine reset.")

                // 2. Delete User Data from Storage
                dataStorage.deleteUserData(userId)
                println("KARL Container for user $userId: User data deleted.")

                // 3. Restart Data Observation (Engine is now blank, ready to learn anew)
                dataObservationJob =
                    dataSource.observeInteractionData(
                        onNewData = { data ->
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
     * Saves the current state of the container's learning engine.
     * Returns a Job representing the asynchronous save operation.
     */
    override suspend fun saveState(): Job =
        containerScope.launch {
            stateMutex.withLock { // Ensure no other state ops interfere
                println("KARL Container for user $userId: Saving state...")
                // Get the current state from the learning engine
                val currentState = learningEngine.getCurrentState()

                // Save the state using the data storage
                dataStorage.saveContainerState(userId, currentState)
                println("KARL Container for user $userId: State saved.")
            }
        }

    /**
     * Updates the user-defined instructions for the container.
     * This might influence how the AI learns or predicts.
     */
    override fun updateInstructions(instructions: List<KarlInstruction>) {
        println("KARL Container for user $userId: Updating instructions.")
        this.currentInstructions = instructions
        // Optionally, notify the learning engine if instructions directly affect its behavior
        // learningEngine.updateInstructions(instructions) // Would need this method in LearningEngine interface
    }

    /**
     * Releases resources held by the container and its dependencies.
     * Should be called when the container is no longer needed (e.g., app exit).
     */
    override suspend fun release() {
        println("KARL Container for user $userId: Releasing resources...")
        // Cancel data observation job
        dataObservationJob?.cancelAndJoin()
        dataObservationJob = null

        // Release dependencies
        learningEngine.release()
        dataStorage.release()

        // Note: The containerScope itself is expected to be managed and cancelled by the caller (the application).
        // We don't cancel containerScope here, only jobs launched directly by the container's logic.

        println("KARL Container for user $userId: Resources released.")
    }
}
