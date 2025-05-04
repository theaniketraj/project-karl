package com.karl.example

import androidx.compose.desktop.ui.tooling.preview.Preview // Required for @Preview (though less common in main app file)
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver // Example Driver
// Import KARL API and Core Interfaces/Models
import com.karl.core.api.*
import com.karl.core.data.DataSource
import com.karl.core.data.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.Prediction
// Import KARL Implementations (Replace with your actual implementation packages/classes)
import com.karl.kldl.KLDLLearningEngine // Assuming this path is correct
import com.karl.sqldelight.KarlDatabase // Assuming SQLDelight generates this
import com.karl.sqldelight.SQLDelightDataStorage // Assuming this path is correct
// Import KARL UI Components
import com.karl.ui.KarlContainerUI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicLong // For generating simple unique IDs/timestamps

// --- 1. Simple DataSource Implementation for the Example ---
class ExampleDataSource(
    private val userId: String,
    // Use a SharedFlow to allow emitting events from button clicks
    private val externalActionFlow: SharedFlow<String>
) : DataSource {
    override fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope
    ): Job {
        println("ExampleDataSource: Starting observation for $userId")
        return externalActionFlow
            .onEach { actionType ->
                println("ExampleDataSource: Observed action '$actionType'")
                val interaction = InteractionData(
                    type = actionType,
                    details = mapOf("source" to "example_button"),
                    timestamp = System.currentTimeMillis(),
                    userId = userId
                )
                onNewData(interaction) // Pass data to KarlContainer
            }
            .launchIn(coroutineScope) // Use the provided scope
    }
}

// --- 2. Main Application Entry Point ---
fun main() = application {
    val windowState = rememberWindowState(width = 800.dp, height = 600.dp)
    // Create a scope tied to the application lifecycle for cleanup
    val applicationScope = rememberCoroutineScope { SupervisorJob() + Dispatchers.Default }
    var karlContainer: KarlContainer? by remember { mutableStateOf(null) } // Hold the container instance

    // --- State for UI ---
    // StateFlow to hold the latest prediction for the UI
    val predictionState = remember { MutableStateFlow<Prediction?>(null) }
    // StateFlow for simulated learning progress
    val learningProgressState = remember { MutableStateFlow(0.0f) }
    // SharedFlow for triggering actions from buttons to the DataSource
    val actionFlow = remember { MutableSharedFlow<String>(extraBufferCapacity = 5) }

    // --- Lifecycle Management ---
    // Use LaunchedEffect for one-time setup/initialization when the app starts
    LaunchedEffect(Unit) {
        println("App LaunchedEffect: Setting up KARL...")
        val userId = "example-user-01" // Static user for the example

        // Instantiate KARL dependencies (replace with actual setup if needed)
        try {
            val learningEngine: LearningEngine = KLDLLearningEngine() // Simple instantiation
            val dbDriver = JdbcSqliteDriver("jdbc:sqlite:karl_example_$userId.db")
            KarlDatabase.Schema.create(dbDriver) // Create DB schema if it doesn't exist
            val dataStorage: DataStorage = SQLDelightDataStorage(KarlDatabase(dbDriver)) // Use generated DB class
            val dataSource: DataSource = ExampleDataSource(userId, actionFlow) // Use the actionFlow

            // Build the container
            val container = Karl.forUser(userId)
                .withLearningEngine(learningEngine)
                .withDataStorage(dataStorage)
                .withDataSource(dataSource)
                .withCoroutineScope(applicationScope) // Use the app-level scope
                .build()

            // Initialize (must be called!)
            container.initialize(
                learningEngine, dataStorage, dataSource, emptyList(), applicationScope
            )

            karlContainer = container // Store the initialized container
            println("App LaunchedEffect: KARL setup complete.")

        } catch (e: Exception) {
            println("App LaunchedEffect: ERROR setting up KARL: ${e.message}")
            e.printStackTrace()
            // Handle error (e.g., show error message in UI)
        }
    }

    // --- Main Application Window ---
    Window(
        onCloseRequest = {
            println("App onCloseRequest: Cleaning up...")
            // Launch cleanup in the application scope
            applicationScope.launch {
                karlContainer?.saveState()?.join() // Save state and wait
                karlContainer?.release() // Release resources
                println("App onCloseRequest: KARL cleanup finished.")
            }.invokeOnCompletion { // Ensure application exits after cleanup
                exitApplication()
            }
        },
        state = windowState,
        title = "Project KARL - Example Desktop App"
    ) {
        // Use Material 3 Theme (or Material 2)
        MaterialTheme {
            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Project KARL Example", style = MaterialTheme.typography.headlineMedium)

                    // Display the KARL UI Container
                    // Only show if initialization was successful
                    if (karlContainer != null) {
                        KarlContainerUI(
                            predictionState = predictionState, // Pass the StateFlow
                            learningProgressState = learningProgressState // Pass the StateFlow
                            // Add callbacks here if KarlContainerUI had buttons
                        )
                    } else {
                        Text("KARL Container failed to initialize. Check logs.", color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Simulate User Actions:", style = MaterialTheme.typography.titleMedium)

                    // Buttons to trigger the DataSource via the SharedFlow
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = {
                            // Launch coroutine to handle action + prediction update
                            applicationScope.launch {
                                println("Button Clicked: Action A")
                                actionFlow.emit("action_type_A") // Send to DataSource
                                // Simulate progress increase slightly
                                learningProgressState.update { (it + 0.05f).coerceAtMost(1.0f) }
                                // Get and update prediction after action
                                val prediction = karlContainer?.getPrediction()
                                predictionState.value = prediction
                                println("Prediction after Action A: $prediction")
                            }
                        }, enabled = karlContainer != null) {
                            Text("Perform Action A")
                        }

                        Button(onClick = {
                            applicationScope.launch {
                                println("Button Clicked: Action B")
                                actionFlow.emit("action_type_B") // Send to DataSource
                                learningProgressState.update { (it + 0.05f).coerceAtMost(1.0f) }
                                val prediction = karlContainer?.getPrediction()
                                predictionState.value = prediction
                                println("Prediction after Action B: $prediction")
                            }
                        }, enabled = karlContainer != null) {
                            Text("Perform Action B")
                        }

                        Button(onClick = {
                            // Explicitly request prediction without specific action
                            applicationScope.launch {
                                println("Button Clicked: Get Prediction")
                                val prediction = karlContainer?.getPrediction()
                                predictionState.value = prediction
                                println("Explicit Prediction Request: $prediction")
                            }
                        }, enabled = karlContainer != null) {
                            Text("Get Prediction")
                        }
                    }
                }
            }
        }
    }
}