# Getting Started with Project KARL

Welcome to Project KARL! This guide will walk you through the essential steps to integrate the KARL library into your Kotlin application and start leveraging privacy-first, on-device adaptive AI.

KARL empowers you to build intelligent features that learn from individual user behavior *locally*, without sending sensitive data to the cloud.

## Table of Contents

1.  [Introduction](#introduction)
2.  [Prerequisites](#prerequisites)
3.  [Installation](#installation)
    *   [Adding Repositories](#adding-repositories)
    *   [Adding Dependencies](#adding-dependencies)
    *   [Syncing Gradle](#syncing-gradle)
4.  [Core Concepts Recap](#core-concepts-recap)
5. [Modules-Overview](#modules-overview)
6. [Basic Usage Example](#basic-usage-example)
    *   [Step 1: Implement DataSource](#step-1-implement-datasource)
    *   [Step 2: Obtain a CoroutineScope](#step-2-obtain-a-coroutinescope)
    *   [Step 3: Instantiate Dependencies](#step-3-instantiate-dependencies)
    *   [Step 4: Build and Initialize KarlContainer](#step-4-build-and-initialize-karlcontainer)
    *   [Step 5: Get Predictions](#step-5-get-predictions)
    *   [Step 6: Manage Lifecycle (Save & Release)](#step-6-manage-lifecycle-save--release)
7. [Next Steps](#next-step)

---

## Introduction

This guide assumes you want to use the pre-built KARL implementation modules (like `:karl-kldl` for the engine and `:karl-sqldelight` for storage) within your existing Kotlin/JVM application (e.g., a Desktop app using Jetpack Compose, an Android app, or a standard JVM application).

We will set up the necessary dependencies and walk through creating, initializing, and interacting with a basic `KarlContainer`.

## Prerequisites

Before you begin, ensure you have the following installed and configured:

*   **JDK (Java Development Kit):** Version 11 or later is recommended.
*   **Gradle:** Version 7.x or later. Your project should already be using Gradle.
*   **IDE:** IntelliJ IDEA (Community or Ultimate) with the Kotlin plugin is highly recommended for the best development experience.

## Installation

Integrating KARL involves adding the necessary repositories and dependencies to your project's Gradle build scripts.

### Adding Repositories

Ensure your project can resolve dependencies from Maven Central, Google Maven (often needed for Android/Compose transitive dependencies), and the JetBrains Compose repository. Add these to your **root** `build.gradle.kts` (within `allprojects`) or `settings.gradle.kts` (within `dependencyResolutionManagement`):

```kotlin
// Example in root build.gradle.kts
allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // Add any other required repositories
    }
}
```

### Adding Dependencies
In the build.gradle.kts file of the specific application module where you want to use KARL, add the following dependencies:

```kotlin
dependencies {
// --- Required ---
// KARL Core: Contains essential interfaces and logic
// Replace 'com.your-group-id' and 'x.y.z' with actual coordinates and latest version
implementation("com.your-group-id:karl-core:x.y.z")

    // --- Choose Implementations (You MUST choose at least one Engine and one Storage) ---

    // Option 1: KotlinDL Learning Engine Implementation
    implementation("com.your-group-id:karl-kldl:x.y.z")

    // Option 2: SQLDelight Data Storage Implementation
    implementation("com.your-group-id:karl-sqldelight:x.y.z")
    // + You also need the SQLDelight Gradle plugin applied in this module
    // + and the appropriate SQLDelight runtime and driver (e.g., sqlite-driver)
    // See SQLDelight documentation for setup details. Example:
    // implementation("app.cash.sqldelight:sqlite-driver:2.0.1") // Or required driver

    // --- Optional ---

    // Option 3: KARL Compose UI Components (if using Jetpack Compose)
    // implementation("com.your-group-id:karl-compose-ui:x.y.z")
    // + Ensure you have core Jetpack Compose dependencies (ui, runtime, foundation, material3, etc.)

    // --- Other Dependencies ---
    // Kotlin Coroutines (needed for managing scope and async operations)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3") // Or compatible version
}
```

Important:

- Replace com.your-group-id with the actual Maven group ID under which KARL is published.
- Replace x.y.z with the latest stable version number of KARL.
- You must include implementations for LearningEngine and DataStorage. The examples above use :karl-kldl and :karl-sqldelight.
- If using :karl-sqldelight, ensure you follow the standard SQLDelight setup procedures (apply plugin, add drivers).

### Syncing Gradle

After adding the dependencies, sync your Gradle project in IntelliJ IDEA (usually prompted automatically, or via File > Sync Project with Gradle Files, or the Gradle tool window).

## Core Concepts Recap
Before diving into the code, remember the key players:
- `KarlContainer`: The main object you interact with for a specific user. It orchestrates everything.
- `LearningEngine`: The "brain" doing the learning and predicting. You provide an implementation (e.g., KLDLLearningEngine).
- `DataStorage`: Handles saving/loading the AI's learned state. You provide an implementation (e.g., SQLDelightDataStorage).
- `DataSource`: The bridge from your application to KARL. You must implement this interface to tell KARL about relevant user interactions.
- `CoroutineScope`: KARL uses coroutines for background tasks. You must provide a scope that is managed by your application's lifecycle (e.g., viewModelScope in Android, a custom scope for Desktop).

## Modules Overview

- `:karl-core`: Defines the core interfaces (`KarlContainer`, `LearningEngine`, `DataStorage`, `DataSource`), data models, and platform-agnostic orchestration logic. (Multiplatform)
- `:karl-kldl`: Provides an implementation of `LearningEngine` using the KotlinDL library. (JVM)
- `:karl-sqldelight`: Provides an implementation of DataStorage using the SQLDelight library for persistent local storage. (JVM, potentially Multiplatform)
- `:karl-compose-ui`: Contains optional, reusable Jetpack Compose UI components for visualizing KARL containers, suggestions, and progress. (Multiplatform/JVM)
- `:karl-example-desktop`: A sample Jetpack Compose Desktop application demonstrating how to integrate and use the KARL modules. (JVM)
- `/docs`: Contains detailed documentation for the project.

## Basic Usage Example
Let's create a minimal setup to initialize KARL and get a prediction.

### Step 1: Implement DataSource
First, you need to implement the DataSource interface in your application code. This class will be responsible for observing relevant user actions and sending InteractionData to KARL.

```kotlin
package com.karl

import com.karl.core.models.DataSource
import com.karl.core.models.InteractionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicInteger // For simple example

// --- Your Application Specific Logic ---
// Example: A simple counter representing user clicks
object UserActionSimulator {
private val clickCount = AtomicInteger(0)
fun simulateClick() {
clickCount.incrementAndGet()
// In a real app, this would be triggered by actual UI events
_actionFlow.tryEmit("button_clicked_${clickCount.get()}") // Emit simple action type
}

    // Use a Channel or StateFlow/SharedFlow in a real app to emit events
    private val _actionFlow = kotlinx.coroutines.channels.Channel<String>(kotlinx.coroutines.channels.Channel.BUFFERED)
    val actionFlow = _actionFlow.receiveAsFlow()
}
// --- End Your Application Specific Logic ---


// Implementation of the DataSource interface
class MyApplicationDataSource(
private val userId: String, // Needed to associate data with the user
private val actionFlow: Flow<String> // Flow emitting simple action types from your app
) : DataSource {

    override fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope
    ): Job {
        println("MyApplicationDataSource: Starting observation for user $userId")

        // Observe the flow of actions from your application
        return actionFlow
            .onEach { actionType ->
                println("MyApplicationDataSource: Observed action '$actionType'")
                // Create InteractionData from the application event
                val interaction = InteractionData(
                    type = actionType, // e.g., "button_clicked", "command_executed"
                    details = mapOf("info" to "Example detail for $actionType"), // Add relevant metadata
                    timestamp = System.currentTimeMillis(),
                    userId = userId
                )
                // Send the structured data to the KARL container
                onNewData(interaction)
            }
            .launchIn(coroutineScope) // Launch the collector in the provided scope
    }
}
```
### Step 2: Obtain a CoroutineScope

KARL needs a CoroutineScope to manage its background tasks (like learning, saving). This scope must be managed by your application's lifecycle. If you cancel this scope, KARL's operations will be cancelled.

```kotlin
import kotlinx.coroutines.*

// Example: Creating a scope (In a real app, use lifecycle-aware scopes)
// For Desktop Compose, you might use rememberCoroutineScope()
// For Android, use viewModelScope or lifecycleScope
// For simple JVM, manage it manually:
val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

// Remember to cancel this scope when your app/component shuts down!
// e.g., applicationScope.cancel()
```

### Step 3: Instantiate Dependencies

Create instances of the LearningEngine and DataStorage implementations you added as dependencies. This step depends heavily on the specific constructor requirements of those implementation classes (e.g., providing database drivers, context, configuration).

```kotlin
// Example Placeholder Instantiation (Replace with actual constructors)
import com.karl.kldl.KLDLLearningEngine // Assuming these are your implementation classes
import com.karl.sqldelight.SQLDelightDataStorage
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver // Example driver

val userId = "user-123" // The specific user for this container

// Instantiate Learning Engine (Replace with actual constructor)
val learningEngine: LearningEngine = KLDLLearningEngine(/* constructor args if any */)

// Instantiate Data Storage (Replace with actual constructor and setup)
// This requires proper SQLDelight driver setup
val dbDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + "karl_$userId.db") // Example in-memory DB per user
// TODO: Run DB schema creation if needed: KarlDatabase.Schema.create(dbDriver)
val dataStorage: DataStorage = SQLDelightDataStorage(dbDriver /* constructor args if any */)

// Instantiate your DataSource implementation
val dataSource: DataSource = MyApplicationDataSource(userId, UserActionSimulator.actionFlow)
```

### Step 4: Build and Initialize KarlContainer

Use the KarlAPI builder to assemble the container, then initialize it within your applicationScope.

```kotlin
import com.karl.core.api.Karl

// Build the container
val karlContainer: KarlContainer = Karl.forUser(userId)
.withLearningEngine(learningEngine)
.withDataStorage(dataStorage)
.withDataSource(dataSource)
.withCoroutineScope(applicationScope)
// .withInstructions(...) // Optional
.build()

// Initialize asynchronously
val initializationJob = applicationScope.launch {
try {
println("MainApp: Initializing KARL container...")
// Pass dependencies again (interface design might evolve)
karlContainer.initialize(
learningEngine = learningEngine,
dataStorage = dataStorage,
dataSource = dataSource,
coroutineScope = applicationScope
// instructions = listOf(...)
)
println("MainApp: KARL container initialized successfully!")
// Now KARL is observing data from your DataSource and learning in the background

    } catch (e: Exception) {
        println("MainApp: ERROR initializing KARL: ${e.message}")
        e.printStackTrace()
        // Handle errors appropriately (e.g., disable AI features)
    }
}

// Optional: Wait for initialization if needed for subsequent steps
// initializationJob.join()
```

### Step 5: Get Predictions

When you need a suggestion from KARL (e.g., to update the UI), call getPrediction().

```kotlin
// Example: Get a prediction after initialization (or later on user action)
applicationScope.launch {
initializationJob.join() // Ensure initialization is complete

    // Simulate some user actions that trigger the DataSource
    UserActionSimulator.simulateClick()
    delay(100) // Give some time for processing/learning (in real app, prediction follows action)
    UserActionSimulator.simulateClick()
    delay(100)

    println("MainApp: Requesting prediction from KARL...")
    val prediction = karlContainer.getPrediction()

    if (prediction != null) {
        println("MainApp: KARL Suggestion Received: ${prediction.suggestion} (Confidence: ${prediction.confidence})")
        // TODO: Update your application's UI or behavior based on the prediction
    } else {
        println("MainApp: KARL provided no suggestion yet.")
    }
}
```

### Step 6: Manage Lifecycle (Save & Release)

It's crucial to manage the container's lifecycle:

- *Save State*: Call `karlContainer.saveState()` periodically and/or when your application is closing to persist the AI's learned knowledge.
- *Release Resources*: Call `karlContainer.release()` when the container is no longer needed (e.g., user logs out, application closes) to stop observation and clean up resources held by the engine and storage. Cancel the  ` applicationScope` when appropriate.

```kotlin
// Example: Cleanup logic when your app/component is closing
fun performCleanup() {
println("MainApp: Cleaning up KARL...")
applicationScope.launch {
try {
println("MainApp: Saving KARL state...")
karlContainer.saveState().join() // Wait for save to complete
println("MainApp: Releasing KARL resources...")
karlContainer.release()
println("MainApp: KARL cleanup finished.")
} catch (e: Exception) {
println("MainApp: Error during KARL cleanup: ${e.message}")
} finally {
// Finally, cancel the scope you provided
applicationScope.cancel("Application shutting down")
println("MainApp: Application scope cancelled.")
}
}
// In a real app, wait for the cleanup coroutine if necessary before exiting fully.
}

// --- Call cleanup ---
// Example: Add JVM shutdown hook
// Runtime.getRuntime().addShutdownHook(Thread { performCleanup() })
// Or call from your application's specific exit/destroy lifecycle method.
```

## Next Step

Congratulations! You've set up a basic KARL container. Now you can explore further:

- *Implement a Real* `DataSource`: Connect KARL to meaningful user interactions in your app.
- *Refine* `InteractionData`: Decide exactly what metadata KARL should learn from.
- Use Predictions: Integrate the suggestions from `getPrediction()` into your UI or application logic.
- *Explore Instructions*: Implement user controls or application logic using `KarlInstruction`.
- *Configure Implementations*: Dive deeper into the configuration options for your chosen `LearningEngine` and `DataStorage`.
- *Check the Example App*: Explore the `:karl-example-desktop` module for a more complete integration example.
- *Read Full Documentation*: Consult the `/docs` directory or the main [README.md](https://github.com/theaniketraj/project-karl/blob/main/README.md) for more in-depth information.

We hope this guide helps you get started with building amazing, private AI experiences with Project KARL!
