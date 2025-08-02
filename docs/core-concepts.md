# Core Concepts

To effectively use and contribute to Project KARL, it's essential to understand its fundamental architectural components and design principles. This section breaks down the core concepts that define how KARL operates.

## The KARL Container Model (`KarlContainer`)

The central abstraction in Project KARL is the **Container**. Represented by the `KarlContainer` interface, it acts as a dedicated, isolated environment, a "personal AI sandbox" for each user or context.

* **Data Isolation:** Each container operates independently, ensuring that the learning and personalization for one user do not influence another. It holds the user-specific learned model, manages its state, and orchestrates the entire data flow.
* **Lifecycle Management:** The application is responsible for managing the container's lifecycle:
    1.**Creation:** Using the `KarlAPI` builder.
    2.**Initialization:** Calling `initialize()` to load state and start learning.
    3.**State Persistence:** Calling `saveState()` to persist the AI's learned knowledge.
    4.**Reset:** Calling `reset()` to return the AI to its initial "blank slate" state.
    5.**Release:** Calling `release()` to clean up resources on shutdown.

## Data Handling

KARL's effectiveness relies on a well-defined and privacy-preserving data handling process.

* **`InteractionData`:** The primary unit of information for learning. It is a data class designed to hold **metadata** about a user's action (e.g., type of action, timestamp, contextual details), not the sensitive content itself.
* **`DataSource`:** An interface that **your application must implement**. It acts as the bridge, observing user actions within your app and feeding them into the `KarlContainer` as `InteractionData` objects.
* **`DataStorage`:** An interface that defines the contract for local persistence. Implementations like `:karl-room` are responsible for saving the AI's learned state (`KarlContainerState`) and, optionally, a history of interactions to the user's device. **Encryption at rest** is a critical responsibility of any `DataStorage` implementation.

## The Learning Process (`LearningEngine`)

The `LearningEngine` interface encapsulates the actual machine learning model and its adaptation logic.

* **Incremental Learning (`trainStep`)**: KARL primarily employs incremental (or "online") learning. When new `InteractionData` is received, the `LearningEngine`'s `trainStep()` method is triggered. This performs a small update to the model's parameters, allowing it to continuously adapt to the user's evolving behavior in near real-time.
* **No Pre-training (The "Blank Slate")**: A key principle is that models start "from scratch" for each user, with no prior knowledge. The intelligence is built up purely from the individual user's interactions, ensuring true personalization and privacy.

## The Inference Process (`predict`)

Inference is the process of using the learned model to generate suggestions.

* **Requesting a Prediction:** The application calls `karlContainer.getPrediction()`. This method queries the `LearningEngine`, which uses its internal model and optional recent context data to generate a result.
* **The `Prediction` Object:** The output is a structured `Prediction` data class containing the `suggestion`, a `confidence` score, a prediction `type`, and optional `metadata`. This allows the application to intelligently interpret and display the AI's output.
