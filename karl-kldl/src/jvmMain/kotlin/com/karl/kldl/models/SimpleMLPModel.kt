package com.karl.kldl.models

/**
 * Stub implementation for SimpleMLPModel
 * This is a placeholder until KotlinDL dependencies are properly resolved
 */
object SimpleMLPModel {
    fun createModel(
        inputSize: Int = 3,
        numClasses: Int = 4,
        hiddenLayerSizes: List<Int> = listOf(10, 10),
    ): String {
        println("SimpleMLPModel (Stub): Creating model with inputSize=$inputSize, numClasses=$numClasses")
        return "stub_model"
    }

    fun getInputSize(): Int = 3

    fun getNumClasses(): Int = 4
}
