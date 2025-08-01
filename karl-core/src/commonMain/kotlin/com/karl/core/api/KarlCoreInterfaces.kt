package api

// Import models and coroutines types needed by the remaining interfaces
import com.karl.core.learning.LearningInsights
import com.karl.core.models.DataSource // Now import from the data package
import com.karl.core.models.DataStorage // Now import from the data package
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerState
import com.karl.core.models.KarlInstruction
import com.karl.core.models.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Represents the engine responsible for training and inference.
 * Implementations will wrap specific AI/ML libraries (like KotlinDL).
 * This interface remains in the API package as it's a core functional piece
 * orchestrated by the KarlContainer.
 */
interface LearningEngine {
    /**
     * Initializes the engine, potentially loading a model from state.
     * @param state The initial state to load, or null for a new, blank model.
     * @param coroutineScope A CoroutineScope for managing asynchronous tasks within the engine.
     */
    suspend fun initialize(
        state: KarlContainerState?,
        coroutineScope: CoroutineScope,
    )

    /**
     * Performs a single incremental training step based on new interaction data.
     * This method should be non-blocking and potentially run training in the background.
     * @param data The new interaction data to learn from.
     * @return A Job representing the asynchronous learning task.
     */
    fun trainStep(data: InteractionData): Job // Use full path or import InteractionData

    /**
     * Makes a prediction or suggestion based on the current learned state and optionally recent data.
     * This method should be fast and non-blocking.
     * @param contextData Optional recent data or context needed for prediction.
     * @param instructions User-defined instructions to consider during prediction.
     * @return A Prediction object, or null if no suggestion can be made.
     */
    suspend fun predict(
        contextData: List<InteractionData> = emptyList(),
        instructions: List<KarlInstruction> = emptyList(),
    ): Prediction?

    /**
     * Gets the current state of the learning model for persistence.
     */
    suspend fun getCurrentState(): KarlContainerState

    /**
     * Resets the learning engine to a blank state.
     */
    suspend fun reset()

    /**
     * Releases any resources held by the engine.
     */
    suspend fun release()

    /**
     * Gets a human-readable description of the model architecture.
     * This can be used to display model information in the UI.
     *
     * @return A string describing the model architecture (e.g., "MLP(4x8x3)")
     */
    fun getModelArchitectureName(): String {
        return "Unknown Architecture"
    }

    /**
     * (Optional but Recommended) Provides insights into the current learning progress.
     * This can be used to power an "AI Maturity Meter" in the UI.
     *
     * @return A data class containing various learning metrics.
     */
    suspend fun getLearningInsights(): LearningInsights {
        // Provide a default implementation for engines that don't track detailed insights.
        return LearningInsights(interactionCount = 0, progressEstimate = 0.0f)
    }
}

/**
 * The main entry point and orchestrator for a user's AI container.
 * Manages the lifecycle, integrates learning, storage, and data source.
 * This interface remains in the API package as it's the primary public contract.
 */
interface KarlContainer {
    val userId: String

    /**
     * Initializes the container, loading state and starting data observation.
     * @param learningEngine The LearningEngine implementation to use.
     * @param dataStorage The DataStorage implementation to use.
     * @param dataSource The DataSource implementation provided by the application.
     * @param instructions User-defined instructions for this container instance.
     * @param coroutineScope A CoroutineScope provided by the application for managing container tasks.
     */
    suspend fun initialize(
        learningEngine: LearningEngine,
        dataStorage: DataStorage,
        dataSource: DataSource,
        instructions: List<KarlInstruction> = emptyList(),
        coroutineScope: CoroutineScope,
    )

    /**
     * Triggers a prediction based on current state and recent data.
     * @return A Prediction object, or null.
     */
    suspend fun getPrediction(): Prediction?

    /**
     * Resets the container's learned state and deletes associated user data.
     * Returns a Job representing the asynchronous reset operation.
     */
    suspend fun reset(): Job

    /**
     * Saves the current state of the container. Should be called periodically or on app exit.
     */
    suspend fun saveState(): Job

    /**
     * Updates the user-defined instructions for the container.
     */
    fun updateInstructions(instructions: List<KarlInstruction>)

    /**
     * Releases resources held by the container and its dependencies.
     * Should be called when the container is no longer needed (e.g., app exit).
     */
    suspend fun release()
}
