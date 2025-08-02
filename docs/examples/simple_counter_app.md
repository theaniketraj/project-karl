# Example: A Simple Learning Counter

This example provides a minimal, "Hello, World!" style demonstration of Project KARL's core mechanics. The goal is to show the absolute basics of the data flow: how an application event triggers a learning step and how the AI's internal state is updated.

This is the simplest possible integration to verify that your build setup and core components are working together.

## Objective

To create an application with a single button. Each time the button is clicked, KARL's `LearningEngine` is notified, and we can observe its internal "interaction count" metric increasing.

## Key Concepts Demonstrated

* The minimal setup for a `KarlContainer`.
* Implementing a basic `DataSource`.
* The connection between a UI event and the `LearningEngine.trainStep()` method.
* Persisting and loading a simple state metric (the interaction count).

### 1. The `InteractionData`

For this simple case, the `InteractionData` is minimal. We only care that an event happened.

```kotlin
// When the button is clicked in your app
val interaction = InteractionData(
    userId = "counter-user",
    type = "button_increment",
    details = mapOf("source" -> "main_button"),
    timestamp = System.currentTimeMillis()
)
// Pass this 'interaction' to the KarlContainer
```

### 2. The LearningEngine Strategy

The LearningEngine for this example doesn't need a complex neural network. Its primary job is to track the number of interactions it has processed.

* trainStep(data: InteractionData): The implementation of this method will simply increment a private counter variable.
* getCurrentState(): This method will serialize the counter's current value into a ByteArray.
* initialize(state: KarlContainerState?, ...): This method will deserialize the ByteArray from the provided state (if it exists) and set its internal counter to the loaded value.
  
→ This demonstrates the core state management loop without the complexity of ML model weights.

→ The KLDLLearningEngine in the :karl-kldl module already includes logic for tracking interactionCount as part of its state serialization, making it suitable for this test.

### 3. The Application Flow

#### 1. On App Start

* Initialize the KarlContainer with your LearningEngine and DataStorage (e.g., :karl-room) implementations.
* During initialization, the LearningEngine will load the previously saved interaction count from the database.
* The UI displays the initial count (e.g., "Interactions Processed: 42").

#### 2. User Clicks the Button

* The DataSource captures the click and sends an InteractionData object to the KarlContainer.
* The container passes this to LearningEngine.trainStep().
* The engine increments its internal counter from 42 to 43.
* The UI is notified (e.g., via a StateFlow) and updates to show "Interactions Processed: 43".

#### 3. On App Close

* The application calls karlContainer.saveState().
* The LearningEngine's getCurrentState() is called, which serializes the new count (43) into a ByteArray.
* The DataStorage implementation saves this ByteArray to the local database.

#### 4. On Relaunch

* The flow repeats from Step 1. The LearningEngine will now load the value 43 from the database, and the UI will correctly start at "Interactions Processed: 43".

### Conclusion

This simple counter application, while not performing complex AI predictions, perfectly validates the entire KARL architecture: data ingestion via DataSource, stateful processing in the LearningEngine, persistence via DataStorage, and lifecycle management by the KarlContainer. It is the ideal first step for any developer starting with Project KARL.
