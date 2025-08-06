# Example: Predicting the Next Action in a Sequence

This example demonstrates how Project KARL can learn and predict sequential user actions, enabling smarter workflow automation in developer tools and productivity apps.

The repository's `:karl-example-desktop` module contains a complete, runnable implementation.

## Objective

Build a KARL integration that observes user action sequences and suggests the most likely next action, helping reduce repetitive tasks and improve efficiency.

## Key Concepts

- **Sequence Modeling:** Managing time-ordered `InteractionData`.
- **Contextual Prediction:** Using recent action history as input features.
- **Feature Engineering:** Transforming categorical actions into numerical vectors for modeling.

### 1. Designing `InteractionData`

`InteractionData` should capture the sequence and timing of user actions:

```kotlin
// Example DataSource code
fun onUserAction(actionId: String, context: Map<String, Any>) {
    val interaction = InteractionData(
        userId = "current-user",
        type = "user_action",
        details = mapOf("action_id" to actionId) + context,
        timestamp = System.currentTimeMillis()
    )
    onNewData(interaction)
}
```

- `action_id` is the key feature.
- `timestamp` ensures correct ordering.

### 2. LearningEngine Strategy

The engine must learn "what comes next" in a sequence.

**Feature Engineering:**

- Use a sliding window over recent actions.
- Map each unique `action_id` to an integer (e.g., `"git_add"` → 1).
- Input vector: Last N actions as integers (e.g., `[0, 1, 2]` for N=3).
- Target label: The next action, one-hot encoded.

**Model Architecture:**

- Start with a simple MLP for feasibility.
- For advanced use, consider RNNs/LSTMs or Transformers for better sequence modeling.

### 3. Application Flow

1. User performs actions (e.g., `git add`, then `git commit`).
2. DataSource captures each action and sends it to KarlContainer.
3. Each action triggers a training step in the LearningEngine.
4. After an action, the UI requests a prediction via `karlContainer.getPrediction()`.
5. LearningEngine prepares the input vector from recent actions.
6. The model predicts the next likely action (e.g., `git_push`).
7. UI displays the suggestion to the user.

### 4. Conclusion

By modeling sequential user actions and leveraging history-aware features, KARL enables proactive, personalized workflow suggestions that adapt to individual habits.
