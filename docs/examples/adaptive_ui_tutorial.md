# Tutorial: Building a Basic Adaptive UI Element with KARL

This tutorial provides a step-by-step guide to creating a simple adaptive UI element using Project KARL and Jetpack Compose. We will build a "Quick Actions" toolbar that automatically reorders itself to show the user's most frequently used actions first.

## Objective

To learn how to connect KARL's learning capabilities to a reactive UI, creating a user interface that personalizes itself over time.

## Prerequisites

* A working Project KARL setup, as described in the [Getting Started guide](../getting-started.md).
* A Jetpack Compose for Desktop (or Android) application environment.
* Dependencies on `:karl-core`, a `LearningEngine` (e.g., `:karl-kldl`), a `DataStorage` (e.g., `:karl-room`), and `:karl-compose-ui`.

### 1. Defining the Problem

Imagine an application with many possible actions (e.g., "New File," "Save," "Export PDF," "Print"). We want to display the top 3 most frequently used actions in a prominent toolbar for quick access.

### 2. State Management in Your Application

In your application's state holder (e.g., a ViewModel or a custom class managed with `remember`), you'll need to manage the state for the UI.

```kotlin
// In your app's state holder (e.g., a ViewModel)
class AppViewModel(private val karlContainer: KarlContainer) {
    private val _quickActions = MutableStateFlow<List<String>>(listOf("Save", "Export PDF", "Print"))
    val quickActions: StateFlow<List<String>> = _quickActions.asStateFlow()

    fun onActionUsed(actionId: String) {
        // This will be called from the UI when a button is clicked.
        // It will feed data to KARL and then update the quick actions list.
        viewModelScope.launch {
            // The DataSource should be observing this or a similar event stream
            // to create and send InteractionData to the container.

            // After the action is processed, we ask KARL for an updated list.
            val prediction = karlContainer.getPrediction()
            // Assume the prediction.content is a comma-separated list of top actions
            prediction?.content?.let { newOrder ->
                _quickActions.value = newOrder.split(",")
            }
        }
    }
}
```

### 3. Implementing the LearningEngine Strategy

For this scenario, the LearningEngine's role is to rank actions by usage frequency, not to predict the next action. Here’s how it works:

* **trainStep(data: InteractionData):**  
    When the engine receives an `InteractionData` event of type `action_used`, it updates an internal frequency map (such as `Map<String, Int>`). This map serves as the simple "learned model."

* **predict(...):**  
    The engine sorts the frequency map in descending order by count, selects the top three action IDs, and returns them as a comma-separated string in the `Prediction` object's `suggestion` field.

* **State Management:**  
    The `getCurrentState()` method serializes the frequency map (for example, to JSON) into a `ByteArray`. The `initialize()` method deserializes it to restore state.

### 4. Building the Jetpack Compose UI

Your UI observes the `StateFlow` from the state holder and renders the quick action buttons:

```kotlin
@Composable
fun QuickActionsToolbar(viewModel: AppViewModel) {
        val actions by viewModel.quickActions.collectAsState()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                actions.forEach { actionId ->
                        Button(onClick = { viewModel.onActionUsed(actionId) }) {
                                Text(actionId)
                        }
                }
        }
}
```

### 5. Connecting Everything

1. The user clicks an action button, such as "Print."
2. The button's `onClick` handler calls `viewModel.onActionUsed("Print")`.
3. The application's DataSource listens for these events and sends an `InteractionData` object with `type = "action_used"` and `details = mapOf("action_id" to "Print")` to the `KarlContainer`.
4. The LearningEngine's `trainStep` increments the count for "Print" in its frequency map.
5. The ViewModel calls `karlContainer.getPrediction()`.
6. The LearningEngine's `predict` method sorts the map, finds the top three actions, and returns them (e.g., `"Print,Save,Export PDF"`).
7. The ViewModel updates the `_quickActions` `StateFlow`.
8. The `QuickActionsToolbar` Composable automatically recomposes, updating the button order to reflect the user's preferences.

### 6. Conclusion

This tutorial shows how to create a feedback loop between user actions, KARL's learning process, and a reactive UI. By leveraging KARL to learn simple statistics like frequency, you can build adaptive interfaces that personalize themselves to each user's workflow.
