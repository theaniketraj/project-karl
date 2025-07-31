package com.karl.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import com.karl.ui.KarlContainerUI
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.awt.Desktop
import java.net.URI

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

// --- 2. Main Application Entry Point ---
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
        // Panel 1: AI Insights data
        var systemStatus by remember { mutableStateOf("Learning") }
        var modelArchitecture by remember { mutableStateOf("Transformer-GPT") }
        var interactionsProcessed by remember { mutableStateOf(1247) }
        var interactionLog by remember { mutableStateOf(listOf("User clicked 'build'", "Analysis complete", "Model updated")) }
        
        // Panel 2: Prediction Details data
        var lastActionProcessed by remember { mutableStateOf("build project") }
        var inputFeatures by remember { mutableStateOf(42) }
        var confidenceScore by remember { mutableStateOf(0.87f) }
        var processingTime by remember { mutableStateOf(156) }
        var adaptivePredictions by remember { mutableStateOf(listOf("Next: run tests", "Likely: commit changes", "Alternative: debug")) }

        // --- Helper Functions for Dynamic Data ---
        fun simulateAction(action: String) {
            lastActionProcessed = action
            interactionsProcessed++
            interactionLog = (interactionLog + "Action: $action").takeLast(5)
            confidenceScore = kotlin.random.Random.nextFloat() * 0.25f + 0.7f // 0.7f to 0.95f
            processingTime = kotlin.random.Random.nextInt(100, 301) // 100 to 300
            systemStatus = when (kotlin.random.Random.nextInt(1, 4)) {
                1 -> "Processing"
                2 -> "Learning"
                else -> "Ready"
            }
            adaptivePredictions = when (action) {
                "git push" -> listOf("Next: wait for CI", "Monitor: build status", "Consider: review PR")
                "git pull" -> listOf("Next: resolve conflicts", "Check: new dependencies", "Update: local branch")
                "run tests" -> listOf("Next: fix failures", "Check: coverage report", "Consider: add tests")
                "build project" -> listOf("Next: run tests", "Check: warnings", "Deploy: staging")
                else -> listOf("Next: analyze context", "Monitor: system state", "Adapt: strategy")
            }
        }
        
        fun runScenario(scenario: String) {
            systemStatus = "Processing"
            interactionLog = (interactionLog + "Scenario: $scenario started").takeLast(5)
            when (scenario) {
                "Heavy Load Test" -> {
                    interactionsProcessed += 50
                    adaptivePredictions = listOf("Optimize: memory usage", "Scale: horizontally", "Monitor: performance")
                }
                "Data Migration" -> {
                    interactionsProcessed += 25
                    adaptivePredictions = listOf("Validate: data integrity", "Backup: before migration", "Test: rollback procedure")
                }
            }
        }

        // --- Lifecycle Management ---
        // Use LaunchedEffect for one-time setup/initialization when the app starts
        LaunchedEffect(Unit) {
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
                // Handle error (e.g., show error message in UI)
            }
        }

        // --- Helper function to update learning progress ---
        fun updateLearningProgress() {
            applicationScope.launch {
                learningEngine?.let { engine ->
                    try {
                        val insights = engine.getLearningInsights()
                        learningProgressState.update { insights.progressEstimate }
                        println(
                            "Progress: ${insights.interactionCount} interactions, ${(insights.progressEstimate * 100).toInt()}%",
                        )
                    } catch (e: Exception) {
                        println("Error getting learning insights: ${e.message}")
                    }
                }
            }
        }

        // --- Main Application Window ---
        Window(
            onCloseRequest = {
                println("App onCloseRequest: Cleaning up...")
                // Launch cleanup in the application scope
                applicationScope
                    .launch {
                        karlContainer?.saveState()?.join() // Save state and wait
                        karlContainer?.release() // Release resources
                        println("App onCloseRequest: KARL cleanup finished.")
                    }
                    .invokeOnCompletion { // Ensure application exits after cleanup
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
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Top Controls Row (Theme Toggle and GitHub Link)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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

                        // Header Section - Transparent with centered content
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                // Animated icon with subtle scale effect
                                val iconScale by animateFloatAsState(
                                    targetValue = if (karlContainer != null) 1.1f else 1.0f,
                                    animationSpec = tween(durationMillis = 1000),
                                )
                                Text(
                                    text = "🧠",
                                    style = MaterialTheme.typography.h1,
                                    modifier =
                                        Modifier
                                            .graphicsLayer(scaleX = iconScale, scaleY = iconScale)
                                            .padding(bottom = 8.dp),
                                )
                                Text(
                                    text = "Project KARL",
                                    style =
                                        MaterialTheme.typography.h2.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                                        ),
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                )
                                Text(
                                    text = "Kotlin Adaptive Reasoning Learner",
                                    style =
                                        MaterialTheme.typography.h6.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                                        ),
                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 8.dp),
                                )
                            }
                        }

                        // Status Section - Transparent with subtle glow effect
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp),
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
                                // Animated status dot with pulsing effect
                                val pulseScale by animateFloatAsState(
                                    targetValue = if (karlContainer != null) 1.0f else 1.2f,
                                    animationSpec = tween(durationMillis = 1500),
                                )

                                Box(
                                    modifier =
                                        Modifier
                                            .size(16.dp)
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

                                Spacer(modifier = Modifier.width(16.dp))

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
                                            MaterialTheme.typography.h6.copy(
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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
                                        style = MaterialTheme.typography.body2,
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
                                modifier = Modifier.fillMaxWidth().weight(1f),
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
                                                .padding(32.dp), // Enlarged padding for bigger content
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
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
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
                                                verticalArrangement = Arrangement.spacedBy(12.dp),
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

                                                Text(
                                                    text = "Interactions Processed: $interactionsProcessed",
                                                    style = MaterialTheme.typography.body2,
                                                    color = accentColor.copy(alpha = 0.9f),
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Text(
                                                    text = "Recent Interactions:",
                                                    style = MaterialTheme.typography.body2.copy(
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                                    ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                )

                                                LazyColumn(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .weight(1f)
                                                        .background(
                                                            color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                                            shape = MaterialTheme.shapes.small,
                                                        )
                                                        .padding(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                                ) {
                                                    items(interactionLog) { interaction ->
                                                        Text(
                                                            text = "• $interaction",
                                                            style = MaterialTheme.typography.caption,
                                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                        )
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
                                            .fillMaxHeight()
                                            .graphicsLayer(alpha = predictionAlpha),
                                ) {
                                    val predictionInteractionSource = remember { MutableInteractionSource() }
                                    val isPredictionHovered by predictionInteractionSource.collectIsHoveredAsState()

                                    val hoverScale by animateFloatAsState(
                                        targetValue = if (isPredictionHovered) 1.02f else 1.0f,
                                        animationSpec = tween(durationMillis = 200),
                                    )

                                    val accentColor = if (isDarkTheme) Color(0xFF81C784) else Color(0xFF388E3C)

                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .graphicsLayer(scaleX = hoverScale, scaleY = hoverScale)
                                                .background(
                                                    color = accentColor.copy(alpha = if (isPredictionHovered) 0.12f else 0.05f),
                                                    shape = MaterialTheme.shapes.medium,
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = accentColor.copy(alpha = if (isPredictionHovered) 0.2f else 0.08f),
                                                    shape = MaterialTheme.shapes.medium,
                                                )
                                                .hoverable(interactionSource = predictionInteractionSource)
                                                .padding(16.dp),
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            // Panel Title
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = "🔮 Prediction Details",
                                                    style = MaterialTheme.typography.h6.copy(
                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                    ),
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
                                                )
                                                IconButton(
                                                    onClick = {
                                                        enlargedSection = if (enlargedSection == "prediction") null else "prediction"
                                                    },
                                                    modifier = Modifier.size(24.dp),
                                                ) {
                                                    Icon(
                                                        imageVector = if (enlargedSection == "prediction") Icons.Default.CloseFullscreen else Icons.Default.OpenInFull,
                                                        contentDescription = if (enlargedSection == "prediction") "Minimize" else "Expand",
                                                        tint = accentColor.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(16.dp),
                                                    )
                                                }
                                            }

                                            Divider(color = accentColor.copy(alpha = 0.2f), thickness = 1.dp)

                                            // Input Context
                                            Text(
                                                text = "Input Context:",
                                                style = MaterialTheme.typography.body2.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                            )
                                            Text(
                                                text = "Last Action: \"$lastActionProcessed\"",
                                                style = MaterialTheme.typography.caption,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                            )
                                            Text(
                                                text = "Input Features: $inputFeatures",
                                                style = MaterialTheme.typography.caption,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Prediction Output
                                            Text(
                                                text = "Prediction Output:",
                                                style = MaterialTheme.typography.body2.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                            )
                                            Text(
                                                text = "Confidence: ${(confidenceScore * 100).toInt()}%",
                                                style = MaterialTheme.typography.caption,
                                                color = accentColor.copy(alpha = 0.9f),
                                            )
                                            Text(
                                                text = "Processing Time: ${processingTime}ms",
                                                style = MaterialTheme.typography.caption,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                text = "Adaptive Predictions:",
                                                style = MaterialTheme.typography.body2.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                            )

                                            LazyColumn(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .background(
                                                        color = MaterialTheme.colors.surface.copy(alpha = 0.3f),
                                                        shape = MaterialTheme.shapes.small,
                                                    )
                                                    .padding(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                            ) {
                                                items(adaptivePredictions) { prediction ->
                                                    Text(
                                                        text = "• $prediction",
                                                        style = MaterialTheme.typography.caption,
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                    )
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
                                                .padding(32.dp), // Enlarged padding for bigger content
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
                                                            "🎮 Controls"
                                                        } else {
                                                            "🎮 Controls"
                                                        },
                                                    style =
                                                        MaterialTheme.typography.h5.copy( // Larger text size
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
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
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action A: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action B: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
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
                                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action A: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action B: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Dynamic Action Simulation Buttons
                                                    Text(
                                                        text = "🚀 Simulate Actions",
                                                        style = MaterialTheme.typography.h6.copy(
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                                        ),
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    simulateAction("git push")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(140.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "📤 Git Push",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    simulateAction("run tests")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(140.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "🧪 Run Tests",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                    simulateAction("build project")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(140.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFFE91E63),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "🔨 Build",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    simulateAction("git pull")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(140.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF607D8B),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "📥 Git Pull",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }
                                                    }

                                                    // Scenario Testing Buttons
                                                    Text(
                                                        text = "🎯 Test Scenarios",
                                                        style = MaterialTheme.typography.h6.copy(
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                                                        ),
                                                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                    )

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    runScenario("Heavy Load Test")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(145.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "🔥 Heavy Load",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    runScenario("Data Migration")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(56.dp)
                                                                .width(145.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(28.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "📊 Migration",
                                                                style = MaterialTheme.typography.body1.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        } else {
                                                            Text(
                                                                text = "🔮 Get Prediction",
                                                                style =
                                                                    MaterialTheme.typography.h6.copy(
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                    val actionType = "action_type_A"
                                                                    println("UI: Emitting action '$actionType' to SharedFlow.")
                                                                    actionFlow.emit(actionType)
                                                                    updateLearningProgress()
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action A: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
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
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action B: $prediction")
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
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                    ),
                                                            )
                                                        }
                                                    }

                                                    // Dynamic Action Buttons (Compact)
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    simulateAction("git push")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(40.dp)
                                                                .width(90.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(20.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF2196F3),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "📤",
                                                                style = MaterialTheme.typography.caption.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    simulateAction("run tests")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(40.dp)
                                                                .width(90.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(20.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "🧪",
                                                                style = MaterialTheme.typography.caption.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }
                                                    }

                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    ) {
                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    runScenario("Heavy Load Test")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(40.dp)
                                                                .width(90.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(20.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFFFF5722),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "🔥",
                                                                style = MaterialTheme.typography.caption.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
                                                        }

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    runScenario("Data Migration")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier
                                                                .height(40.dp)
                                                                .width(90.dp)
                                                                .pointerHoverIcon(PointerIcon.Hand),
                                                            shape = RoundedCornerShape(20.dp),
                                                            colors = ButtonDefaults.buttonColors(
                                                                backgroundColor = androidx.compose.ui.graphics.Color(0xFF3F51B5),
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                        ) {
                                                            Text(
                                                                text = "📊",
                                                                style = MaterialTheme.typography.caption.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                            )
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
                                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                        ),
                                                                )
                                                            }
                                                        } else {
                                                            Text(
                                                                text = "🔮 Get Prediction",
                                                                style =
                                                                    MaterialTheme.typography.body1.copy(
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                    ),
                                                            )
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
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
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
