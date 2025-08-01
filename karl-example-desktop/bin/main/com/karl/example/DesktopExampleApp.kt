package com.karl.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import api.KarlContainer
import api.LearningEngine
import com.karl.core.api.Karl
import com.karl.core.models.DataSource
import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.Prediction
import kotlinx.coroutines.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.awt.Desktop
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Data structure for structured interaction logs
data class StructuredInteraction(
    val timestamp: String,
    val action: String,
    val prediction: String,
    val confidence: Float,
)

// Data structure for real-time interaction log entries
data class LogEntry(
    val timestamp: String,
    val action: String,
    val prediction: String,
    val confidence: Float,
)

// Custom GitHub Icon
private var gitHubIconCache: ImageVector? = null

val GitHubIcon: ImageVector
    get() {
        if (gitHubIconCache != null) {
            return gitHubIconCache!!
        }
        gitHubIconCache =
            ImageVector.Builder(
                name = "GitHub",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        stroke = null,
                        strokeLineWidth = 0f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Miter,
                        strokeLineMiter = 4f,
                        pathFillType =
                            androidx.compose.ui.graphics.PathFillType.NonZero,
                    ) {
                        // GitHub icon path
                        moveTo(12f, 0f)
                        curveTo(5.374f, 0f, 0f, 5.373f, 0f, 12f)
                        curveTo(0f, 17.302f, 3.438f, 21.8f, 8.207f, 23.387f)
                        curveTo(8.806f, 23.498f, 9.025f, 23.126f, 9.025f, 22.81f)
                        curveTo(9.025f, 22.524f, 9.015f, 21.924f, 9.01f, 21.148f)
                        curveTo(5.672f, 21.914f, 4.968f, 19.752f, 4.968f, 19.752f)
                        curveTo(4.422f, 18.377f, 3.633f, 18.007f, 3.633f, 18.007f)
                        curveTo(2.546f, 17.225f, 3.717f, 17.241f, 3.717f, 17.241f)
                        curveTo(4.922f, 17.328f, 5.555f, 18.514f, 5.555f, 18.514f)
                        curveTo(6.625f, 20.366f, 8.364f, 19.835f, 9.05f, 19.531f)
                        curveTo(9.158f, 18.766f, 9.467f, 18.237f, 9.81f, 17.941f)
                        curveTo(7.145f, 17.641f, 4.344f, 16.594f, 4.344f, 11.816f)
                        curveTo(4.344f, 10.489f, 4.809f, 9.406f, 5.579f, 8.554f)
                        curveTo(5.444f, 8.252f, 5.039f, 6.963f, 5.684f, 5.227f)
                        curveTo(5.684f, 5.227f, 6.689f, 4.907f, 8.984f, 6.548f)
                        curveTo(9.944f, 6.288f, 10.964f, 6.158f, 11.984f, 6.153f)
                        curveTo(13.004f, 6.158f, 14.024f, 6.288f, 14.984f, 6.548f)
                        curveTo(17.264f, 4.907f, 18.269f, 5.227f, 18.269f, 5.227f)
                        curveTo(18.914f, 6.963f, 18.509f, 8.252f, 18.389f, 8.554f)
                        curveTo(19.154f, 9.406f, 19.619f, 10.489f, 19.619f, 11.816f)
                        curveTo(19.619f, 16.607f, 16.814f, 17.638f, 14.144f, 17.931f)
                        curveTo(14.564f, 18.311f, 14.954f, 19.062f, 14.954f, 20.207f)
                        curveTo(14.954f, 21.837f, 14.939f, 23.148f, 14.939f, 23.544f)
                        curveTo(14.939f, 23.864f, 15.155f, 24.239f, 15.764f, 24.124f)
                        curveTo(20.565f, 22.529f, 24f, 18.033f, 24f, 12f)
                        curveTo(24f, 5.373f, 18.626f, 0f, 12f, 0f)
                        close()
                    }
                }
                .build()
        return gitHubIconCache!!
    }

// --- 1. Simple DataSource Implementation for the Example ---
class ExampleDataSource(
    private val userId: String,
    // Use a SharedFlow to allow emitting events from button clicks
    private val externalActionFlow: SharedFlow<String>,
) : DataSource {
    init {
        println("DEBUG: ExampleDataSource constructor - userId=$userId, externalActionFlow=$externalActionFlow")
    }

    override fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope,
    ): Job {
        println("ExampleDataSource: Starting observation for $userId")
        println("DEBUG: externalActionFlow = $externalActionFlow")
        println("DEBUG: coroutineScope = $coroutineScope")
        val job =
            externalActionFlow
                .onEach { actionType ->
                    try {
                        val interaction =
                            InteractionData(
                                type = actionType,
                                details = mapOf("source" to "example_button", "timestamp_ms" to System.currentTimeMillis()),
                                timestamp = System.currentTimeMillis(),
                                userId = userId,
                            )
                        // --- Add these logs for Phase 1 verification ---
                        println("DataSource: Received action '$actionType'.")
                        println("DataSource: Created InteractionData -> $interaction")
                        // --- End of Phase 1 logs ---
                        println("DataSource: About to call onNewData(interaction)...")
                        onNewData(interaction) // Pass data to KarlContainer
                        println("DataSource: Successfully passed data to KarlContainer!")
                    } catch (e: Exception) {
                        println("DataSource ERROR: Exception occurred while processing action '$actionType': ${e.message}")
                        e.printStackTrace()
                    }
                }
                .launchIn(coroutineScope) // Use the provided scope
        println("DEBUG: Started flow observation job = $job")
        return job
    }
}

// --- 2. Simple In-Memory DataStorage Implementation for the Example ---
class InMemoryDataStorage : DataStorage {
    private val interactions = mutableListOf<InteractionData>()
    private val containerStates = mutableMapOf<String, com.karl.core.models.KarlContainerState>()

    override suspend fun initialize() {
        println("InMemoryDataStorage: Initialized")
    }

    override suspend fun saveContainerState(
        userId: String,
        state: com.karl.core.models.KarlContainerState,
    ) {
        containerStates[userId] = state
        println("InMemoryDataStorage: Saved container state for user: $userId")
    }

    override suspend fun loadContainerState(userId: String): com.karl.core.models.KarlContainerState? {
        return containerStates[userId]
    }

    override suspend fun saveInteractionData(data: InteractionData) {
        interactions.add(data)
        println("InMemoryDataStorage: Stored interaction: $data")
    }

    override suspend fun loadRecentInteractionData(
        userId: String,
        limit: Int,
        type: String?,
    ): List<InteractionData> {
        return interactions
            .filter { it.userId == userId }
            .let {
                if (type != null) it.filter { interaction -> interaction.type == type } else it
            }
            .takeLast(limit)
    }

    override suspend fun deleteUserData(userId: String) {
        interactions.removeAll { it.userId == userId }
        containerStates.remove(userId)
        println("InMemoryDataStorage: Deleted data for user: $userId")
    }

    override suspend fun release() {
        interactions.clear()
        containerStates.clear()
        println("InMemoryDataStorage: Released all resources")
    }
}

// Simple Sparkline Chart Composable
@Composable
fun SparklineChart(
    data: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    lineWidth: Float = 2f,
) {
    Canvas(modifier = modifier) {
        if (data.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val maxValue = data.maxOrNull() ?: 1f
        val minValue = data.minOrNull() ?: 0f
        val range = maxValue - minValue

        if (range == 0f) return@Canvas

        val stepX = width / (data.size - 1)

        for (i in 0 until data.size - 1) {
            val x1 = i * stepX
            val y1 = height - ((data[i] - minValue) / range) * height
            val x2 = (i + 1) * stepX
            val y2 = height - ((data[i + 1] - minValue) / range) * height

            drawLine(
                color = color,
                start = androidx.compose.ui.geometry.Offset(x1, y1),
                end = androidx.compose.ui.geometry.Offset(x2, y2),
                strokeWidth = lineWidth,
            )
        }

        // Draw dots at each data point
        data.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minValue) / range) * height
            drawCircle(
                color = color,
                radius = lineWidth,
                center = androidx.compose.ui.geometry.Offset(x, y),
            )
        }
    }
}

// --- 2. Main Application Entry Point ---
@OptIn(ExperimentalFoundationApi::class)
fun main() =
    application {
        val windowState = rememberWindowState() // Remove fixed size, let it be resizable
        // Create a scope tied to the application lifecycle for cleanup
        val applicationScope =
            remember {
                CoroutineScope(SupervisorJob() + Dispatchers.Default)
            }
        var karlContainer: KarlContainer? by remember {
            mutableStateOf(null)
        } // Hold the container instance
        var learningEngine: LearningEngine? by remember {
            mutableStateOf(null)
        } // Hold the learning engine instance for getting insights

        // --- State for UI ---
        // StateFlow to hold the latest prediction for the UI
        val predictionState = remember { MutableStateFlow<Prediction?>(null) }
        // StateFlow for simulated learning progress
        val learningProgressState = remember { MutableStateFlow(0.0f) }
        // StateFlow for loading state when getting predictions
        val isLoadingPrediction = remember { MutableStateFlow(false) }
        // SharedFlow for triggering actions from buttons to the DataSource
        val actionFlow = remember { MutableSharedFlow<String>(extraBufferCapacity = 5) }
        // State for theme toggle (true = dark theme, false = light theme)
        var isDarkTheme by remember { mutableStateOf(true) }
        // State for enlarged sections
        var enlargedSection by remember {
            mutableStateOf<String?>(null)
        } // "insights", "prediction", "controls", or null

        // --- Dynamic Data State for Three Panels ---
        // Panel 1: AI Insights data - Real-time state flows
        val systemStatusState = remember { MutableStateFlow("Initializing") }
        val systemStatus by systemStatusState.collectAsState()
        val interactionCountState = remember { MutableStateFlow(0L) }
        val interactionsProcessed by interactionCountState.collectAsState()
        val averageConfidenceState = remember { MutableStateFlow(0.0f) }
        val averageConfidence by averageConfidenceState.collectAsState()
        val recentInteractionsState = remember { MutableStateFlow<List<String>>(emptyList()) }
        val recentInteractions by recentInteractionsState.collectAsState()

        // Confidence history for sparkline chart - real-time StateFlow
        val confidenceHistoryState =
            remember { MutableStateFlow<List<Float>>(listOf(0.75f, 0.82f, 0.79f, 0.87f, 0.92f, 0.85f, 0.89f, 0.91f, 0.83f, 0.87f)) }
        val confidenceHistoryData by confidenceHistoryState.collectAsState()

        // Real-time structured interaction log - StateFlow for live updates
        val structuredLogState = remember { MutableStateFlow<List<LogEntry>>(emptyList()) }
        val realtimeStructuredLog by structuredLogState.collectAsState()

        // Panel 2: Prediction Details data - Real-time state flows
        val lastActionState = remember { MutableStateFlow("No action yet") }
        val lastAction by lastActionState.collectAsState()
        val processingTimeState = remember { MutableStateFlow(0L) }
        val processingTime by processingTimeState.collectAsState()
        val predictionValue by predictionState.collectAsState()

        var modelArchitecture by remember { mutableStateOf("MLP(3,16,8,4)") }
        var interactionLog by remember { mutableStateOf(listOf("User clicked 'build'", "Analysis complete", "Model updated")) }

        // Enhanced structured interaction log with timestamps and predictions
        var structuredInteractionLog by remember {
            mutableStateOf(
                listOf(
                    StructuredInteraction("14:23:15", "build", "run tests", 0.87f),
                    StructuredInteraction("14:22:48", "git pull", "resolve conflicts", 0.92f),
                    StructuredInteraction("14:21:33", "open file", "edit code", 0.79f),
                ),
            )
        }

        // Action feedback state for visual feedback
        var actionFeedbackMessage by remember { mutableStateOf<String?>(null) }
        var showActionFeedback by remember { mutableStateOf(false) }

        // Panel 2: Prediction Details data
        var lastActionProcessed by remember { mutableStateOf("build project") }
        var inputFeatures by remember { mutableStateOf(42) }
        var confidenceScore by remember { mutableStateOf(0.87f) }
        var adaptivePredictions by remember { mutableStateOf(listOf("Next: run tests", "Likely: commit changes", "Alternative: debug")) }

        // --- Helper function to update learning progress and insights ---
        fun updateLearningProgress() {
            applicationScope.launch {
                learningEngine?.let { engine ->
                    try {
                        val insights = engine.getLearningInsights()
                        learningProgressState.update { insights.progressEstimate }

                        // Update AI Insights panel data
                        interactionCountState.value = insights.interactionCount
                        val averageConfidence = insights.customMetrics["averageConfidence"] as? Float ?: 0.5f
                        averageConfidenceState.value = averageConfidence

                        // Update confidence history for sparkline
                        val historyFromEngine = insights.customMetrics["confidenceHistory"] as? List<Float>
                        if (historyFromEngine != null && historyFromEngine.isNotEmpty()) {
                            confidenceHistoryState.value = historyFromEngine
                        }

                        println(
                            "Progress: ${insights.interactionCount} interactions, " +
                                "${(insights.progressEstimate * 100).toInt()}%, " +
                                "confidence: ${(averageConfidence * 100).toInt()}%",
                        )
                    } catch (e: Exception) {
                        println("Error getting learning insights: ${e.message}")
                    }
                }
            }
        }

        // Helper function to add interaction log entry
        fun addInteractionLogEntry(
            action: String,
            prediction: String,
            confidence: Float,
        ) {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val logEntry = "[$timestamp] Action: '$action' -> Predicted: '$prediction' (${(confidence * 100).toInt()}%)"

            // Update the string-based log for existing UI components
            val currentList = recentInteractionsState.value
            val newList = (listOf(logEntry) + currentList).take(10) // Keep only last 10 entries
            recentInteractionsState.value = newList

            // Update the structured log for enhanced UI components
            val structuredEntry =
                LogEntry(
                    timestamp = timestamp,
                    action = action,
                    prediction = prediction,
                    confidence = confidence,
                )
            val currentStructuredList = structuredLogState.value
            val newStructuredList = (listOf(structuredEntry) + currentStructuredList).take(8) // Keep last 8 entries for display
            structuredLogState.value = newStructuredList
        }

        // Helper function to handle prediction with timing measurement
        suspend fun handlePredictionWithTiming(
            actionType: String,
            actionDescription: String,
            emitToActionFlow: Boolean = true,
        ) {
            // Update last action state
            lastActionState.value = actionDescription
            systemStatusState.value = "Processing prediction..."

            // Emit to actionFlow only if specified (not for "Get Prediction" button)
            if (emitToActionFlow) {
                actionFlow.emit(actionType)
            }

            // Measure prediction time
            val startTime = System.currentTimeMillis()
            val prediction = karlContainer?.getPrediction()
            val endTime = System.currentTimeMillis()
            val measuredProcessingTime = endTime - startTime

            // Update states
            processingTimeState.value = measuredProcessingTime
            predictionState.value = prediction

            // Add to interaction log
            prediction?.let { pred ->
                addInteractionLogEntry(actionType, pred.suggestion, pred.confidence)
            }

            // Update learning insights after interaction
            updateLearningProgress()

            systemStatusState.value = "Ready"
            println("Prediction for '$actionType' completed in ${measuredProcessingTime}ms: $prediction")
        }

        // --- Helper Functions for Dynamic Data ---
        fun simulateAction(action: String) {
            // Set system status to processing
            systemStatusState.value = "Processing"

            lastActionProcessed = action
            interactionLog = (interactionLog + "Action: $action").takeLast(5)
            confidenceScore = kotlin.random.Random.nextFloat() * 0.25f + 0.7f // 0.7f to 0.95f

            // Visual feedback
            actionFeedbackMessage = "Action '$action' sent!"
            showActionFeedback = true

            // Generate prediction based on action
            val prediction =
                when (action) {
                    "git push" -> "wait for CI"
                    "git pull" -> "resolve conflicts"
                    "run tests" -> "fix failures"
                    "build project" -> "run tests"
                    else -> "analyze context"
                }

            // Add to structured interaction log
            val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val newStructuredInteraction = StructuredInteraction(currentTime, action, prediction, confidenceScore)
            structuredInteractionLog = (structuredInteractionLog + newStructuredInteraction).takeLast(8)

            // Add to real-time interaction log
            addInteractionLogEntry(action, prediction, confidenceScore)

            adaptivePredictions =
                when (action) {
                    "git push" -> listOf("Next: wait for CI", "Monitor: build status", "Consider: review PR")
                    "git pull" -> listOf("Next: resolve conflicts", "Check: new dependencies", "Update: local branch")
                    "run tests" -> listOf("Next: fix failures", "Check: coverage report", "Consider: add tests")
                    "build project" -> listOf("Next: run tests", "Check: warnings", "Deploy: staging")
                    else -> listOf("Next: analyze context", "Monitor: system state", "Adapt: strategy")
                }

            // Update learning insights
            updateLearningProgress()

            // Reset system status to ready after a delay
            applicationScope.launch {
                delay(1000) // Simulate processing time
                systemStatusState.value = "Ready"
            }
        }

        fun runScenario(scenario: String) {
            // Set system status to processing
            systemStatusState.value = "Processing"

            interactionLog = (interactionLog + "Scenario: $scenario started").takeLast(5)

            // Visual feedback
            actionFeedbackMessage = "Scenario '$scenario' started!"
            showActionFeedback = true

            val confidence = kotlin.random.Random.nextFloat() * 0.15f + 0.8f // 0.8f to 0.95f
            val prediction =
                when (scenario) {
                    "Heavy Load Test" -> "optimize performance"
                    "Data Migration" -> "validate integrity"
                    else -> "monitor results"
                }

            // Add to structured interaction log
            val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            val newStructuredInteraction = StructuredInteraction(currentTime, scenario.lowercase(), prediction, confidence)
            structuredInteractionLog = (structuredInteractionLog + newStructuredInteraction).takeLast(8)

            // Add to real-time interaction log
            addInteractionLogEntry(scenario.lowercase(), prediction, confidence)

            when (scenario) {
                "Heavy Load Test" -> {
                    adaptivePredictions = listOf("Optimize: memory usage", "Scale: horizontally", "Monitor: performance")
                }
                "Data Migration" -> {
                    adaptivePredictions = listOf("Validate: data integrity", "Backup: before migration", "Test: rollback procedure")
                }
            }

            // Update learning insights
            updateLearningProgress()

            // Reset system status to ready after a delay
            applicationScope.launch {
                delay(1500) // Simulate longer processing time for scenarios
                systemStatusState.value = "Ready"
            }
        }

        // --- Lifecycle Management ---
        // Use LaunchedEffect for one-time setup/initialization when the app starts
        LaunchedEffect(Unit) {
            systemStatusState.value = "Initializing"
            println("App LaunchedEffect: Setting up KARL...")
            val userId = "example-user-01" // Static user for the example

            // Instantiate KARL dependencies with REAL implementations
            try {
                val engine: LearningEngine = RealLearningEngine(learningRate = 0.01f) // Real neural network
                val dataStorage: DataStorage =
                    RealDataStorage("karl_database.db") // Real SQLite persistence

                println("DEBUG: Creating ExampleDataSource with actionFlow = $actionFlow")
                val dataSource: DataSource = ExampleDataSource(userId, actionFlow) // Use the actionFlow
                println("DEBUG: ExampleDataSource created successfully")

                // Build the container
                val container =
                    Karl.forUser(userId)
                        .withLearningEngine(engine)
                        .withDataStorage(dataStorage)
                        .withDataSource(dataSource)
                        .withCoroutineScope(applicationScope) // Use the app-level scope
                        .build()

                // Initialize (must be called!)
                container.initialize(
                    engine,
                    dataStorage,
                    dataSource,
                    emptyList(),
                    applicationScope,
                )

                karlContainer = container // Store the initialized container
                learningEngine = engine // Store the learning engine for insights

                // Update system status to Ready
                systemStatusState.value = "Ready"

                // Set model architecture info from the learning engine
                modelArchitecture = engine.getModelArchitectureName()

                // Update learning insights after initialization
                updateLearningProgress()

                println("App LaunchedEffect: KARL setup complete.")

                // === TEST: Auto-emit a test action to verify the flow works ===
                delay(2000) // Wait 2 seconds
                println("========== AUTO-TEST: Emitting test action ==========")
                println("AUTO-TEST: actionFlow = $actionFlow")
                println("AUTO-TEST: actionFlow.subscriptionCount = ${actionFlow.subscriptionCount}")
                val result = actionFlow.tryEmit("test_action")
                println("AUTO-TEST: tryEmit result = $result")
                println("========== AUTO-TEST: Test action emitted ==========")

                // Try regular emit as well
                delay(1000)
                println("========== AUTO-TEST: Using regular emit ==========")
                actionFlow.emit("test_action_2")
                println("========== AUTO-TEST: Regular emit completed ==========")
            } catch (e: Exception) {
                println("App LaunchedEffect: ERROR setting up KARL: ${e.message}")
                e.printStackTrace()
                systemStatusState.value = "Error"
                // Handle error (e.g., show error message in UI)
            }
        }

        // --- Main Application Window ---
        Window(
            onCloseRequest = {
                println("App onCloseRequest: Cleaning up...")
                // Launch cleanup in the application scope
                applicationScope
                    .launch {
                        println("App onCloseRequest: About to call karlContainer.saveState()...")
                        karlContainer?.saveState()?.join() // Save state and wait
                        println("App onCloseRequest: saveState() completed successfully")
                        karlContainer?.release() // Release resources
                        println("App onCloseRequest: KARL cleanup finished.")
                    }
                    .invokeOnCompletion { // Ensure application exits after cleanup
                        println("App onCloseRequest: Cleanup coroutine completed, exiting application")
                        exitApplication()
                    }
            },
            state = windowState,
            title = "Project KARL - Example Desktop App",
        ) {
            // Use Material Theme with conditional colors based on theme toggle
            MaterialTheme(
                colors =
                    if (isDarkTheme) {
                        darkColors(
                            primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
                            primaryVariant = androidx.compose.ui.graphics.Color(0xFF1976D2),
                            secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6),
                            background = androidx.compose.ui.graphics.Color(0xFF121212),
                            surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                            onPrimary = androidx.compose.ui.graphics.Color.White,
                            onSecondary = androidx.compose.ui.graphics.Color.Black,
                            onBackground = androidx.compose.ui.graphics.Color.White,
                            onSurface = androidx.compose.ui.graphics.Color.White,
                        )
                    } else {
                        lightColors(
                            primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
                            primaryVariant = androidx.compose.ui.graphics.Color(0xFF1976D2),
                            secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6),
                            background = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
                            surface = androidx.compose.ui.graphics.Color.White,
                            onPrimary = androidx.compose.ui.graphics.Color.White,
                            onSecondary = androidx.compose.ui.graphics.Color.Black,
                            onBackground = androidx.compose.ui.graphics.Color.Black,
                            onSurface = androidx.compose.ui.graphics.Color.Black,
                        )
                    },
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background,
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp), // Reduced padding from 32.dp
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Top Controls Row (Theme Toggle and GitHub Link)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), // Reduced from 16.dp
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Theme Toggle Button
                            IconButton(
                                onClick = { isDarkTheme = !isDarkTheme },
                                modifier = Modifier.padding(end = 8.dp).pointerHoverIcon(PointerIcon.Hand),
                            ) {
                                Icon(
                                    imageVector =
                                        if (isDarkTheme) {
                                            Icons.Default.WbSunny
                                        } else {
                                            Icons.Default.Brightness2
                                        },
                                    contentDescription =
                                        if (isDarkTheme) {
                                            "Switch to Light Theme"
                                        } else {
                                            "Switch to Dark Theme"
                                        },
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            // GitHub Link Button
                            IconButton(
                                onClick = {
                                    try {
                                        Desktop.getDesktop()
                                            .browse(
                                                URI(
                                                    "https://github.com/theaniketraj/project-karl",
                                                ),
                                            )
                                    } catch (e: Exception) {
                                        println("Could not open GitHub link: ${e.message}")
                                    }
                                },
                                modifier = Modifier.pointerHoverIcon(PointerIcon.Hand),
                            ) {
                                Icon(
                                    imageVector = GitHubIcon,
                                    contentDescription = "Open GitHub Repository",
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        // Header Section - Compact with centered content
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp), // Reduced from 32.dp
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                // Animated icon with subtle scale effect - smaller size
                                val iconScale by animateFloatAsState(
                                    targetValue = if (karlContainer != null) 1.05f else 1.0f, // Reduced from 1.1f
                                    animationSpec = tween(durationMillis = 1000),
                                )
                                Text(
                                    text = "🧠",
                                    style = MaterialTheme.typography.h3, // Reduced from h1
                                    modifier =
                                        Modifier
                                            .graphicsLayer(scaleX = iconScale, scaleY = iconScale)
                                            .padding(bottom = 4.dp), // Reduced from 8.dp
                                )
                                Text(
                                    text = "Project KARL",
                                    style =
                                        MaterialTheme.typography.h4.copy( // Reduced from h2
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                                        ),
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                )
                                Text(
                                    text = "Kotlin Adaptive Reasoning Learner",
                                    style =
                                        MaterialTheme.typography.body1.copy( // Reduced from h6
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                                        ),
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 4.dp), // Reduced from 8.dp
                                )
                            }
                        }

                        // Status Section - Compact with subtle glow effect
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp), // Reduced from 32.dp
                            contentAlignment = Alignment.Center,
                        ) {
                            // Animated status indicator with glow effect
                            val statusAlpha by animateFloatAsState(
                                targetValue = if (karlContainer != null) 1.0f else 0.6f,
                                animationSpec = tween(durationMillis = 800),
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.graphicsLayer(alpha = statusAlpha),
                            ) {
                                // Animated status dot with pulsing effect - smaller size
                                val pulseScale by animateFloatAsState(
                                    targetValue = if (karlContainer != null) 1.0f else 1.1f, // Reduced pulse effect
                                    animationSpec = tween(durationMillis = 1500),
                                )

                                Box(
                                    modifier =
                                        Modifier
                                            .size(12.dp) // Reduced from 16.dp
                                            .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                                            .background(
                                                color =
                                                    if (karlContainer != null) {
                                                        androidx.compose.ui.graphics.Color(0xFF10AC84)
                                                    } else {
                                                        androidx.compose.ui.graphics.Color(0xFFFF9500)
                                                    },
                                                shape = androidx.compose.foundation.shape.CircleShape,
                                            ),
                                )

                                Spacer(modifier = Modifier.width(12.dp)) // Reduced from 16.dp

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text =
                                            if (karlContainer != null) {
                                                "KARL System Ready"
                                            } else {
                                                "System Initializing..."
                                            },
                                        style =
                                            MaterialTheme.typography.body1.copy( // Reduced from h6
                                                fontWeight = FontWeight.Medium,
                                            ),
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                    )
                                    Text(
                                        text =
                                            if (karlContainer != null) {
                                                "AI engine active and learning"
                                            } else {
                                                "Please wait while components initialize"
                                            },
                                        style = MaterialTheme.typography.caption, // Reduced from body2
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                    )
                                }
                            }
                        }

                        // Main Content Section - Three Panel Layout
                        if (karlContainer != null) {
                            // Calculate weights based on enlarged section
                            val insightsWeight by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "insights" -> 0.6f
                                            "prediction" -> 0.2f
                                            "controls" -> 0.2f
                                            else -> 0.33f
                                        },
                                    animationSpec = tween(durationMillis = 300),
                                )
                            val predictionWeight by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "insights" -> 0.2f
                                            "prediction" -> 0.6f
                                            "controls" -> 0.2f
                                            else -> 0.33f
                                        },
                                    animationSpec = tween(durationMillis = 300),
                                )
                            val controlsWeight by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "insights" -> 0.2f
                                            "prediction" -> 0.2f
                                            "controls" -> 0.6f
                                            else -> 0.33f
                                        },
                                    animationSpec = tween(durationMillis = 300),
                                )

                            // Content fade animations for smooth transitions
                            val insightsAlpha by
                                animateFloatAsState(
                                    targetValue = if (enlargedSection == null || enlargedSection == "insights") 1.0f else 0.7f,
                                    animationSpec = tween(durationMillis = 400),
                                )
                            val predictionAlpha by
                                animateFloatAsState(
                                    targetValue = if (enlargedSection == null || enlargedSection == "prediction") 1.0f else 0.7f,
                                    animationSpec = tween(durationMillis = 400),
                                )
                            val controlsAlpha by
                                animateFloatAsState(
                                    targetValue = if (enlargedSection == null || enlargedSection == "controls") 1.0f else 0.7f,
                                    animationSpec = tween(durationMillis = 400),
                                )

                            Row(
                                modifier = Modifier.fillMaxWidth().weight(1f, fill = true), // Ensure fill = true for maximum height usage
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                // AI Insights Section - Transparent with subtle animations
                                Box(
                                    modifier =
                                        Modifier
                                            .weight(insightsWeight)
                                            .fillMaxHeight() // Use available height instead of fixed height
                                            .graphicsLayer(alpha = insightsAlpha),
                                ) {
                                    // Enhanced hover interaction with animated background
                                    val insightsInteractionSource = remember { MutableInteractionSource() }
                                    val isInsightsHovered by insightsInteractionSource.collectIsHoveredAsState()

                                    // Animated hover effects
                                    val hoverScale by animateFloatAsState(
                                        targetValue = if (isInsightsHovered) 1.02f else 1.0f,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val hoverElevation by animateFloatAsState(
                                        targetValue = if (isInsightsHovered) 8.dp.value else 0.dp.value,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val backgroundAlpha by animateFloatAsState(
                                        targetValue = if (isInsightsHovered) 0.12f else 0.05f,
                                        animationSpec = tween(durationMillis = 300),
                                    )
                                    val borderAlpha by animateFloatAsState(
                                        targetValue = if (isInsightsHovered) 0.2f else 0.08f,
                                        animationSpec = tween(durationMillis = 300),
                                    )

                                    // Colorful accent animation for hover
                                    val accentColor =
                                        if (isDarkTheme) {
                                            androidx.compose.ui.graphics.Color(0xFF64B5F6) // Light blue for dark theme
                                        } else {
                                            androidx.compose.ui.graphics.Color(0xFF1976D2) // Darker blue for light theme
                                        }

                                    // Transparent background with subtle border and hover effects
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .graphicsLayer(
                                                    scaleX = hoverScale,
                                                    scaleY = hoverScale,
                                                    shadowElevation = hoverElevation,
                                                )
                                                .background(
                                                    color =
                                                        if (isInsightsHovered) {
                                                            accentColor.copy(alpha = backgroundAlpha * 0.3f)
                                                        } else {
                                                            MaterialTheme.colors.surface.copy(alpha = backgroundAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .border(
                                                    width = if (isInsightsHovered) 2.dp else 1.dp,
                                                    color =
                                                        if (isInsightsHovered) {
                                                            accentColor.copy(alpha = borderAlpha)
                                                        } else {
                                                            MaterialTheme.colors.onSurface.copy(alpha = borderAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .hoverable(insightsInteractionSource)
                                                .padding(20.dp), // Reduced padding from 32.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                // Enhanced header with animation
                                                val headerScale by animateFloatAsState(
                                                    targetValue = if (karlContainer != null) 1.0f else 0.95f,
                                                    animationSpec = tween(durationMillis = 600),
                                                )

                                                Text(
                                                    text =
                                                        if (enlargedSection == "insights") {
                                                            "📊 AI Insights"
                                                        } else {
                                                            "📊 AI Insights"
                                                        },
                                                    style =
                                                        MaterialTheme.typography.h5.copy( // Larger text size
                                                            fontWeight = FontWeight.SemiBold,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                                    modifier =
                                                        Modifier.graphicsLayer(
                                                            scaleX = headerScale,
                                                            scaleY = headerScale,
                                                        ),
                                                )

                                                // Transparent ghost button
                                                IconButton(
                                                    onClick = {
                                                        enlargedSection = if (enlargedSection == "insights") null else "insights"
                                                    },
                                                    modifier =
                                                        Modifier
                                                            .size(40.dp) // Larger button
                                                            .background(
                                                                color =
                                                                    if (enlargedSection == "insights") {
                                                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                                                    } else {
                                                                        androidx.compose.ui.graphics.Color.Transparent
                                                                    },
                                                                shape = RoundedCornerShape(8.dp),
                                                            ),
                                                ) {
                                                    Icon(
                                                        imageVector =
                                                            if (enlargedSection == "insights") {
                                                                Icons.Default.CloseFullscreen
                                                            } else {
                                                                Icons.Default.OpenInFull
                                                            },
                                                        contentDescription =
                                                            if (enlargedSection == "insights") {
                                                                "Restore Size"
                                                            } else {
                                                                "Focus Mode"
                                                            },
                                                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(20.dp), // Larger icon
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))

                                            // Dynamic AI Insights Content
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced from 12.dp
                                            ) {
                                                // System Status
                                                Text(
                                                    text = "System Status: $systemStatus",
                                                    style = MaterialTheme.typography.body2,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                )

                                                Text(
                                                    text = "Architecture: $modelArchitecture",
                                                    style = MaterialTheme.typography.body2,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = "Interactions Processed: $interactionsProcessed",
                                                        style = MaterialTheme.typography.body2,
                                                        color = accentColor.copy(alpha = 0.9f),
                                                    )

                                                    // AI Maturity Meter - Sparkline Chart in its own box
                                                    Box(
                                                        modifier =
                                                            Modifier
                                                                .background(
                                                                    color = MaterialTheme.colors.surface.copy(alpha = 0.2f),
                                                                    shape = RoundedCornerShape(8.dp),
                                                                )
                                                                .border(
                                                                    width = 1.dp,
                                                                    color = accentColor.copy(alpha = 0.2f),
                                                                    shape = RoundedCornerShape(8.dp),
                                                                )
                                                                .padding(12.dp),
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                        ) {
                                                            Text(
                                                                text = "Confidence Trend",
                                                                style = MaterialTheme.typography.caption,
                                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                                            )
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            SparklineChart(
                                                                data = confidenceHistoryData,
                                                                modifier =
                                                                    Modifier.size(
                                                                        width = if (enlargedSection == "insights") 120.dp else 80.dp,
                                                                        height = if (enlargedSection == "insights") 40.dp else 25.dp,
                                                                    ),
                                                                color = accentColor,
                                                                lineWidth = if (enlargedSection == "insights") 2.5f else 2.0f,
                                                            )
                                                            Spacer(modifier = Modifier.height(4.dp))
                                                            Text(
                                                                text = "${(confidenceHistoryData.lastOrNull() ?: 0.87f).times(
                                                                    100,
                                                                ).toInt()}%",
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                                color = accentColor,
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = "Recent Interactions:",
                                                    style =
                                                        MaterialTheme.typography.body2.copy(
                                                            fontWeight = FontWeight.Medium,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                )

                                                LazyColumn(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f)
                                                            .background(
                                                                color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                                                shape = MaterialTheme.shapes.small,
                                                            )
                                                            .padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                ) {
                                                    items(structuredInteractionLog.reversed()) { interaction ->
                                                        Column(
                                                            modifier = Modifier.fillMaxWidth(),
                                                        ) {
                                                            // Timestamp and action line
                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically,
                                                            ) {
                                                                Text(
                                                                    text = "[${interaction.timestamp}] Action: '${interaction.action}'",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Medium,
                                                                        ),
                                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                                )
                                                                Text(
                                                                    text = "${(interaction.confidence * 100).toInt()}%",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                    color =
                                                                        when {
                                                                            interaction.confidence >= 0.9f ->
                                                                                androidx.compose.ui.graphics.Color(
                                                                                    0xFF4CAF50,
                                                                                )
                                                                            interaction.confidence >= 0.8f ->
                                                                                androidx.compose.ui.graphics.Color(
                                                                                    0xFF8BC34A,
                                                                                )
                                                                            interaction.confidence >= 0.7f ->
                                                                                androidx.compose.ui.graphics.Color(
                                                                                    0xFFFFEB3B,
                                                                                )
                                                                            else -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                                                        },
                                                                )
                                                            }
                                                            // Prediction line
                                                            Text(
                                                                text = "→ Predicted: '${interaction.prediction}'",
                                                                style = MaterialTheme.typography.caption,
                                                                color = accentColor.copy(alpha = 0.9f),
                                                                modifier = Modifier.padding(start = 8.dp),
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Prediction Details Section - Middle panel with dynamic data
                                Box(
                                    modifier =
                                        Modifier
                                            .weight(predictionWeight)
                                            .fillMaxHeight() // Use available height instead of fixed height
                                            .graphicsLayer(alpha = predictionAlpha),
                                ) {
                                    // Enhanced hover interaction with animated background
                                    val predictionInteractionSource = remember { MutableInteractionSource() }
                                    val isPredictionHovered by predictionInteractionSource.collectIsHoveredAsState()

                                    // Animated hover effects
                                    val hoverScale by animateFloatAsState(
                                        targetValue = if (isPredictionHovered) 1.02f else 1.0f,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val hoverElevation by animateFloatAsState(
                                        targetValue = if (isPredictionHovered) 8.dp.value else 0.dp.value,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val backgroundAlpha by animateFloatAsState(
                                        targetValue = if (isPredictionHovered) 0.12f else 0.05f,
                                        animationSpec = tween(durationMillis = 300),
                                    )
                                    val borderAlpha by animateFloatAsState(
                                        targetValue = if (isPredictionHovered) 0.2f else 0.08f,
                                        animationSpec = tween(durationMillis = 300),
                                    )

                                    // Colorful accent animation for hover - different color for Prediction
                                    val accentColor =
                                        if (isDarkTheme) {
                                            androidx.compose.ui.graphics.Color(0xFF81C784) // Light green for dark theme
                                        } else {
                                            androidx.compose.ui.graphics.Color(0xFF388E3C) // Darker green for light theme
                                        }

                                    // Transparent background with subtle border and hover effects
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .graphicsLayer(
                                                    scaleX = hoverScale,
                                                    scaleY = hoverScale,
                                                    shadowElevation = hoverElevation,
                                                )
                                                .background(
                                                    color =
                                                        if (isPredictionHovered) {
                                                            accentColor.copy(alpha = backgroundAlpha * 0.3f)
                                                        } else {
                                                            MaterialTheme.colors.surface.copy(alpha = backgroundAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .border(
                                                    width = if (isPredictionHovered) 2.dp else 1.dp,
                                                    color =
                                                        if (isPredictionHovered) {
                                                            accentColor.copy(alpha = borderAlpha)
                                                        } else {
                                                            MaterialTheme.colors.onSurface.copy(alpha = borderAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .hoverable(predictionInteractionSource)
                                                .padding(20.dp), // Reduced padding from 32.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                // Enhanced header with animation
                                                val headerScale by animateFloatAsState(
                                                    targetValue = if (karlContainer != null) 1.0f else 0.95f,
                                                    animationSpec = tween(durationMillis = 600),
                                                )

                                                Text(
                                                    text =
                                                        if (enlargedSection == "prediction") {
                                                            "🔮 Prediction Details"
                                                        } else {
                                                            "🔮 Prediction Details"
                                                        },
                                                    style =
                                                        MaterialTheme.typography.h5.copy( // Larger text size
                                                            fontWeight = FontWeight.SemiBold,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                                    modifier =
                                                        Modifier.graphicsLayer(
                                                            scaleX = headerScale,
                                                            scaleY = headerScale,
                                                        ),
                                                )

                                                // Transparent ghost button
                                                IconButton(
                                                    onClick = {
                                                        enlargedSection = if (enlargedSection == "prediction") null else "prediction"
                                                    },
                                                    modifier =
                                                        Modifier
                                                            .size(40.dp) // Larger button
                                                            .background(
                                                                color =
                                                                    if (enlargedSection == "prediction") {
                                                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                                                    } else {
                                                                        androidx.compose.ui.graphics.Color.Transparent
                                                                    },
                                                                shape = RoundedCornerShape(8.dp),
                                                            ),
                                                ) {
                                                    Icon(
                                                        imageVector =
                                                            if (enlargedSection == "prediction") {
                                                                Icons.Default.CloseFullscreen
                                                            } else {
                                                                Icons.Default.OpenInFull
                                                            },
                                                        contentDescription =
                                                            if (enlargedSection == "prediction") {
                                                                "Restore Size"
                                                            } else {
                                                                "Focus Mode"
                                                            },
                                                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(20.dp), // Larger icon
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp)) // More spacing

                                            // Dynamic Prediction Content
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                            ) {
                                                // Input Context
                                                Text(
                                                    text = "Input Context:",
                                                    style =
                                                        MaterialTheme.typography.body2.copy(
                                                            fontWeight = FontWeight.Medium,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                )
                                                Text(
                                                    text = "Last Action: \"$lastAction\"",
                                                    style = MaterialTheme.typography.caption,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                )

                                                // Input Features with Tooltip
                                                @OptIn(ExperimentalFoundationApi::class)
                                                TooltipArea(
                                                    tooltip = {
                                                        // Tooltip content
                                                        Box(
                                                            modifier =
                                                                Modifier
                                                                    .background(
                                                                        color = MaterialTheme.colors.surface,
                                                                        shape = RoundedCornerShape(8.dp),
                                                                    )
                                                                    .border(
                                                                        width = 1.dp,
                                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                                                                        shape = RoundedCornerShape(8.dp),
                                                                    )
                                                                    .padding(12.dp),
                                                        ) {
                                                            Column(
                                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                                            ) {
                                                                Text(
                                                                    text = "Input Features Explained",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                    color = MaterialTheme.colors.onSurface,
                                                                )
                                                                Text(
                                                                    text = "Number of numerical features derived from:",
                                                                    style = MaterialTheme.typography.caption,
                                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                                )
                                                                Text(
                                                                    text = "• Action context and timing",
                                                                    style = MaterialTheme.typography.caption,
                                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                                )
                                                                Text(
                                                                    text = "• Historical interaction patterns",
                                                                    style = MaterialTheme.typography.caption,
                                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                                )
                                                                Text(
                                                                    text = "• Environmental state vectors",
                                                                    style = MaterialTheme.typography.caption,
                                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                                )
                                                            }
                                                        }
                                                    },
                                                    delayMillis = 600, // Show tooltip after 600ms hover
                                                    tooltipPlacement =
                                                        TooltipPlacement.CursorPoint(
                                                            offset = androidx.compose.ui.unit.DpOffset(0.dp, 16.dp),
                                                        ),
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    ) {
                                                        Text(
                                                            text =
                                                                "Input Features: ${
                                                                    predictionValue?.metadata?.get("input_features") ?: "Not available"
                                                                }",
                                                            style = MaterialTheme.typography.caption,
                                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                        )
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = "Feature information",
                                                            modifier = Modifier.size(14.dp),
                                                            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Primary Prediction Output - Prominently styled
                                                Text(
                                                    text = "Primary Prediction:",
                                                    style =
                                                        MaterialTheme.typography.body2.copy(
                                                            fontWeight = FontWeight.Medium,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                )

                                                // Primary prediction with enhanced styling
                                                Box(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .background(
                                                                color = accentColor.copy(alpha = 0.1f),
                                                                shape = RoundedCornerShape(8.dp),
                                                            )
                                                            .border(
                                                                width = 1.dp,
                                                                color = accentColor.copy(alpha = 0.3f),
                                                                shape = RoundedCornerShape(8.dp),
                                                            )
                                                            .padding(12.dp),
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = predictionValue?.suggestion ?: "No prediction available",
                                                            style =
                                                                MaterialTheme.typography.body1.copy(
                                                                    fontWeight = FontWeight.SemiBold,
                                                                    fontSize = 16.sp,
                                                                ),
                                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                                        )
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                        ) {
                                                            Text(
                                                                text =
                                                                    "Confidence: ${
                                                                        ((predictionValue?.confidence ?: 0f) * 100).toInt()
                                                                    }%",
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = FontWeight.Medium,
                                                                    ),
                                                                color = accentColor.copy(alpha = 0.9f),
                                                            )
                                                            Text(
                                                                text = "Processing: ${processingTime}ms",
                                                                style = MaterialTheme.typography.caption,
                                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))

                                                // Alternative Predictions - Secondary styling
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                ) {
                                                    Text(
                                                        text = "Alternative Predictions:",
                                                        style =
                                                            MaterialTheme.typography.body2.copy(
                                                                fontWeight = FontWeight.Medium,
                                                            ),
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                    )
                                                    Text(
                                                        text = "(${predictionValue?.alternatives?.size ?: 0} options)",
                                                        style = MaterialTheme.typography.caption,
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                                    )
                                                }

                                                LazyColumn(
                                                    modifier =
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .weight(1f)
                                                            .background(
                                                                color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                                                shape = MaterialTheme.shapes.small,
                                                            )
                                                            .padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                                ) {
                                                    items(predictionValue?.alternatives ?: emptyList()) { alternative ->
                                                        Row(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                        ) {
                                                            Box(
                                                                modifier =
                                                                    Modifier
                                                                        .size(4.dp)
                                                                        .background(
                                                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                                                                            shape = CircleShape,
                                                                        ),
                                                            )
                                                            Text(
                                                                text = alternative,
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                                                                    ),
                                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Interaction Controls Section - Transparent with animations
                                Box(
                                    modifier =
                                        Modifier
                                            .weight(controlsWeight)
                                            .fillMaxHeight() // Use available height instead of fixed height
                                            .graphicsLayer(alpha = controlsAlpha),
                                ) {
                                    // Enhanced hover interaction with animated background
                                    val controlsInteractionSource = remember { MutableInteractionSource() }
                                    val isControlsHovered by controlsInteractionSource.collectIsHoveredAsState()

                                    // Animated hover effects
                                    val hoverScale by animateFloatAsState(
                                        targetValue = if (isControlsHovered) 1.02f else 1.0f,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val hoverElevation by animateFloatAsState(
                                        targetValue = if (isControlsHovered) 8.dp.value else 0.dp.value,
                                        animationSpec = tween(durationMillis = 200),
                                    )
                                    val backgroundAlpha by animateFloatAsState(
                                        targetValue = if (isControlsHovered) 0.12f else 0.05f,
                                        animationSpec = tween(durationMillis = 300),
                                    )
                                    val borderAlpha by animateFloatAsState(
                                        targetValue = if (isControlsHovered) 0.2f else 0.08f,
                                        animationSpec = tween(durationMillis = 300),
                                    )

                                    // Colorful accent animation for hover - different color for Controls
                                    val accentColor =
                                        if (isDarkTheme) {
                                            androidx.compose.ui.graphics.Color(0xFF81C784) // Light green for dark theme
                                        } else {
                                            androidx.compose.ui.graphics.Color(0xFF388E3C) // Darker green for light theme
                                        }

                                    // Transparent background with subtle border and hover effects
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .graphicsLayer(
                                                    scaleX = hoverScale,
                                                    scaleY = hoverScale,
                                                    shadowElevation = hoverElevation,
                                                )
                                                .background(
                                                    color =
                                                        if (isControlsHovered) {
                                                            accentColor.copy(alpha = backgroundAlpha * 0.3f)
                                                        } else {
                                                            MaterialTheme.colors.surface.copy(alpha = backgroundAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .border(
                                                    width = if (isControlsHovered) 2.dp else 1.dp,
                                                    color =
                                                        if (isControlsHovered) {
                                                            accentColor.copy(alpha = borderAlpha)
                                                        } else {
                                                            MaterialTheme.colors.onSurface.copy(alpha = borderAlpha)
                                                        },
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .hoverable(controlsInteractionSource)
                                                .padding(20.dp), // Reduced padding from 32.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                // Enhanced header with animation
                                                val headerScale by animateFloatAsState(
                                                    targetValue = if (karlContainer != null) 1.0f else 0.95f,
                                                    animationSpec = tween(durationMillis = 600),
                                                )

                                                Text(
                                                    text =
                                                        if (enlargedSection == "controls") {
                                                            "🧑‍💻 Controls"
                                                        } else {
                                                            "🧑‍💻 Controls"
                                                        },
                                                    style =
                                                        MaterialTheme.typography.h5.copy( // Larger text size
                                                            fontWeight = FontWeight.SemiBold,
                                                        ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                                    modifier =
                                                        Modifier.graphicsLayer(
                                                            scaleX = headerScale,
                                                            scaleY = headerScale,
                                                        ),
                                                )

                                                // Transparent ghost button
                                                IconButton(
                                                    onClick = {
                                                        enlargedSection = if (enlargedSection == "controls") null else "controls"
                                                    },
                                                    modifier =
                                                        Modifier
                                                            .size(40.dp) // Larger button
                                                            .background(
                                                                color =
                                                                    if (enlargedSection == "controls") {
                                                                        MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                                                    } else {
                                                                        androidx.compose.ui.graphics.Color.Transparent
                                                                    },
                                                                shape = RoundedCornerShape(8.dp),
                                                            ),
                                                ) {
                                                    Icon(
                                                        imageVector =
                                                            if (enlargedSection == "controls") {
                                                                Icons.Default.CloseFullscreen
                                                            } else {
                                                                Icons.Default.OpenInFull
                                                            },
                                                        contentDescription =
                                                            if (enlargedSection == "controls") {
                                                                "Restore Size"
                                                            } else {
                                                                "Focus Mode"
                                                            },
                                                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.size(20.dp), // Larger icon
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(24.dp)) // More spacing

                                            // Responsive description with animation
                                            if (enlargedSection != "insights") {
                                                val descriptionAlpha by animateFloatAsState(
                                                    targetValue = 1.0f,
                                                    animationSpec = tween(durationMillis = 800),
                                                )

                                                Text(
                                                    text = "Simulate user actions to train the AI model",
                                                    style = MaterialTheme.typography.body1.copy(fontSize = 16.sp), // Larger subtitle
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                    modifier =
                                                        Modifier
                                                            .padding(bottom = 32.dp) // More spacing
                                                            .graphicsLayer(alpha = descriptionAlpha),
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                )
                                            }

                                            // Enhanced Fluent Design Buttons with animations
                                            Column(
                                                verticalArrangement =
                                                    Arrangement.spacedBy(
                                                        if (enlargedSection == "controls") 24.dp else 20.dp, // More spacing
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                            ) {
                                                if (enlargedSection == "insights") {
                                                    // Compact mode - Fluent pill buttons
                                                    Column(
                                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                    ) {
                                                        // Fluent: Compact pill button with hover and pressed animation
                                                        val buttonAInteractionSource = remember { MutableInteractionSource() }
                                                        val isButtonAHovered by buttonAInteractionSource.collectIsHoveredAsState()
                                                        val isButtonAPressed by buttonAInteractionSource.collectIsPressedAsState()
                                                        val buttonAScale by animateFloatAsState(
                                                            targetValue =
                                                                when {
                                                                    isButtonAPressed -> 0.95f
                                                                    isButtonAHovered -> 1.05f
                                                                    else -> 1.0f
                                                                },
                                                            animationSpec = tween(durationMillis = 150),
                                                        )

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "action_type_A"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(actionType, "Clicked 'Action A'")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(36.dp)
                                                                    .width(80.dp)
                                                                    .graphicsLayer(
                                                                        scaleX = buttonAScale,
                                                                        scaleY = buttonAScale,
                                                                    )
                                                                    .hoverable(buttonAInteractionSource),
                                                            shape = RoundedCornerShape(18.dp), // Fluent: Pill shape
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor =
                                                                        when {
                                                                            isButtonAPressed ->
                                                                                androidx.compose.ui.graphics.Color(0xFF388E3C)
                                                                            isButtonAHovered ->
                                                                                androidx.compose.ui.graphics.Color(0xFF66BB6A)
                                                                            else -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                                                                        },
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                            elevation = ButtonDefaults.elevation(0.dp), // Fluent: No elevation
                                                            interactionSource = buttonAInteractionSource,
                                                        ) {
                                                            Text(
                                                                text = "A",
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = FontWeight.Medium,
                                                                    ),
                                                            )
                                                        }

                                                        val buttonBInteractionSource = remember { MutableInteractionSource() }
                                                        val isButtonBHovered by buttonBInteractionSource.collectIsHoveredAsState()
                                                        val isButtonBPressed by buttonBInteractionSource.collectIsPressedAsState()
                                                        val buttonBScale by animateFloatAsState(
                                                            targetValue =
                                                                when {
                                                                    isButtonBPressed -> 0.95f
                                                                    isButtonBHovered -> 1.05f
                                                                    else -> 1.0f
                                                                },
                                                            animationSpec = tween(durationMillis = 150),
                                                        )

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "action_type_B"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(actionType, "Clicked 'Action B'")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(36.dp)
                                                                    .width(80.dp)
                                                                    .graphicsLayer(
                                                                        scaleX = buttonBScale,
                                                                        scaleY = buttonBScale,
                                                                    )
                                                                    .hoverable(buttonBInteractionSource),
                                                            shape = RoundedCornerShape(18.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor =
                                                                        when {
                                                                            isButtonBPressed ->
                                                                                androidx.compose.ui.graphics.Color(0xFFF57C00)
                                                                            isButtonBHovered ->
                                                                                androidx.compose.ui.graphics.Color(0xFFFFB74D)
                                                                            else -> androidx.compose.ui.graphics.Color(0xFFFF9800)
                                                                        },
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                            elevation = ButtonDefaults.elevation(0.dp),
                                                            interactionSource = buttonBInteractionSource,
                                                        ) {
                                                            Text(
                                                                text = "B",
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = FontWeight.Medium,
                                                                    ),
                                                            )
                                                        }

                                                        // Get Prediction button with loading state and enhanced interactions
                                                        val predictionInteractionSource = remember { MutableInteractionSource() }
                                                        val isPredictionHovered by predictionInteractionSource.collectIsHoveredAsState()
                                                        val isPredictionPressed by predictionInteractionSource.collectIsPressedAsState()
                                                        val isLoading by isLoadingPrediction.collectAsState()

                                                        val predictionButtonScale by animateFloatAsState(
                                                            targetValue =
                                                                when {
                                                                    isPredictionPressed -> 0.95f
                                                                    isPredictionHovered -> 1.05f
                                                                    else -> 1.0f
                                                                },
                                                            animationSpec = tween(durationMillis = 150),
                                                        )

                                                        Button(
                                                            onClick = {
                                                                if (!isLoading) {
                                                                    applicationScope.launch {
                                                                        isLoadingPrediction.value = true
                                                                        try {
                                                                            println("Button Clicked: Get Prediction")
                                                                            handlePredictionWithTiming(
                                                                                "explicit_prediction",
                                                                                "Clicked 'Get Prediction'",
                                                                                emitToActionFlow = false,
                                                                            )
                                                                        } finally {
                                                                            isLoadingPrediction.value = false
                                                                        }
                                                                    }
                                                                }
                                                            },
                                                            enabled = karlContainer != null && !isLoading,
                                                            modifier =
                                                                Modifier
                                                                    .height(36.dp)
                                                                    .width(100.dp)
                                                                    .graphicsLayer(
                                                                        scaleX = predictionButtonScale,
                                                                        scaleY = predictionButtonScale,
                                                                    )
                                                                    .hoverable(predictionInteractionSource),
                                                            shape = RoundedCornerShape(18.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor =
                                                                        when {
                                                                            isLoading -> MaterialTheme.colors.primary.copy(alpha = 0.7f)
                                                                            isPredictionHovered ->
                                                                                MaterialTheme.colors.primary.copy(
                                                                                    alpha = 0.9f,
                                                                                )
                                                                            else -> MaterialTheme.colors.primary
                                                                        },
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                            interactionSource = predictionInteractionSource,
                                                        ) {
                                                            if (isLoading) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(16.dp),
                                                                    color = androidx.compose.ui.graphics.Color.White,
                                                                    strokeWidth = 2.dp,
                                                                )
                                                            } else {
                                                                Text(
                                                                    text = "🔮",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else if (enlargedSection == "controls") {
                                                    // Large buttons when controls is enlarged
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "action_type_A"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Action A' (large view)",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(64.dp)
                                                                    .width(160.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand), // Larger buttons
                                                            shape = RoundedCornerShape(32.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🔄 Action A",
                                                                style =
                                                                    MaterialTheme.typography.h6.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "action_type_B"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Action B' (large view)",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(64.dp)
                                                                    .width(160.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(32.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF9800),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "⚡ Action B",
                                                                style =
                                                                    MaterialTheme.typography.h6.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Dynamic Action Simulation Buttons
                                                    Text(
                                                        text = "🚀 Simulate Actions",
                                                        style =
                                                            MaterialTheme.typography.h6.copy(
                                                                fontWeight = FontWeight.SemiBold,
                                                            ),
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.padding(vertical = 8.dp),
                                                    )

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "simulate_git_push"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Git Push'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(140.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "📤 Git Push",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "simulate_run_tests"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Run Tests'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(140.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🧪 Run Tests",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "simulate_build_project"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Build Project'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(140.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🔨 Build",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "simulate_git_pull"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Git Pull'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(140.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF607D8B),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "📥 Git Pull",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Scenario Testing Buttons
                                                    Text(
                                                        text = "🎯 Test Scenarios",
                                                        style =
                                                            MaterialTheme.typography.h6.copy(
                                                                fontWeight = FontWeight.SemiBold,
                                                            ),
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.padding(vertical = 8.dp),
                                                    )

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "scenario_heavy_load_test"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Heavy Load Test'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(145.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🔥 Heavy Load",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    val actionType = "scenario_data_migration"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    handlePredictionWithTiming(
                                                                        actionType,
                                                                        "Clicked 'Data Migration'",
                                                                    )
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(56.dp)
                                                                    .width(145.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "📊 Migration",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Large Get Prediction button with enhanced interactions
                                                    val largePredictionInteractionSource = remember { MutableInteractionSource() }
                                                    val isLargePredictionHovered by
                                                        largePredictionInteractionSource.collectIsHoveredAsState()
                                                    val isLargePredictionPressed by
                                                        largePredictionInteractionSource.collectIsPressedAsState()
                                                    val isLoading by isLoadingPrediction.collectAsState()

                                                    val largePredictionButtonScale by animateFloatAsState(
                                                        targetValue =
                                                            when {
                                                                isLargePredictionPressed -> 0.97f
                                                                isLargePredictionHovered -> 1.02f
                                                                else -> 1.0f
                                                            },
                                                        animationSpec = tween(durationMillis = 150),
                                                    )

                                                    Button(
                                                        onClick = {
                                                            if (!isLoading) {
                                                                applicationScope.launch {
                                                                    isLoadingPrediction.value = true
                                                                    try {
                                                                        println("Button Clicked: Get Prediction")
                                                                        handlePredictionWithTiming(
                                                                            "explicit_prediction_request",
                                                                            "Clicked 'Get Prediction'",
                                                                            emitToActionFlow = false,
                                                                        )
                                                                    } finally {
                                                                        isLoadingPrediction.value = false
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        enabled = karlContainer != null && !isLoading,
                                                        modifier =
                                                            Modifier
                                                                .height(64.dp)
                                                                .width(280.dp)
                                                                .graphicsLayer(
                                                                    scaleX = largePredictionButtonScale,
                                                                    scaleY = largePredictionButtonScale,
                                                                )
                                                                .hoverable(largePredictionInteractionSource)
                                                                .pointerHoverIcon(PointerIcon.Hand), // Extra large prediction button
                                                        shape = RoundedCornerShape(32.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    when {
                                                                        isLoading -> MaterialTheme.colors.primary.copy(alpha = 0.7f)
                                                                        isLargePredictionPressed ->
                                                                            MaterialTheme.colors.primary.copy(
                                                                                alpha = 0.8f,
                                                                            )
                                                                        isLargePredictionHovered ->
                                                                            MaterialTheme.colors.primary.copy(
                                                                                alpha = 0.9f,
                                                                            )
                                                                        else -> MaterialTheme.colors.primary
                                                                    },
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        interactionSource = largePredictionInteractionSource,
                                                    ) {
                                                        if (isLoading) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(20.dp),
                                                                    color = androidx.compose.ui.graphics.Color.White,
                                                                    strokeWidth = 2.dp,
                                                                )
                                                                Text(
                                                                    text = "Getting Prediction...",
                                                                    style =
                                                                        MaterialTheme.typography.h6.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        } else {
                                                            Text(
                                                                text = "🔮 Get Prediction",
                                                                style =
                                                                    MaterialTheme.typography.h6.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    // Normal buttons when both sections are equal
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    systemStatusState.value = "Processing"
                                                                    val actionType = "action_type_A"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)

                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction

                                                                    // Add to interaction log with prediction details
                                                                    prediction?.let { pred ->
                                                                        addInteractionLogEntry(actionType, pred.suggestion, pred.confidence)
                                                                    } ?: addInteractionLogEntry(actionType, "no prediction", 0.0f)

                                                                    updateLearningProgress()
                                                                    println("Prediction after Action A: $prediction")

                                                                    delay(500) // Simulate processing
                                                                    systemStatusState.value = "Ready"
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(48.dp)
                                                                    .width(130.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand), // Larger normal buttons
                                                            shape = RoundedCornerShape(24.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🔄 Action A",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    systemStatusState.value = "Processing"
                                                                    val actionType = "action_type_B"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)

                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction

                                                                    // Add to interaction log with prediction details
                                                                    prediction?.let { pred ->
                                                                        addInteractionLogEntry(actionType, pred.suggestion, pred.confidence)
                                                                    } ?: addInteractionLogEntry(actionType, "no prediction", 0.0f)

                                                                    updateLearningProgress()
                                                                    println("Prediction after Action B: $prediction")

                                                                    delay(500) // Simulate processing
                                                                    systemStatusState.value = "Ready"
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier =
                                                                Modifier
                                                                    .height(48.dp)
                                                                    .width(130.dp)
                                                                    .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(24.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF9800),
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "⚡ Action B",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Dynamic Action Buttons (Compact)
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    ) {
                                                        // Git Push Button with Tooltip
                                                        TooltipArea(
                                                            tooltip = {
                                                                Surface(
                                                                    color = MaterialTheme.colors.surface,
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    elevation = 8.dp,
                                                                    modifier = Modifier.padding(4.dp),
                                                                ) {
                                                                    Text(
                                                                        text = "Simulate 'Git Push' Action",
                                                                        style = MaterialTheme.typography.caption,
                                                                        modifier = Modifier.padding(8.dp),
                                                                    )
                                                                }
                                                            },
                                                            delayMillis = 600,
                                                            tooltipPlacement =
                                                                TooltipPlacement.CursorPoint(
                                                                    offset = androidx.compose.ui.unit.DpOffset(0.dp, 16.dp),
                                                                ),
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    applicationScope.launch {
                                                                        simulateAction("git push")
                                                                    }
                                                                },
                                                                enabled = karlContainer != null,
                                                                modifier =
                                                                    Modifier
                                                                        .height(40.dp)
                                                                        .width(90.dp)
                                                                        .pointerHoverIcon(PointerIcon.Hand),
                                                                shape = RoundedCornerShape(20.dp),
                                                                colors =
                                                                    ButtonDefaults.buttonColors(
                                                                        backgroundColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                                                                        contentColor = androidx.compose.ui.graphics.Color.White,
                                                                    ),
                                                            ) {
                                                                Text(
                                                                    text = "📤",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        }

                                                        // Run Tests Button with Tooltip
                                                        TooltipArea(
                                                            tooltip = {
                                                                Surface(
                                                                    color = MaterialTheme.colors.surface,
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    elevation = 8.dp,
                                                                    modifier = Modifier.padding(4.dp),
                                                                ) {
                                                                    Text(
                                                                        text = "Simulate 'Run Tests' Action",
                                                                        style = MaterialTheme.typography.caption,
                                                                        modifier = Modifier.padding(8.dp),
                                                                    )
                                                                }
                                                            },
                                                            delayMillis = 600,
                                                            tooltipPlacement =
                                                                TooltipPlacement.CursorPoint(
                                                                    offset = androidx.compose.ui.unit.DpOffset(0.dp, 16.dp),
                                                                ),
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    applicationScope.launch {
                                                                        simulateAction("run tests")
                                                                    }
                                                                },
                                                                enabled = karlContainer != null,
                                                                modifier =
                                                                    Modifier
                                                                        .height(40.dp)
                                                                        .width(90.dp)
                                                                        .pointerHoverIcon(PointerIcon.Hand),
                                                                shape = RoundedCornerShape(20.dp),
                                                                colors =
                                                                    ButtonDefaults.buttonColors(
                                                                        backgroundColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                                                                        contentColor = androidx.compose.ui.graphics.Color.White,
                                                                    ),
                                                            ) {
                                                                Text(
                                                                    text = "🧪",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    ) {
                                                        // Heavy Load Test Button with Tooltip
                                                        TooltipArea(
                                                            tooltip = {
                                                                Surface(
                                                                    color = MaterialTheme.colors.surface,
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    elevation = 8.dp,
                                                                    modifier = Modifier.padding(4.dp),
                                                                ) {
                                                                    Text(
                                                                        text = "Run 'Heavy Load Test' Scenario",
                                                                        style = MaterialTheme.typography.caption,
                                                                        modifier = Modifier.padding(8.dp),
                                                                    )
                                                                }
                                                            },
                                                            delayMillis = 600,
                                                            tooltipPlacement =
                                                                TooltipPlacement.CursorPoint(
                                                                    offset = androidx.compose.ui.unit.DpOffset(0.dp, 16.dp),
                                                                ),
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    applicationScope.launch {
                                                                        val actionType = "scenario_heavy_load_test"
                                                                        println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                        actionFlow.emit(actionType)
                                                                        updateLearningProgress()
                                                                        handlePredictionWithTiming(
                                                                            actionType,
                                                                            "Clicked 'Heavy Load Test' (compact)",
                                                                        )
                                                                    }
                                                                },
                                                                enabled = karlContainer != null,
                                                                modifier =
                                                                    Modifier
                                                                        .height(40.dp)
                                                                        .width(90.dp)
                                                                        .pointerHoverIcon(PointerIcon.Hand),
                                                                shape = RoundedCornerShape(20.dp),
                                                                colors =
                                                                    ButtonDefaults.buttonColors(
                                                                        backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
                                                                        contentColor = androidx.compose.ui.graphics.Color.White,
                                                                    ),
                                                            ) {
                                                                Text(
                                                                    text = "🔥",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        }

                                                        // Data Migration Button with Tooltip
                                                        TooltipArea(
                                                            tooltip = {
                                                                Surface(
                                                                    color = MaterialTheme.colors.surface,
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    elevation = 8.dp,
                                                                    modifier = Modifier.padding(4.dp),
                                                                ) {
                                                                    Text(
                                                                        text = "Run 'Data Migration' Scenario",
                                                                        style = MaterialTheme.typography.caption,
                                                                        modifier = Modifier.padding(8.dp),
                                                                    )
                                                                }
                                                            },
                                                            delayMillis = 600,
                                                            tooltipPlacement =
                                                                TooltipPlacement.CursorPoint(
                                                                    offset = androidx.compose.ui.unit.DpOffset(0.dp, 16.dp),
                                                                ),
                                                        ) {
                                                            Button(
                                                                onClick = {
                                                                    applicationScope.launch {
                                                                        val actionType = "scenario_data_migration"
                                                                        println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                        actionFlow.emit(actionType)
                                                                        updateLearningProgress()
                                                                        handlePredictionWithTiming(
                                                                            actionType,
                                                                            "Clicked 'Data Migration' (compact)",
                                                                        )
                                                                    }
                                                                },
                                                                enabled = karlContainer != null,
                                                                modifier =
                                                                    Modifier
                                                                        .height(40.dp)
                                                                        .width(90.dp)
                                                                        .pointerHoverIcon(PointerIcon.Hand),
                                                                shape = RoundedCornerShape(20.dp),
                                                                colors =
                                                                    ButtonDefaults.buttonColors(
                                                                        backgroundColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
                                                                        contentColor = androidx.compose.ui.graphics.Color.White,
                                                                    ),
                                                            ) {
                                                                Text(
                                                                    text = "📊",
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        }
                                                    }

                                                    // Normal Get Prediction button with enhanced interactions
                                                    val normalPredictionInteractionSource = remember { MutableInteractionSource() }
                                                    val isNormalPredictionHovered by
                                                        normalPredictionInteractionSource.collectIsHoveredAsState()
                                                    val isNormalPredictionPressed by
                                                        normalPredictionInteractionSource.collectIsPressedAsState()
                                                    val isLoading by isLoadingPrediction.collectAsState()

                                                    val normalPredictionButtonScale by animateFloatAsState(
                                                        targetValue =
                                                            when {
                                                                isNormalPredictionPressed -> 0.96f
                                                                isNormalPredictionHovered -> 1.03f
                                                                else -> 1.0f
                                                            },
                                                        animationSpec = tween(durationMillis = 150),
                                                    )

                                                    Button(
                                                        onClick = {
                                                            if (!isLoading) {
                                                                applicationScope.launch {
                                                                    isLoadingPrediction.value = true
                                                                    try {
                                                                        println("Button Clicked: Get Prediction")
                                                                        val prediction = karlContainer?.getPrediction()
                                                                        predictionState.value = prediction
                                                                        println("Explicit Prediction Request: $prediction")
                                                                    } finally {
                                                                        isLoadingPrediction.value = false
                                                                    }
                                                                }
                                                            }
                                                        },
                                                        enabled = karlContainer != null && !isLoading,
                                                        modifier =
                                                            Modifier
                                                                .height(48.dp)
                                                                .width(180.dp)
                                                                .graphicsLayer(
                                                                    scaleX = normalPredictionButtonScale,
                                                                    scaleY = normalPredictionButtonScale,
                                                                )
                                                                .hoverable(normalPredictionInteractionSource)
                                                                .pointerHoverIcon(PointerIcon.Hand), // Larger prediction button
                                                        shape = RoundedCornerShape(24.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    when {
                                                                        isLoading -> MaterialTheme.colors.primary.copy(alpha = 0.7f)
                                                                        isNormalPredictionPressed ->
                                                                            MaterialTheme.colors.primary.copy(
                                                                                alpha = 0.8f,
                                                                            )
                                                                        isNormalPredictionHovered ->
                                                                            MaterialTheme.colors.primary.copy(
                                                                                alpha = 0.9f,
                                                                            )
                                                                        else -> MaterialTheme.colors.primary
                                                                    },
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        interactionSource = normalPredictionInteractionSource,
                                                    ) {
                                                        if (isLoading) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(16.dp),
                                                                    color = androidx.compose.ui.graphics.Color.White,
                                                                    strokeWidth = 2.dp,
                                                                )
                                                                Text(
                                                                    text = "Loading...",
                                                                    style =
                                                                        MaterialTheme.typography.body1.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        } else {
                                                            Text(
                                                                text = "🔮 Get Prediction",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Visual Feedback for Action Clicks
                                                    if (showActionFeedback && actionFeedbackMessage != null) {
                                                        Box(
                                                            modifier =
                                                                Modifier
                                                                    .fillMaxWidth()
                                                                    .padding(vertical = 8.dp),
                                                            contentAlignment = Alignment.Center,
                                                        ) {
                                                            Surface(
                                                                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                                                                shape = RoundedCornerShape(16.dp),
                                                                modifier = Modifier.padding(4.dp),
                                                            ) {
                                                                Text(
                                                                    text = actionFeedbackMessage!!,
                                                                    style =
                                                                        MaterialTheme.typography.caption.copy(
                                                                            fontWeight = FontWeight.Bold,
                                                                            color = MaterialTheme.colors.primary,
                                                                        ),
                                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                                )
                                                            }
                                                        }

                                                        // Auto-hide the feedback after 2 seconds
                                                        LaunchedEffect(showActionFeedback) {
                                                            if (showActionFeedback) {
                                                                delay(2000)
                                                                showActionFeedback = false
                                                                actionFeedbackMessage = null
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // When KARL is not ready yet - show loading state with animations
                            Box(
                                modifier = Modifier.fillMaxWidth().weight(1f), // Use weight instead of fixed height
                                contentAlignment = Alignment.Center,
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(24.dp),
                                ) {
                                    val loadingAlpha by animateFloatAsState(
                                        targetValue = if (karlContainer == null) 1.0f else 0.0f,
                                        animationSpec = tween(durationMillis = 1000),
                                    )

                                    Text(
                                        text = "🚀",
                                        style = MaterialTheme.typography.h2,
                                        modifier = Modifier.graphicsLayer(alpha = loadingAlpha),
                                    )

                                    Text(
                                        text = "Initializing KARL AI...",
                                        style =
                                            MaterialTheme.typography.h5.copy(
                                                fontWeight = FontWeight.SemiBold,
                                            ),
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                        modifier = Modifier.graphicsLayer(alpha = loadingAlpha),
                                    )

                                    Text(
                                        text = "Setting up learning engine and data sources",
                                        style = MaterialTheme.typography.body1,
                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.graphicsLayer(alpha = loadingAlpha),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
