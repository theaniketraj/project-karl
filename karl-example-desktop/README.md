# KARL Example: Desktop Application

This module (`:karl-example-desktop`) provides a runnable Jetpack Compose Desktop application demonstrating the integration and basic usage of the Project KARL library.

It serves as a practical example of how to:

*   Instantiate and configure KARL components (`LearningEngine`, `DataStorage`, `DataSource`).
*   Initialize and manage the lifecycle of a `KarlContainer`.
*   Integrate KARL with a user interface using the `:karl-compose-ui` module.
*   Simulate user interactions to feed data into KARL.
*   Retrieve and display predictions from KARL.

## Overview

This example application simulates a very basic scenario where user actions (represented by button clicks) are fed into a KARL container. The container uses:

*   **`:karl-core`**: For the core interfaces and orchestration logic.
*   **`:karl-kldl`**: As the `LearningEngine` implementation (using KotlinDL).
*   **`:karl-sqldelight`**: As the `DataStorage` implementation (using SQLDelight with an embedded SQLite database).
*   **`:karl-compose-ui`**: For the `KarlContainerUI` and `KarlLearningProgressIndicator` composables.

The application displays the status of the KARL container, a simulated learning progress bar, the latest prediction received, and buttons to trigger sample user actions ("Action A", "Action B").

**Note:** The learning model (`KLDLLearningEngine` with its default simple MLP) in this example is minimal. It will adapt slightly based on button clicks, but don't expect complex, nuanced predictions without further model refinement and more realistic interaction data. The focus here is on demonstrating the *integration* mechanism.

## Prerequisites

*   JDK 11+
*   Gradle (managed by the wrapper included in the root project)
*   An operating system supported by Jetpack Compose for Desktop (Windows, macOS, Linux)

## Building and Running

Ensure you have successfully built the parent project (`project-karl`) first, as this module depends on the other KARL modules.

You can run this example application using Gradle from the root directory of the `project-karl`:

```pgsql
# Navigate to the root project directory
cd /path/to/project-karl

# Clean and build the entire project (optional but recommended first time)
./gradlew clean build

# Run the example desktop application
./gradlew :karl-example-desktop:run
```

Alternatively, you can set up an "Application" run configuration in IntelliJ IDEA:
1. Go to Run > Edit Configurations...
2. Click + and select Application.
3. Name: Run Karl Example Desktop (or similar)
4. Main class: `com.karl.example.DesktopExampleAppKt`.
5. Module: Select `karl-project.karl-example-desktop.jvmMain`.
6. Click Apply and OK.
7. You can now run this configuration from the IDE.

**Code Structure** (`karl-example-desktop/src/jvmMain/kotlin/`)
- `com/karl/example/DesktopExampleApp.kt`: Contains the `main()` entry point, Jetpack Compose UI setup, KARL component instantiation, LaunchedEffect for initialization, button click handlers, and lifecycle management (`onCloseRequest`).
- `com/karl/example/ExampleDataSource.kt`: (Included within `DesktopExampleApp.kt` in the previous example, but could be a separate file). Implements the DataSource interface, connecting button clicks (via a `SharedFlow`) to `InteractionData` emissions for KARL.

## Technical Details & Implementation Notes

- **Threading & Coroutines**: The application uses `rememberCoroutineScope` tied to the Compose `application` lifecycle. KARL initialization, prediction requests, and interaction processing are launched within this scope using `launch { ... }` to avoid blocking the main UI thread.
- **State Management**: `MutableStateFlow` is used to hold the latest `Prediction` and simulated learning progress, allowing the `KarlContainerUI` composable to reactively update via `collectAsState()`.
- **Dependency Injection (Basic)**: Instances of `KLDLLearningEngine`, `SQLDelightDataStorage`, and `ExampleDataSource` are created directly within the `LaunchedEffect`. In a larger application, a proper dependency injection framework (like Koin, Hilt - although Hilt is Android-focused, or manual injection) would be used.
- **Database**: An embedded SQLite database file (`karl_example_example-user-01.db`) will be created in the directory where the application is run. It uses `JdbcSqliteDriver`. Schema creation (`KarlDatabase.Schema.create`) is called within the `LaunchedEffect` for simplicity (idempotent).
- **Lifecycle**: The Window's `onCloseRequest` lambda ensures `karlContainer.saveState()` and `karlContainer.release()` are called before `exitApplication()`, providing a clean shutdown path.
- **Error Handling**: Basic `try-catch` blocks are used during KARL initialization. Robust error handling would be needed for a production application.

## Exploring Further
- Modify the `InteractionData` created in `ExampleDataSource` to include more details.
- Change the `actionType` strings emitted by the buttons.
- Observe the console output to see log messages from KARL components.
- Inspect the created SQLite database file (`karl_example_... .db`) using a database tool to see how state is stored (though the model state itself might be a binary blob).
- Modify the `KarlContainerUI` composable in the `:karl-compose-ui` module to display more information or add controls.
- (Advanced) Swap out the `KLDLLearningEngine` for a custom implementation or configure its internal model differently (requires changes in the `:karl-kldl` module).