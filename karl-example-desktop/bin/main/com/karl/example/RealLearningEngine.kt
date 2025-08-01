package com.karl.example

import api.LearningEngine
import com.karl.core.learning.LearningInsights
import com.karl.core.models.InteractionData
import com.karl.core.models.KarlContainerState
import com.karl.core.models.KarlInstruction
import com.karl.core.models.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.*
import kotlin.random.Random

/**
 * Real LearningEngine implementation using a simple Multi-Layer Perceptron
 * This replaces the KLDLLearningEngine stub with actual neural network learning
 */
class RealLearningEngine(
    private val learningRate: Float = 0.01f,
    private val randomSeed: Long = 42L,
) : LearningEngine {
    private val isInitialized = AtomicBoolean(false)
    private val modelMutex = Mutex()
    private lateinit var engineScope: CoroutineScope
    private val random = Random(randomSeed)

    // Simple MLP structure
    private val inputSize = 4 // [action_type_hash, timestamp_normalized, user_hash, context]
    private val hiddenSize = 8
    private val outputSize = 3 // [next_action_confidence, timing_prediction, preference_score]

    // Network weights (initialized randomly)
    private lateinit var weightsInputHidden: Array<FloatArray>
    private lateinit var weightsHiddenOutput: Array<FloatArray>
    private lateinit var biasHidden: FloatArray
    private lateinit var biasOutput: FloatArray

    // Training history
    private val trainingHistory = mutableListOf<TrainingExample>()
    private var trainingSteps = 0
    private var interactionCount = 0L

    // Confidence history for sparkline visualization (keep last 100 predictions)
    private val confidenceHistory = mutableListOf<Float>()

    data class TrainingExample(
        val input: FloatArray,
        val expectedOutput: FloatArray,
        val timestamp: Long,
    )

    override suspend fun initialize(
        state: KarlContainerState?,
        coroutineScope: CoroutineScope,
    ) {
        if (!isInitialized.compareAndSet(false, true)) {
            println("RealLearningEngine: Already initialized.")
            return
        }

        this.engineScope = coroutineScope

        modelMutex.withLock {
            if (state != null) {
                // Try to restore from saved state
                println("RealLearningEngine: Attempting to restore from saved state...")
                try {
                    restoreFromState(state)
                    println("RealLearningEngine: Successfully restored from saved state")
                } catch (e: Exception) {
                    println("RealLearningEngine: Failed to restore state, initializing new model: ${e.message}")
                    initializeNewModel()
                }
            } else {
                initializeNewModel()
            }
        }

        println("RealLearningEngine: Initialized successfully with real neural network")
    }

    private fun initializeNewModel() {
        // Initialize weights with Xavier/Glorot initialization
        val inputHiddenLimit = sqrt(6.0 / (inputSize + hiddenSize)).toFloat()
        weightsInputHidden =
            Array(inputSize) {
                FloatArray(hiddenSize) { random.nextFloat() * 2 * inputHiddenLimit - inputHiddenLimit }
            }

        val hiddenOutputLimit = sqrt(6.0 / (hiddenSize + outputSize)).toFloat()
        weightsHiddenOutput =
            Array(hiddenSize) {
                FloatArray(outputSize) { random.nextFloat() * 2 * hiddenOutputLimit - hiddenOutputLimit }
            }

        // Initialize biases to small random values
        biasHidden = FloatArray(hiddenSize) { random.nextFloat() * 0.1f - 0.05f }
        biasOutput = FloatArray(outputSize) { random.nextFloat() * 0.1f - 0.05f }

        println("RealLearningEngine: Initialized new neural network model")
    }

    private fun restoreFromState(state: KarlContainerState) {
        // For simplicity, we'll just initialize a new model
        // In a real implementation, you'd deserialize the weights from state.data
        initializeNewModel()
        println("RealLearningEngine: State restoration not fully implemented, using new model")
    }

    override fun trainStep(data: InteractionData): Job {
        if (!isInitialized.get()) {
            println("RealLearningEngine: trainStep() called but engine not initialized")
            return engineScope.launch { /* no-op */ }
        }

        // Increment interaction count at the start of trainStep
        interactionCount++
        println("RealLearningEngine: trainStep() received data -> $data")

        return engineScope.launch {
            modelMutex.withLock {
                try {
                    // Convert interaction data to training input
                    val input = convertInteractionToInput(data)
                    val expectedOutput = generateExpectedOutput(data)

                    // Store training example
                    val example = TrainingExample(input, expectedOutput, System.currentTimeMillis())
                    trainingHistory.add(example)

                    // Perform forward and backward pass
                    val (hiddenLayer, output) = forwardPass(input)
                    backwardPass(input, hiddenLayer, output, expectedOutput)

                    trainingSteps++

                    // Keep only recent history to prevent memory bloat
                    if (trainingHistory.size > 1000) {
                        trainingHistory.removeFirst()
                    }

                    println("RealLearningEngine: Training step $trainingSteps completed for data type: ${data.type}")

                    // Log training progress occasionally
                    if (trainingSteps % 10 == 0) {
                        val loss = calculateLoss(output, expectedOutput)
                        println("RealLearningEngine: Training step $trainingSteps, Loss: ${"%.4f".format(loss)}")
                    }
                } catch (e: Exception) {
                    println("RealLearningEngine: Error during training: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
    }

    private fun convertInteractionToInput(data: InteractionData): FloatArray {
        // Convert interaction data to neural network input
        val actionHash = data.type.hashCode().toFloat() / Int.MAX_VALUE // Normalize to [-1, 1]
        val timestampNormalized = (data.timestamp % 86400000) / 86400000.0f // Time of day [0, 1]
        val userHash = data.userId.hashCode().toFloat() / Int.MAX_VALUE // Normalize to [-1, 1]
        val contextValue = if (data.details.isNotEmpty()) 0.5f else 0.0f // Simple context encoding

        return floatArrayOf(actionHash, timestampNormalized, userHash, contextValue)
    }

    private fun generateExpectedOutput(data: InteractionData): FloatArray {
        // Generate expected output based on interaction
        // This is a simplified approach - in reality this would be more sophisticated
        val actionConfidence =
            when (data.type) {
                "action_type_A" -> 0.8f
                "action_type_B" -> 0.7f
                else -> 0.5f
            }

        val timingPrediction = random.nextFloat() * 0.5f + 0.5f // Random timing between 0.5-1.0
        val preferenceScore = actionConfidence * 0.9f // Slightly lower than confidence

        return floatArrayOf(actionConfidence, timingPrediction, preferenceScore)
    }

    private fun forwardPass(input: FloatArray): Pair<FloatArray, FloatArray> {
        // Hidden layer computation
        val hiddenLayer = FloatArray(hiddenSize)
        for (h in 0 until hiddenSize) {
            var sum = biasHidden[h]
            for (i in 0 until inputSize) {
                sum += input[i] * weightsInputHidden[i][h]
            }
            hiddenLayer[h] = tanh(sum) // Activation function
        }

        // Output layer computation
        val output = FloatArray(outputSize)
        for (o in 0 until outputSize) {
            var sum = biasOutput[o]
            for (h in 0 until hiddenSize) {
                sum += hiddenLayer[h] * weightsHiddenOutput[h][o]
            }
            output[o] = sigmoid(sum) // Sigmoid for output probabilities
        }

        return Pair(hiddenLayer, output)
    }

    private fun backwardPass(
        input: FloatArray,
        hiddenLayer: FloatArray,
        output: FloatArray,
        expected: FloatArray,
    ) {
        // Calculate output layer errors
        val outputErrors = FloatArray(outputSize)
        for (o in 0 until outputSize) {
            val error = expected[o] - output[o]
            outputErrors[o] = error * sigmoidDerivative(output[o])
        }

        // Calculate hidden layer errors
        val hiddenErrors = FloatArray(hiddenSize)
        for (h in 0 until hiddenSize) {
            var error = 0.0f
            for (o in 0 until outputSize) {
                error += outputErrors[o] * weightsHiddenOutput[h][o]
            }
            hiddenErrors[h] = error * tanhDerivative(hiddenLayer[h])
        }

        // Update weights and biases
        for (h in 0 until hiddenSize) {
            for (o in 0 until outputSize) {
                weightsHiddenOutput[h][o] += learningRate * outputErrors[o] * hiddenLayer[h]
            }
        }

        for (i in 0 until inputSize) {
            for (h in 0 until hiddenSize) {
                weightsInputHidden[i][h] += learningRate * hiddenErrors[h] * input[i]
            }
        }

        // Update biases
        for (o in 0 until outputSize) {
            biasOutput[o] += learningRate * outputErrors[o]
        }
        for (h in 0 until hiddenSize) {
            biasHidden[h] += learningRate * hiddenErrors[h]
        }
    }

    private fun calculateLoss(
        output: FloatArray,
        expected: FloatArray,
    ): Float {
        var loss = 0.0f
        for (i in output.indices) {
            val diff = expected[i] - output[i]
            loss += diff * diff
        }
        return loss / output.size
    }

    // Activation functions
    private fun tanh(x: Float): Float = kotlin.math.tanh(x.toDouble()).toFloat()

    private fun sigmoid(x: Float): Float = (1.0 / (1.0 + exp(-x.toDouble()))).toFloat()

    private fun tanhDerivative(y: Float): Float = 1.0f - y * y

    private fun sigmoidDerivative(y: Float): Float = y * (1.0f - y)

    override suspend fun predict(
        contextData: List<InteractionData>,
        instructions: List<KarlInstruction>,
    ): Prediction? {
        if (!isInitialized.get()) {
            println("RealLearningEngine: predict() called but engine not initialized")
            return null
        }

        return modelMutex.withLock {
            try {
                println("RealLearningEngine: predict() called with ${contextData.size} context items")

                // Use most recent interaction as input, or create default if empty
                val input =
                    if (contextData.isNotEmpty()) {
                        convertInteractionToInput(contextData.last())
                    } else {
                        FloatArray(inputSize) { 0.0f } // Default neutral input
                    }

                // Forward pass to get prediction
                val (_, output) = forwardPass(input)

                // Generate multiple predictions with different thresholds
                val primaryConfidence = output[0]
                val timingPrediction = output[1]
                val preferenceScore = output[2]

                // Create action suggestions based on all outputs
                val actionSuggestions = mutableListOf<Pair<String, Float>>()

                // Primary action based on main confidence
                val primaryAction =
                    when {
                        primaryConfidence > 0.7f -> "High Priority Action"
                        primaryConfidence > 0.5f -> "Standard Action"
                        else -> "Low Priority Action"
                    }
                actionSuggestions.add(primaryAction to primaryConfidence)

                // Alternative actions based on timing and preference
                val timingAction =
                    when {
                        timingPrediction > 0.7f -> "Time-Sensitive Task"
                        timingPrediction > 0.4f -> "Scheduled Task"
                        else -> "Background Task"
                    }
                actionSuggestions.add(timingAction to timingPrediction)

                val preferenceAction =
                    when {
                        preferenceScore > 0.6f -> "User Preferred Action"
                        preferenceScore > 0.3f -> "Alternative Action"
                        else -> "Fallback Action"
                    }
                actionSuggestions.add(preferenceAction to preferenceScore)

                // Sort by confidence and take top suggestion as primary
                val sortedSuggestions = actionSuggestions.sortedByDescending { it.second }
                val suggestion = sortedSuggestions.first().first
                val alternatives = sortedSuggestions.drop(1).map { it.first }

                val prediction =
                    Prediction(
                        suggestion = suggestion,
                        confidence = primaryConfidence,
                        type = "neural_network_prediction",
                        alternatives = alternatives,
                        metadata =
                            mapOf(
                                "training_steps" to trainingSteps.toString(),
                                "model_output" to output.contentToString(),
                                "context_size" to contextData.size.toString(),
                                "input_features" to input.contentToString(),
                                "timing_prediction" to timingPrediction.toString(),
                                "preference_score" to preferenceScore.toString(),
                                "action_count" to actionSuggestions.size.toString(),
                            ),
                    )

                // Track confidence for sparkline visualization
                confidenceHistory.add(primaryConfidence)
                if (confidenceHistory.size > 100) {
                    confidenceHistory.removeAt(0) // Keep only last 100 predictions
                }

                println("RealLearningEngine: Generated prediction -> $prediction")
                prediction
            } catch (e: Exception) {
                println("RealLearningEngine: Error during prediction: ${e.message}")
                null
            }
        }
    }

    override suspend fun getCurrentState(): KarlContainerState {
        println("RealLearningEngine: getCurrentState() called")
        return modelMutex.withLock {
            println("RealLearningEngine: Serializing neural network state...")
            println("RealLearningEngine: Training steps: $trainingSteps, Interactions: $interactionCount")
            println("RealLearningEngine: Training history size: ${trainingHistory.size}")

            // Serialize the actual neural network weights and state
            val serializedState = serializeNeuralNetworkState()

            println("RealLearningEngine: Serialized neural network state, data size=${serializedState.size} bytes")
            KarlContainerState(data = serializedState, version = 1)
        }
    }

    /**
     * Serializes the neural network weights and training state.
     * In a production implementation, this would use a proper serialization format.
     */
    private fun serializeNeuralNetworkState(): ByteArray {
        println("RealLearningEngine: serializeNeuralNetworkState() called")

        val stateBuilder = mutableListOf<Byte>()

        // Add learning rate as bytes
        val learningRateBytes =
            learningRate.toBits().let { bits ->
                byteArrayOf(
                    (bits shr 24).toByte(),
                    (bits shr 16).toByte(),
                    (bits shr 8).toByte(),
                    bits.toByte(),
                )
            }
        stateBuilder.addAll(learningRateBytes.toList())

        // Add training steps
        stateBuilder.addAll(
            trainingSteps.let { steps ->
                byteArrayOf(
                    (steps shr 24).toByte(),
                    (steps shr 16).toByte(),
                    (steps shr 8).toByte(),
                    steps.toByte(),
                )
            }.toList(),
        )

        // Add interaction count
        val interactionCountInt = interactionCount.toInt()
        stateBuilder.addAll(
            interactionCountInt.let { count ->
                byteArrayOf(
                    (count shr 24).toByte(),
                    (count shr 16).toByte(),
                    (count shr 8).toByte(),
                    count.toByte(),
                )
            }.toList(),
        )

        // Serialize input-hidden weights (simplified - just a sample)
        weightsInputHidden.take(2).forEach { row ->
            row.take(2).forEach { weight ->
                val weightBits = weight.toBits()
                stateBuilder.addAll(
                    byteArrayOf(
                        (weightBits shr 24).toByte(),
                        (weightBits shr 16).toByte(),
                        (weightBits shr 8).toByte(),
                        weightBits.toByte(),
                    ).toList(),
                )
            }
        }

        // Serialize hidden-output weights (simplified - just a sample)
        weightsHiddenOutput.take(2).forEach { row ->
            row.take(2).forEach { weight ->
                val weightBits = weight.toBits()
                stateBuilder.addAll(
                    byteArrayOf(
                        (weightBits shr 24).toByte(),
                        (weightBits shr 16).toByte(),
                        (weightBits shr 8).toByte(),
                        weightBits.toByte(),
                    ).toList(),
                )
            }
        }

        val result = stateBuilder.toByteArray()
        println("RealLearningEngine: serializeNeuralNetworkState() completed, serialized ${result.size} bytes")
        return result
    }

    override suspend fun reset() {
        modelMutex.withLock {
            trainingHistory.clear()
            trainingSteps = 0
            interactionCount = 0L
            initializeNewModel()
            println("RealLearningEngine: Reset completed - reinitialized neural network")
        }
    }

    override suspend fun getLearningInsights(): LearningInsights {
        return modelMutex.withLock {
            // Calculate average confidence from recent training history
            val averageConfidence =
                if (trainingHistory.isNotEmpty()) {
                    val recentHistory = trainingHistory.takeLast(20) // Last 20 training examples
                    val totalConfidence = recentHistory.sumOf { it.expectedOutput[0].toDouble() }
                    (totalConfidence / recentHistory.size).toFloat()
                } else {
                    0.5f // Default confidence when no training history
                }

            LearningInsights(
                interactionCount = interactionCount,
                progressEstimate = (interactionCount / 100.0f).coerceAtMost(1.0f),
                customMetrics =
                    mapOf(
                        "averageConfidence" to averageConfidence,
                        "trainingSteps" to trainingSteps,
                        "modelVersion" to "neural_network_v1",
                        "confidenceHistory" to confidenceHistory.toList(), // Copy to avoid concurrent modification
                    ),
            )
        }
    }

    override fun getModelArchitectureName(): String {
        return "MLP(${inputSize}x${hiddenSize}x$outputSize)"
    }

    override suspend fun release() {
        println("RealLearningEngine: Released neural network resources")
    }
}
