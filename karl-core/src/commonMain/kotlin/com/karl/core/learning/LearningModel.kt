// karl-core/src/commonMain/kotlin/com/karl/core/learning/LearningModel.kt

package com.karl.core.learning

import com.karl.core.models.KarlContainerState

/**
 * A generic interface representing a machine learning model within a LearningEngine.
 *
 * This abstraction allows a LearningEngine implementation (like `:karl-kldl`) to be
 * designed to work with different model architectures (e.g., MLP, RNN, Transformer)
 * that conform to this contract. It defines the essential operations of a model:
 * prediction, training, and state management.
 */
interface LearningModel {

    /**
     * Initializes the model's internal structure and parameters.
     * This is where the actual model graph (e.g., a KotlinDL Sequential model) would be built.
     */
    fun initialize()

    /**
     * Performs inference on a given input feature vector.
     *
     * @param input A numerical representation (e.g., FloatArray) of the input features.
     * @return A numerical representation (e.g., FloatArray) of the model's output,
     *         typically a vector of probabilities or scores.
     */
    fun predict(input: FloatArray): FloatArray

    /**
     * Performs a single training step on the model.
     *
     * @param input A numerical representation of the training input features.
     * @param target A numerical representation of the expected output (the label).
     */
    fun train(input: FloatArray, target: FloatArray)

    /**
     * Loads the model's learned parameters (e.g., weights) from a serialized state.
     *
     * @param state The [KarlContainerState] containing the previously saved model data.
     * @throws Exception if the state is invalid or incompatible with the model architecture.
     */
    fun loadState(state: KarlContainerState)

    /**
     * Retrieves the model's current learned parameters in a serializable format.
     *
     * @return A [KarlContainerState] object containing the model's data.
     */
    fun saveState(): KarlContainerState
}

 /* A generic interface representing a machine learning model within a LearningEngine.
 *
 * This abstraction allows a LearningEngine implementation (like `:karl-kldl`) to be
 * designed to work with different model architectures (e.g., MLP, RNN, Transformer)
 * that conform to this contract. It defines the essential operations of a model:
 * prediction, training, and state management.
 */
interface LearningModel {

    /**
     * Initializes the model's internal structure and parameters.
     * This is where the actual model graph (e.g., a KotlinDL Sequential model) would be built.
     */
    fun initialize()

    /**
     * Performs inference on a given input feature vector.
     *
     * @param input A numerical representation (e.g., FloatArray) of the input features.
     * @return A numerical representation (e.g., FloatArray) of the model's output,
     *         typically a vector of probabilities or scores.
     */
    fun predict(input: FloatArray): FloatArray

    /**
     * Performs a single training step on the model.
     *
     * @param input A numerical representation of the training input features.
     * @param target A numerical representation of the expected output (the label).
     */
    fun train(input: FloatArray, target: FloatArray)

    /**
     * Loads the model's learned parameters (e.g., weights) from a serialized state.
     *
     * @param state The [KarlContainerState] containing the previously saved model data.
     * @throws Exception if the state is invalid or incompatible with the model architecture.
     */
    fun loadState(state: KarlContainerState)

    /**
     * Retrieves the model's current learned parameters in a serializable format.
     *
     * @return A [KarlContainerState] object containing the model's data.
     */
    fun saveState(): KarlContainerState
}