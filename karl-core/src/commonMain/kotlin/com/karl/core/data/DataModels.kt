package com.karl.core.data

/**
 * Represents a piece of user interaction data that KARL can learn from.
 * This is metadata, not sensitive content.
 * @property type A string representing the type of interaction (e.g., "git_command", "button_click").
 * @property details A map or structured data containing details about the interaction (e.g., {"command": "commit"}).
 * @property timestamp The time the interaction occurred (useful for sequence or recency).
 * @property userId The ID of the user this data belongs to (useful if one storage handles multiple users).
 */
data class InteractionData(
    val type: String,
    val details: Map<String, Any>, // Or a more structured sealed class later
    val timestamp: Long,
    val userId: String // KARL is per-user, but storage might be shared
)

/**
 * Represents the learned state of the AI model (e.g., model weights, internal parameters).
 * This is what gets saved and loaded.
 * @property data A byte array containing the serialized state of the model.
 * @property version A version identifier for the state format, useful for migrations.
 */
data class KarlContainerState(
    val data: ByteArray,
    val version: Int = 1 // Start with version 1
) {
    // Good practice to override equals and hashCode for ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KarlContainerState

        if (!data.contentEquals(other.data)) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + version
        return result
    }
}

/**
 * Represents a prediction or suggestion made by KARL.
 * @property suggestion The main suggestion (e.g., "git commit").
 * @property confidence A value indicating the confidence in the suggestion (0.0 to 1.0).
 * @property type The type of prediction (e.g., "next_command", "ui_highlight").
 * @property metadata Optional additional data about the prediction (e.g., suggested branch name).
 */
data class Prediction(
    val suggestion: String, // Or a more structured sealed class for specific types
    val confidence: Float,
    val type: String,
    val metadata: Map<String, Any>? = null
)

/**
 * Represents a user-defined instruction or rule for the KarlContainer.
 * This is a placeholder; a DSL or structured class hierarchy could be used later.
 */
sealed class KarlInstruction {
    // Example: A rule to ignore specific data types
    data class IgnoreDataType(val type: String) : KarlInstruction()
    // Example: A rule to prioritize suggestions with confidence above a threshold
    data class MinConfidence(val threshold: Float) : KarlInstruction()
    // Add more specific instruction types as needed
}