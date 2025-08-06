# Tutorial: Implementing User Rules for KARL

Project KARL is designed to be controllable by users. The `KarlInstruction` system lets your application—and your users—define rules that guide the AI's behavior. This tutorial walks you through implementing a simple, user-configurable rule.

## Objective

Learn how to use the `KarlInstruction` and `InstructionParser` interfaces to let users set a minimum confidence threshold for predictions shown in the UI.

## Prerequisites

- A working Project KARL setup.
- Familiarity with the `KarlContainer` and `LearningEngine` interfaces.

## 1. The `KarlInstruction` Model

The `:karl-core` module provides a sealed class called `KarlInstruction`. For this tutorial, we'll use the built-in `MinConfidence` instruction:

```kotlin
sealed class KarlInstruction {
    // ...
    data class MinConfidence(val threshold: Float) : KarlInstruction()
}
```

This data class represents a rule: predictions must have a confidence score greater than or equal to `threshold` to be considered valid.

## 2. Implementing an InstructionParser

The `InstructionParser` converts raw user input into structured `KarlInstruction` objects. For this tutorial, we'll parse a simple string format like `min_confidence=0.75`.

```kotlin
class SimpleDslInstructionParser : InstructionParser {
    override fun parse(rawInput: String): List<KarlInstruction> {
        if (rawInput.isBlank()) return emptyList()

        val instructions = mutableListOf<KarlInstruction>()
        val lines = rawInput.lines().filter { it.isNotBlank() }

        for (line in lines) {
            val parts = line.split('=', limit = 2).map { it.trim() }
            if (parts.size != 2) throw KarlInstructionParseException("Invalid instruction format: '$line'")

            val key = parts[0]
            val value = parts[1]

            when (key) {
                "min_confidence" -> {
                    val threshold = value.toFloatOrNull()
                        ?: throw KarlInstructionParseException("Invalid float value for min_confidence: '$value'")
                    if (threshold !in 0.0f..1.0f)
                        throw KarlInstructionParseException("min_confidence must be between 0.0 and 1.0")
                    instructions.add(KarlInstruction.MinConfidence(threshold))
                }
                else -> throw KarlInstructionParseException("Unknown instruction key: '$key'")
            }
        }
        return instructions
    }
}
```

## 3. Integrating with the Application

Your application should provide a UI for users to input rules and a way to update the `KarlContainer`.

**Example UI (Jetpack Compose):**

```kotlin
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val currentRulesText by viewModel.rulesText.collectAsState()

    Column {
        Text("Enter KARL Rules (e.g., min_confidence=0.8):")
        OutlinedTextField(
            value = currentRulesText,
            onValueChange = { newText -> viewModel.onRulesTextChanged(newText) }
        )
        Button(onClick = { viewModel.applyRules() }) {
            Text("Apply Rules")
        }
    }
}
```

**State Holder / ViewModel:**

```kotlin
class SettingsViewModel(
    private val karlContainer: KarlContainer,
    private val instructionParser: InstructionParser = SimpleDslInstructionParser()
) {
    private val _rulesText = MutableStateFlow("")
    val rulesText: StateFlow<String> = _rulesText.asStateFlow()

    fun onRulesTextChanged(newText: String) {
        _rulesText.value = newText
    }

    fun applyRules() {
        try {
            val instructions = instructionParser.parse(_rulesText.value)
            karlContainer.updateInstructions(instructions)
            // Show success message to user
        } catch (e: KarlInstructionParseException) {
            // Show error message to user
        }
    }
}
```

## 4. Enforcing the Rule in the LearningEngine

Update your `LearningEngine` implementation to respect the instructions:

```kotlin
override suspend fun predict(
    contextData: List<InteractionData>,
    instructions: List<KarlInstruction>
): Prediction? {
    // ... model inference to get rawPrediction and confidence ...

    val minConfidenceInstruction = instructions.filterIsInstance<KarlInstruction.MinConfidence>().firstOrNull()

    if (minConfidenceInstruction != null && confidence < minConfidenceInstruction.threshold) {
        println("Prediction confidence $confidence is below user threshold ${minConfidenceInstruction.threshold}")
        return null // Suppress the prediction
    }

    return Prediction(suggestion = rawPrediction, confidence = confidence)
}
```

## 5. Conclusion

This tutorial shows how to create a feedback loop for user control. By implementing an `InstructionParser` and using `karlContainer.updateInstructions()`, you empower users to customize their local AI's behavior, enhancing transparency and trust in line with Project KARL's philosophy.
