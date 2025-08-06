# Example: Learning User Preferences

This example demonstrates a fundamental use case for Project KARL: learning a user's simple, categorical preferences over time. We will model a scenario where an application has a choice between two themes, "Synthwave" and "Minimalist," and KARL learns which one the user prefers based on their selections.

## Objective

To show how KARL can be used for a basic classification task by observing user choices and adapting future defaults or suggestions.

## Key Concepts

* **Classification:** Using KARL to predict a category.
* **Feature Extraction:** Converting a user choice into meaningful `InteractionData`.
* **Stateful Default:** Using KARL's prediction to set the default state of the application on next launch.

### 1. Designing the `InteractionData`

When a user selects a theme, our `DataSource` needs to capture this choice as a clear signal.

```kotlin
// Conceptual code within your DataSource
fun onThemeSelected(themeName: String) {
    val interaction = InteractionData(
        userId = "current-user",
        type = "theme_selection",
        details = mapOf("selected_theme" to themeName), // "Synthwave" or "Minimalist"
        timestamp = System.currentTimeMillis()
    )
    // Pass this 'interaction' to the KarlContainer
    onNewData(interaction)
}
```

The `type` field in `InteractionData` clearly identifies the event, while the `details` map contains the user's specific choice—this will be our primary feature for learning.

### 2. LearningEngine Strategy

For this binary classification task, a simple model is sufficient.

#### Feature Engineering

The LearningEngine must convert `InteractionData` into a format suitable for the model:

* **Input Vector:** The input can be minimal (e.g., a constant value like `1.0f` to trigger prediction), or include additional context such as time of day if you want to explore changing preferences.
  
* **Target Label:** The user's choice is encoded numerically for training. For example:
  * `"Synthwave"` → `[1.0, 0.0]` (one-hot encoded)
  * `"Minimalist"` → `[0.0, 1.0]`

#### Model Architecture

A simple Multi-Layer Perceptron (MLP) with one hidden layer and an output layer of two neurons works well. The output provides scores representing the model's confidence for each theme.

**Example architecture:**

```kotlin
Input → Dense(8, activation=ReLU) → Dense(2) → (Softmax via loss function)
```

### 3. Application Flow

1. **User Selects a Theme:** The user clicks a theme button (e.g., "Synthwave").
2. **DataSource Captures Event:** `onThemeSelected("Synthwave")` is called, creating and sending the `InteractionData`.
3. **LearningEngine Trains:** The `trainStep` method receives the data, using the input features and target label `[1.0, 0.0]` to update the MLP's weights, increasing its bias toward "Synthwave".
4. **App Relaunches:** On the next launch, before rendering the UI, the app calls `karlContainer.getPrediction()`.
5. **KARL Predicts:** The LearningEngine runs inference. After several "Synthwave" selections, the output might be `[0.85, 0.15]`, indicating 85% confidence in "Synthwave".
6. **UI Adapts:** The prediction suggests "Synthwave" as the default. The application uses this to set the initial theme, personalizing the experience.

### 4. Conclusion

This example demonstrates KARL's adaptive learning: translating user choices into training signals for an on-device model, enabling privacy-preserving personalization of application defaults and behavior.
