package com.karl.core.models

data class InteractionData(
    val type: String,
    val details: Map<String, Any>,
    val timestamp: Long,
    val userId: String,
)

data class KarlContainerState(
    val data: ByteArray,
    val version: Int = 1,
) {
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

data class Prediction(
    val suggestion: String,
    val confidence: Float,
    val type: String,
    val metadata: Map<String, Any>? = emptyMap(),
)

sealed class KarlInstruction {
    data class IgnoreDataType(val type: String) : KarlInstruction()

    data class MinConfidence(val threshold: Float) : KarlInstruction()
}
