// karl-core/src/commonMain/kotlin/com/karl/core/learning/LearningEngine.kt

package com.karl.core.learning

import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerState
import com.karl.core.models.KarlInstruction
import com.karl.core.models.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Defines the contract for the AI model's learning and inference capabilities.
 *
 * This interface abstracts the underlying machine learning framework (e.g., KotlinDL, a custom model)
 * from the `KarlContainer`. Implementations of this interface are responsible for managing the
 * ML model's lifecycle, training, prediction, and state serialization.
 */
interface LearningEngine {
    /**
     * Initializes the learning engine. This method should prepare the engine for operation,
     * which includes creating a new model or loading an existing one from a saved state.
     *
     * @param state The previously saved [KarlContainerState] to load, or null if starting fresh.
     * @param coroutineScope A [CoroutineScope] provided by the container for launching
     *                       long-running or asynchronous tasks (like background training).
     */
    suspend fun initialize(
        state: KarlContainerState?,
        coroutineScope: CoroutineScope,
    )

    /**
     * Performs a single, incremental training step based on new interaction data.
     *
     * This method should be non-blocking. It should ideally offload the actual training
     * computation to a background thread using the provided `engineScope` from `initialize`.
     *
     * @param data The new [InteractionData] to learn from.
     * @return A [Job] representing the asynchronous learning task, allowing the caller
     *         to optionally track its completion or handle cancellation.
     */
    fun trainStep(data: InteractionData): Job

    /**
     * Makes a prediction or suggestion based on the current learned state and optional context.
     *
     * This method should be designed to be fast and efficient for on-device execution,
     * as it might be called frequently from the application's UI thread (within a coroutine).
     *
     * @param contextData Optional list of recent [InteractionData] to provide context for the prediction.
     * @param instructions A list of user-defined [KarlInstruction]s that can modify the prediction
     *                     behavior (e.g., setting a confidence threshold).
     * @return A [Prediction] object if a confident suggestion can be made, or null otherwise.
     */
    suspend fun predict(
        contextData: List<InteractionData> = emptyList(),
        instructions: List<KarlInstruction> = emptyList(),
    ): Prediction?

    /**
     * Retrieves the current, serializable state of the learning model for persistence.
     *
     * The implementation is responsible for converting its internal model representation
     * (e.g., model architecture, weights, parameters) into a `ByteArray`.
     *
     * @return The current [KarlContainerState] of the model.
     */
    suspend fun getCurrentState(): KarlContainerState

    /**
     * Resets the learning engine to its initial, untrained "blank slate" state.
     * This should clear any learned parameters and reset the internal model.
     */
    suspend fun reset()

    /**
     * Releases any resources held by the engine, such as native memory used by an ML backend
     * or active coroutine jobs. This should be called when the container is being shut down.
     */
    suspend fun release()

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
 * A data class to encapsulate metrics about the learning engine's progress.
 *
 * @property interactionCount The total number of interactions processed by `trainStep`.
 * @property progressEstimate A normalized value (0.0 to 1.0) representing the estimated
 *                            "maturity" of the model, based on the engine's internal logic.
 * @property customMetrics A map for any other engine-specific metrics (e.g., average confidence, loss).
 */
data class LearningInsights(
    val interactionCount: Long,
    val progressEstimate: Float,
    val customMetrics: Map<String, Any> = emptyMap(),
)
