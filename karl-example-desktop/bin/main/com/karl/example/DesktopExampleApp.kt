package com.karl.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import api.KarlContainer
import api.LearningEngine
import com.karl.core.api.*
import com.karl.core.models.DataSource
import com.karl.core.models.DataStorage
import com.karl.core.models.InteractionData
import com.karl.core.models.Prediction
import com.karl.kldl.KLDLLearningEngine
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
    override fun observeInteractionData(
        onNewData: suspend (InteractionData) -> Unit,
        coroutineScope: CoroutineScope,
    ): Job {
        println("ExampleDataSource: Starting observation for $userId")
        return externalActionFlow
            .onEach { actionType ->
                println("ExampleDataSource: Observed action '$actionType'")
                val interaction =
                    InteractionData(
                        type = actionType,
                        details = mapOf("source" to "example_button"),
                        timestamp = System.currentTimeMillis(),
                        userId = userId,
                    )
                onNewData(interaction) // Pass data to KarlContainer
            }
            .launchIn(coroutineScope) // Use the provided scope
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
        val windowState = rememberWindowState(width = 900.dp, height = 800.dp)
        // Create a scope tied to the application lifecycle for cleanup
        val applicationScope = rememberCoroutineScope { SupervisorJob() + Dispatchers.Default }
        var karlContainer: KarlContainer? by remember {
            mutableStateOf(null)
        } // Hold the container instance

        // --- State for UI ---
        // StateFlow to hold the latest prediction for the UI
        val predictionState = remember { MutableStateFlow<Prediction?>(null) }
        // StateFlow for simulated learning progress
        val learningProgressState = remember { MutableStateFlow(0.0f) }
        // SharedFlow for triggering actions from buttons to the DataSource
        val actionFlow = remember { MutableSharedFlow<String>(extraBufferCapacity = 5) }
        // State for theme toggle (true = dark theme, false = light theme)
        var isDarkTheme by remember { mutableStateOf(true) }
        // State for enlarged sections
        var enlargedSection by remember {
            mutableStateOf<String?>(null)
        } // "insights" or "controls" or null

        // --- Lifecycle Management ---
        // Use LaunchedEffect for one-time setup/initialization when the app starts
        LaunchedEffect(Unit) {
            println("App LaunchedEffect: Setting up KARL...")
            val userId = "example-user-01" // Static user for the example

            // Instantiate KARL dependencies (replace with actual setup if needed)
            try {
                val learningEngine: LearningEngine = KLDLLearningEngine() // Simple instantiation
                val dataStorage: DataStorage =
                    InMemoryDataStorage() // Use in-memory storage for simplicity
                val dataSource: DataSource = ExampleDataSource(userId, actionFlow) // Use the actionFlow

                // Build the container
                val container =
                    Karl.forUser(userId)
                        .withLearningEngine(learningEngine)
                        .withDataStorage(dataStorage)
                        .withDataSource(dataSource)
                        .withCoroutineScope(applicationScope) // Use the app-level scope
                        .build()

                // Initialize (must be called!)
                container.initialize(
                    learningEngine,
                    dataStorage,
                    dataSource,
                    emptyList(),
                    applicationScope,
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
                                modifier = Modifier.padding(end = 8.dp),
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
                            ) {
                                Icon(
                                    imageVector = GitHubIcon,
                                    contentDescription = "Open GitHub Repository",
                                    tint = MaterialTheme.colors.onSurface,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        // Header Section - Fluent Design with Acrylic Effect
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            elevation = 0.dp, // Fluent: Minimal shadows
                            backgroundColor = if (isDarkTheme) {
                                androidx.compose.ui.graphics.Color(0x40FFFFFF) // Semi-transparent acrylic
                            } else {
                                androidx.compose.ui.graphics.Color(0x40000000) // Semi-transparent acrylic
                            },
                            shape = RoundedCornerShape(8.dp), // Fluent: Subtle rounded corners
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.Start, // Fluent: Left-aligned content
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Fluent: Icon emphasis
                                    Text(
                                        text = "🧠",
                                        style = MaterialTheme.typography.h2,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Project KARL",
                                            style = MaterialTheme.typography.h3.copy(
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Light, // Fluent: Light typography
                                                color = MaterialTheme.colors.onSurface,
                                            ),
                                        )
                                        Text(
                                            text = "Kotlin Adaptive Reasoning Learner",
                                            style = MaterialTheme.typography.body1.copy(
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
                                            ),
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                            modifier = Modifier.padding(top = 4.dp),
                                        )
                                    }
                                }
                            }
                        }

                        // Status Section - Fluent Design
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                            elevation = 0.dp, // Fluent: No shadows
                            backgroundColor = if (karlContainer != null) {
                                if (isDarkTheme) {
                                    androidx.compose.ui.graphics.Color(0xFF0E4B1A) // Dark green with transparency
                                } else {
                                    androidx.compose.ui.graphics.Color(0xFFE8F5E8) // Light green surface
                                }
                            } else {
                                if (isDarkTheme) {
                                    androidx.compose.ui.graphics.Color(0xFF4B0E0E) // Dark red with transparency
                                } else {
                                    androidx.compose.ui.graphics.Color(0xFFFFF3F3) // Light red surface
                                }
                            },
                            shape = RoundedCornerShape(8.dp), // Fluent: Consistent radius
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                // Fluent: Status indicator
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = if (karlContainer != null) {
                                                androidx.compose.ui.graphics.Color(0xFF10AC84)
                                            } else {
                                                androidx.compose.ui.graphics.Color(0xFFFF3838)
                                            },
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        )
                                        .padding(end = 16.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = if (karlContainer != null) {
                                            "KARL System Ready"
                                        } else {
                                            "System Initializing..."
                                        },
                                        style = MaterialTheme.typography.h6.copy(
                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, // Fluent: Medium weight
                                        ),
                                        color = MaterialTheme.colors.onSurface,
                                    )
                                    Text(
                                        text = if (karlContainer != null) {
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

                        // Main Content Section - Fluent Side by Side Layout
                        if (karlContainer != null) {
                            // Calculate weights based on enlarged section
                            val insightsWeight by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "insights" -> 0.75f
                                            "controls" -> 0.25f
                                            else -> 0.5f
                                        },
                                    animationSpec = tween(durationMillis = 300), // Fluent: Faster, more responsive
                                )
                            val controlsWeight = 1f - insightsWeight

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp), // Fluent: Tighter spacing
                            ) {
                                // AI Insights Section - Fluent Design
                                Card(
                                    modifier = Modifier.weight(insightsWeight).height(420.dp),
                                    elevation = 0.dp, // Fluent: No elevation
                                    backgroundColor = if (isDarkTheme) {
                                        androidx.compose.ui.graphics.Color(0x20FFFFFF) // Dark mode acrylic
                                    } else {
                                        androidx.compose.ui.graphics.Color(0xFFFAFAFA) // Light mode subtle surface
                                    },
                                    shape = RoundedCornerShape(8.dp), // Fluent: Consistent radius
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = if (enlargedSection == "insights") {
                                                    "📊 AI Insights"
                                                } else {
                                                    "📊 AI Insights"
                                                },
                                                style = MaterialTheme.typography.h6.copy(
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, // Fluent: Medium weight
                                                ),
                                                color = MaterialTheme.colors.onSurface,
                                            )
                                            // Fluent: Ghost button style
                                            IconButton(
                                                onClick = {
                                                    enlargedSection = if (enlargedSection == "insights") null else "insights"
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(
                                                        color = if (enlargedSection == "insights") {
                                                            MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                                        } else {
                                                            androidx.compose.ui.graphics.Color.Transparent
                                                        },
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                            ) {
                                                Icon(
                                                    imageVector = if (enlargedSection == "insights") {
                                                        Icons.Default.CloseFullscreen
                                                    } else {
                                                        Icons.Default.OpenInFull
                                                    },
                                                    contentDescription = if (enlargedSection == "insights") {
                                                        "Restore Size"
                                                    } else {
                                                        "Focus Mode"
                                                    },
                                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            KarlContainerUI(
                                                predictionState = predictionState,
                                                learningProgressState = learningProgressState,
                                            )
                                        }
                                    }
                                }

                                // Interaction Controls Section - Fluent Design
                                Card(
                                    modifier = Modifier.weight(controlsWeight).height(420.dp),
                                    elevation = 0.dp, // Fluent: No elevation
                                    backgroundColor = if (isDarkTheme) {
                                        androidx.compose.ui.graphics.Color(0x20FFFFFF) // Dark mode acrylic
                                    } else {
                                        androidx.compose.ui.graphics.Color(0xFFFAFAFA) // Light mode subtle surface
                                    },
                                    shape = RoundedCornerShape(8.dp), // Fluent: Consistent radius
                                ) {
                                    Column(
                                        modifier = Modifier.padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = if (enlargedSection == "controls") {
                                                    "🎮 Controls"
                                                } else {
                                                    "🎮 Controls"
                                                },
                                                style = MaterialTheme.typography.h6.copy(
                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, // Fluent: Medium weight
                                                ),
                                                color = MaterialTheme.colors.onSurface,
                                            )
                                            // Fluent: Ghost button style
                                            IconButton(
                                                onClick = {
                                                    enlargedSection = if (enlargedSection == "controls") null else "controls"
                                                },
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .background(
                                                        color = if (enlargedSection == "controls") {
                                                            MaterialTheme.colors.primary.copy(alpha = 0.1f)
                                                        } else {
                                                            androidx.compose.ui.graphics.Color.Transparent
                                                        },
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                            ) {
                                                Icon(
                                                    imageVector = if (enlargedSection == "controls") {
                                                        Icons.Default.CloseFullscreen
                                                    } else {
                                                        Icons.Default.OpenInFull
                                                    },
                                                    contentDescription = if (enlargedSection == "controls") {
                                                        "Restore Size"
                                                    } else {
                                                        "Focus Mode"
                                                    },
                                                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Responsive text based on section size
                                        if (enlargedSection != "insights") {
                                            Text(
                                                text = "Simulate user actions to train the AI model",
                                                style = MaterialTheme.typography.body2,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                                                modifier = Modifier.padding(bottom = 24.dp),
                                            )
                                        }

                                        // Fluent Design Buttons - Adaptive sizing
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(
                                                if (enlargedSection == "controls") 20.dp else 16.dp,
                                            ),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            if (enlargedSection == "insights") {
                                                // Compact mode - Fluent pill buttons
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                    // Fluent: Compact pill button
                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action A")
                                                                actionFlow.emit("action_type_A")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction = karlContainer?.getPrediction()
                                                                predictionState.value = prediction
                                                                println("Prediction after Action A: $prediction")
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier = Modifier.height(32.dp).width(80.dp),
                                                        shape = RoundedCornerShape(16.dp), // Fluent: Pill shape
                                                        colors = ButtonDefaults.buttonColors(
                                                            backgroundColor = if (isDarkTheme) {
                                                                androidx.compose.ui.graphics.Color(0xFF0E7B0E)
                                                            } else {
                                                                androidx.compose.ui.graphics.Color(0xFF0E7B0E)
                                                            },
                                                            contentColor = androidx.compose.ui.graphics.Color.White,
                                                        ),
                                                        elevation = ButtonDefaults.elevation(0.dp), // Fluent: No elevation
                                                    ) {
                                                        Text(
                                                            text = "A",
                                                            style = MaterialTheme.typography.caption.copy(
                                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                                            ),
                                                        )
                                                    }
                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action B")
                                                                actionFlow.emit("action_type_B")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Prediction after Action B: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(36.dp)
                                                                .width(100.dp),
                                                        shape = RoundedCornerShape(18.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color(
                                                                            0xFFFF9800,
                                                                        ),
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "⚡ B",
                                                            style =
                                                                MaterialTheme.typography.caption
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }
                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println(
                                                                    "Button Clicked: Get Prediction",
                                                                )
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Explicit Prediction Request: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(36.dp)
                                                                .width(100.dp),
                                                        shape = RoundedCornerShape(18.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    MaterialTheme.colors
                                                                        .primary,
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "🔮",
                                                            style =
                                                                MaterialTheme.typography.caption
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }
                                                }
                                            } else if (enlargedSection == "controls") {
                                                // Large buttons when controls is enlarged
                                                Row(
                                                    horizontalArrangement =
                                                        Arrangement.spacedBy(20.dp),
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action A")
                                                                actionFlow.emit("action_type_A")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Prediction after Action A: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(60.dp)
                                                                .width(150.dp),
                                                        shape = RoundedCornerShape(30.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color(
                                                                            0xFF4CAF50,
                                                                        ),
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "🔄 Action A",
                                                            style =
                                                                MaterialTheme.typography.h6
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }

                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action B")
                                                                actionFlow.emit("action_type_B")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Prediction after Action B: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(60.dp)
                                                                .width(150.dp),
                                                        shape = RoundedCornerShape(30.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color(
                                                                            0xFFFF9800,
                                                                        ),
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "⚡ Action B",
                                                            style =
                                                                MaterialTheme.typography.h6
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        applicationScope.launch {
                                                            println(
                                                                "Button Clicked: Get Prediction",
                                                            )
                                                            val prediction =
                                                                karlContainer?.getPrediction()
                                                            predictionState.value = prediction
                                                            println(
                                                                "Explicit Prediction Request: $prediction",
                                                            )
                                                        }
                                                    },
                                                    enabled = karlContainer != null,
                                                    modifier = Modifier.height(60.dp).width(250.dp),
                                                    shape = RoundedCornerShape(30.dp),
                                                    colors =
                                                        ButtonDefaults.buttonColors(
                                                            backgroundColor =
                                                                MaterialTheme.colors
                                                                    .primary,
                                                            contentColor =
                                                                androidx.compose.ui
                                                                    .graphics.Color
                                                                    .White,
                                                        ),
                                                ) {
                                                    Text(
                                                        text = "🔮 Get Prediction",
                                                        style =
                                                            MaterialTheme.typography.h6.copy(
                                                                fontWeight =
                                                                    androidx.compose.ui
                                                                        .text.font
                                                                        .FontWeight
                                                                        .Bold,
                                                            ),
                                                    )
                                                }
                                            } else {
                                                // Normal buttons when both sections are equal
                                                Row(
                                                    horizontalArrangement =
                                                        Arrangement.spacedBy(12.dp),
                                                ) {
                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action A")
                                                                actionFlow.emit("action_type_A")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Prediction after Action A: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(44.dp)
                                                                .width(120.dp),
                                                        shape = RoundedCornerShape(22.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color(
                                                                            0xFF4CAF50,
                                                                        ),
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "🔄 Action A",
                                                            style =
                                                                MaterialTheme.typography.caption
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }

                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Action B")
                                                                actionFlow.emit("action_type_B")
                                                                learningProgressState.update {
                                                                    (it + 0.05f).coerceAtMost(1.0f)
                                                                }
                                                                val prediction =
                                                                    karlContainer
                                                                        ?.getPrediction()
                                                                predictionState.value = prediction
                                                                println(
                                                                    "Prediction after Action B: $prediction",
                                                                )
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier =
                                                            Modifier.height(44.dp)
                                                                .width(120.dp),
                                                        shape = RoundedCornerShape(22.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color(
                                                                            0xFFFF9800,
                                                                        ),
                                                                contentColor =
                                                                    androidx.compose.ui
                                                                        .graphics
                                                                        .Color
                                                                        .White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "⚡ Action B",
                                                            style =
                                                                MaterialTheme.typography.caption
                                                                    .copy(
                                                                        fontWeight =
                                                                            androidx.compose
                                                                                .ui
                                                                                .text
                                                                                .font
                                                                                .FontWeight
                                                                                .Bold,
                                                                    ),
                                                        )
                                                    }
                                                }

                                                Button(
                                                    onClick = {
                                                        applicationScope.launch {
                                                            println(
                                                                "Button Clicked: Get Prediction",
                                                            )
                                                            val prediction =
                                                                karlContainer?.getPrediction()
                                                            predictionState.value = prediction
                                                            println(
                                                                "Explicit Prediction Request: $prediction",
                                                            )
                                                        }
                                                    },
                                                    enabled = karlContainer != null,
                                                    modifier = Modifier.height(44.dp).width(160.dp),
                                                    shape = RoundedCornerShape(22.dp),
                                                    colors =
                                                        ButtonDefaults.buttonColors(
                                                            backgroundColor =
                                                                MaterialTheme.colors
                                                                    .primary,
                                                            contentColor =
                                                                androidx.compose.ui
                                                                    .graphics.Color
                                                                    .White,
                                                        ),
                                                ) {
                                                    Text(
                                                        text = "🔮 Get Prediction",
                                                        style =
                                                            MaterialTheme.typography.caption
                                                                .copy(
                                                                    fontWeight =
                                                                        androidx.compose
                                                                            .ui
                                                                            .text
                                                                            .font
                                                                            .FontWeight
                                                                            .Bold,
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
                }
            }
        }
    }
