# Getting Started

This guide provides the essential steps to integrate Project KARL into your Kotlin application and run a basic example.

## 1. Prerequisites

Before you begin, ensure your development environment includes:

* **Kotlin:** Version 1.9.23 or higher.
* **JDK:** Version 11 or later.
* **Gradle:** Version 7.3 or higher.
* **IDE:** IntelliJ IDEA or Android Studio is highly recommended.

## 2. Project Setup

Integrating KARL involves adding its modules as dependencies via Gradle. We strongly recommend using a **Version Catalog (`libs.versions.toml`)** for managing dependencies.

### Step 2.1: Configure Repositories

Ensure your `settings.gradle.kts` is configured to resolve dependencies from `mavenCentral()` and `google()`.

→ For a complete reference, please see the [`settings.gradle.kts`](https://github.com/theaniketraj/project-karl/blob/main/settings.gradle.kts) file in our repository.

### Step 2.2: Add Dependencies

You need to add aliases for KARL's modules and their required dependencies to your `libs.versions.toml`. Then, add them to your application module's `build.gradle.kts`.

*Key dependencies for your app module:*

* `libs.karl.core`
* A `LearningEngine` implementation (e.g., `libs.karl.kldl`)
* A `DataStorage` implementation (e.g., `libs.karl.room`)
* Transitive dependencies required by the implementations (e.g., `libs.androidx.room.runtime`, `libs.kotlindl.api`).

→ For a complete, working setup, please refer to the [`libs.versions.toml`](https://github.com/theaniketraj/project-karl/blob/main/gradle/libs.versions.toml) and the [example application's `build.gradle.kts`](https://github.com/theaniketraj/project-karl/blob/main/karl-example-desktop/build.gradle.kts).

## 3. Your First Integration

The integration process involves three main steps within your application code:

### Step 3.1: Implement `DataSource`

Create a class that implements the `DataSource` interface. This class is responsible for observing user actions in your app and converting them into `InteractionData` objects for KARL.

*Conceptual Snippet:*

```kotlin
class MyAppDataSource(private val userId: String) : DataSource {
    override fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope
    ): Job {
        // ... logic to listen to your app's events ...
        // ... on event, create InteractionData and call onNewData(interaction) ...
        // ... return the observation Job ...
    }
}
```

### Step 3.2: Build and Initialize KarlContainer

In your application's startup logic, use the KarlAPI builder to construct and initialize a KarlContainer instance. You must provide your chosen engine, storage, and data source implementations, along with an application-managed CoroutineScope.
*Conceptual Snippet:*

```kotlin
// Instantiate your chosen implementations
val learningEngine: LearningEngine = KLDLLearningEngine()
val dataStorage: DataStorage = RoomDataStorage(database.dao())
val dataSource: DataSource = MyAppDataSource("user-123")

// Build and initialize the container
val karlContainer = Karl.forUser("user-123")
    .withLearningEngine(learningEngine)
    .withDataStorage(dataStorage)
    .withDataSource(dataSource)
    .withCoroutineScope(applicationScope)
    .build()

applicationScope.launch {
    karlContainer.initialize()
}
```

### Step 3.3: Get Predictions and Manage Lifecycle

Once initialized, you can request predictions. Remember to save the state and release resources when your application closes.

*Conceptual Snippet:*

```kotlin
// Get a prediction
val prediction = karlContainer.getPrediction()

// On app shutdown
karlContainer.saveState()
karlContainer.release()

```

## 4. Running the Example

The :karl-example-desktop module provides a complete, runnable demonstration of these concepts. To run it, execute the following command from the project root:

```bash
./gradlew :karl-example-desktop:run
```

→ We highly recommend exploring the source code of the [example application](https://github.com/theaniketraj/project-karl/blob/main/karl-example-desktop/src/main/kotlin/com/karl/example/DesktopExampleApp.kt) to see a practical implementation.
