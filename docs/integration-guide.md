# Integration Guide

This guide provides a more detailed walkthrough of the steps and considerations for effectively integrating Project KARL into your application.

## 1. Designing `InteractionData`

The quality of KARL's learning depends entirely on the data you provide.

* **Identify Relevant Metadata:** Choose user actions that provide meaningful signals for the personalization you want to achieve. Focus on *what* the user did (e.g., "used-search-filter"), not the *content* of what they did (e.g., the search query text).
* **Structure the `details` Map:** Use the `details` map in `InteractionData` to provide rich contextual features. For example, when a user saves a file, the details could include `mapOf("file_type" to "kt", "lines_of_code" to 150)`. These features are crucial for the `LearningEngine`.

### 2. Implementing the `DataSource`

Your `DataSource` is the bridge between your app and KARL.

* **Event Source:** Connect your `DataSource` to your application's existing event system, whether it's based on callbacks, listeners, or reactive streams like Kotlin Flow.
* **Coroutine Scope:** The `observeInteractionData` method is provided with a `CoroutineScope` from the `KarlContainer`. Use this scope to launch any long-running listeners or collectors to ensure they are tied to the container's lifecycle.
* **Filtering:** You can perform initial filtering within the `DataSource` to avoid sending low-value or noisy interactions to KARL.

See the [`ExampleDataSource`](https://github.com/theaniketraj/project-karl/blob/main/karl-example-desktop/bin/main/com/karl/example/DesktopExampleApp.kt) for a reference implementation using `SharedFlow`.

### 3. Managing the `KarlContainer` Lifecycle

Proper lifecycle management is essential for persistence and resource management.

* **Scope:** The `CoroutineScope` you provide to the container should be tied to the lifecycle of the component that "owns" KARL (e.g., a user session, a ViewModel, or the application itself). When this scope is cancelled, KARL's background tasks will be stopped.
* **`saveState()`:** Call this method when the application is about to lose state (e.g., on shutdown, when moving to the background). It's an asynchronous operation that returns a `Job`, which you can `.join()` if you need to ensure the save completes before exiting.
* **`release()`:** Call this method when the container is no longer needed. It will stop data observation and release resources held by the engine and storage implementations.

### 4. Integrating with a UI (e.g., Jetpack Compose)

To display KARL's output, connect it to your UI's state management system.

* **State Observation:** Use a state management holder (like a ViewModel or a custom state class) to interact with the `KarlContainer`. This holder should expose KARL's outputs (predictions, maturity metrics) as observable state, such as `StateFlow`.
* **Requesting Predictions:** Trigger calls to `karlContainer.getPrediction()` from your state holder in response to UI events or lifecycle changes.
* **Displaying Suggestions:** Your UI components (e.g., Composables) should observe the state (`StateFlow`) and recompose to display the latest prediction.
* **Feedback Loop:** Consider feeding user interactions with the suggestions (e.g., "suggestion-accepted," "suggestion-dismissed") back into KARL as new `InteractionData` to help it learn about the quality of its own predictions.

→ The [`:karl-compose-ui`](https://github.com/theaniketraj/project-karl/tree/main/karl-compose-ui) module provides pre-built components for visualizing KARL's state.

→ The [`:karl-example-desktop`](https://github.com/theaniketraj/project-karl/blob/main/karl-example-desktop/bin/main/com/karl/example/DesktopExampleApp.kt) module demonstrates how to wire them up.
