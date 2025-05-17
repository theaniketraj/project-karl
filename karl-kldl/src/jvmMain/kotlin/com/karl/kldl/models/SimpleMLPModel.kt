package com.karl.kldl.models

import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
// Other KotlinDL imports if needed for more complex layers later (e.g., Dropout, Flatten)

/**
 * Defines a simple Multi-Layer Perceptron (MLP) model using KotlinDL.
 *
 * This model is designed for a basic classification task where the input is a fixed-size vector
 * (representing features extracted from recent interactions, e.g., the last N action types)
 * and the output is a probability distribution over a fixed number of possible next actions.
 *
 * @property inputSize The number of features in the input vector. For example, if using the
 *                     last 3 actions, and each action is represented by one feature, inputSize = 3.
 * @property numClasses The number of possible output classes (e.g., the number of distinct actions
 *                      the model can predict).
 * @property hiddenLayerSizes A list of integers defining the number of neurons in each hidden layer.
 *                            An empty list means no hidden layers (direct input to output).
 *                            Example: listOf(16, 8) creates two hidden layers.
 * @property hiddenLayerActivation The activation function to use for the hidden layers.
 *                                  Defaults to ReLU (Rectified Linear Unit).
 * @property outputLayerActivation The activation function for the output layer.
 *                                 For multi-class classification, this is typically NOT explicitly set here
 *                                 if using a loss function like SoftmaxCrossEntropyWithLogits,
 *                                 as the loss function handles the softmax. If set, it might be
 *                                 Activations.Softmax for explicit probability output, or
 *                                 Activations.Sigmoid for binary or multi-label classification.
 *                                 Defaults to null (implying linear output before loss function).
 *
 * @return A configured KotlinDL [Sequential] model.
 */
fun createSimpleMLPModel(
    inputSize: Int,
    numClasses: Int,
    hiddenLayerSizes: List<Int> = listOf(16, 8), // Default: two hidden layers
    hiddenLayerActivation: Activations = Activations.Relu,
    outputLayerActivation: Activations? = null // Often handled by the loss function
): Sequential {
    println("Creating SimpleMLPModel: inputSize=$inputSize, numClasses=$numClasses, hiddenLayers=$hiddenLayerSizes")

    // Start building the model sequentially
    val layers = mutableListOf<org.jetbrains.kotlinx.dl.api.core.layer.Layer>()

    // 1. Input Layer
    // The shape is defined by the inputSize. KotlinDL's Input layer takes Long for shape.
    layers.add(Input(inputSize.toLong()))

    // 2. Hidden Layers (if any)
    var currentInputSize = inputSize
    hiddenLayerSizes.forEachIndexed { index, layerSize ->
        println("Adding Hidden Layer ${index + 1}: size=$layerSize, activation=$hiddenLayerActivation")
        layers.add(
            Dense(
                outputSize = layerSize,
                activation = hiddenLayerActivation,
                // kernelInitializer = Initializers.HE_NORMAL, // Example initializer
                // biasInitializer = Initializers.ZEROS      // Example initializer
                // name = "hidden_layer_${index + 1}"       // Optional name
            )
        )
        currentInputSize = layerSize // Output of this layer is input to the next
    }

    // 3. Output Layer
    // The output layer has 'numClasses' neurons, one for each possible action/class.
    // The activation function here depends on the loss function used during compilation.
    // If using SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS, the softmax is applied by the loss,
    // so the output layer can be linear (no explicit activation or Activations.Linear).
    println("Adding Output Layer: size=$numClasses, activation=${outputLayerActivation ?: "Linear (handled by loss)"}")
    layers.add(
        Dense(
            outputSize = numClasses,
            activation = outputLayerActivation ?: Activations.Linear, // Linear if null, or specified
            // name = "output_layer"
        )
    )

    return Sequential.of(layers)
}

// --- Example Usage (how KLDLLearningEngine might call it) ---

fun main() {
    // Example parameters from KLDLLearningEngine constants
    val INPUT_SIZE_CONST = 3
    val NUM_ACTIONS_CONST = 4

    // Create a model with default hidden layers
    val model1 = createSimpleMLPModel(
        inputSize = INPUT_SIZE_CONST,
        numClasses = NUM_ACTIONS_CONST
    )
    model1.summary() // Print model summary

    println("\n-------------------\n")

    // Create a model with custom hidden layers and softmax output
    val model2 = createSimpleMLPModel(
        inputSize = INPUT_SIZE_CONST,
        numClasses = NUM_ACTIONS_CONST,
        hiddenLayerSizes = listOf(32, 16, 8),
        outputLayerActivation = Activations.Softmax // Explicit softmax
    )
    model2.summary()
}
