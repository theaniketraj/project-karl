package com.karl.example

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        val windowState = rememberWindowState() // Remove fixed size, let it be resizable
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

                            // Content fade animations for smooth transitions
                            val insightsAlpha by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "controls" -> 0.6f
                                            else -> 1.0f
                                        },
                                    animationSpec = tween(durationMillis = 400),
                                )
                            val controlsAlpha by
                                animateFloatAsState(
                                    targetValue =
                                        when (enlargedSection) {
                                            "insights" -> 0.6f
                                            else -> 1.0f
                                        },
                                    animationSpec = tween(durationMillis = 400),
                                )

                            Row(
                                modifier = Modifier.fillMaxWidth().weight(1f), // Use weight to fill available space
                                horizontalArrangement = Arrangement.spacedBy(12.dp), // Fluent: Tighter spacing
                            ) {
                                // AI Insights Section - Transparent with subtle animations
                                Box(
                                    modifier =
                                        Modifier
                                            .weight(insightsWeight)
                                            .fillMaxHeight() // Use available height instead of fixed height
                                            .graphicsLayer(alpha = insightsAlpha),
                                ) {
                                    // Transparent background with subtle border
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = MaterialTheme.colors.surface.copy(alpha = 0.05f),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
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
                                            Spacer(modifier = Modifier.height(24.dp)) // More spacing
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                KarlContainerUI(
                                                    predictionState = predictionState,
                                                    learningProgressState = learningProgressState,
                                                )
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
                                    // Transparent background with subtle border
                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = MaterialTheme.colors.surface.copy(alpha = 0.05f),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f),
                                                    shape = RoundedCornerShape(12.dp),
                                                )
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
                                                        // Fluent: Compact pill button with hover animation
                                                        val buttonAInteractionSource = remember { MutableInteractionSource() }
                                                        val isButtonAHovered by buttonAInteractionSource.collectIsHoveredAsState()
                                                        val buttonAScale by animateFloatAsState(
                                                            targetValue = if (isButtonAHovered) 1.05f else 1.0f,
                                                            animationSpec = tween(durationMillis = 150),
                                                        )

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
                                                                        if (isButtonAHovered) {
                                                                            androidx.compose.ui.graphics.Color(0xFF66BB6A)
                                                                        } else {
                                                                            androidx.compose.ui.graphics.Color(0xFF4CAF50)
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
                                                        val buttonBScale by animateFloatAsState(
                                                            targetValue = if (isButtonBHovered) 1.05f else 1.0f,
                                                            animationSpec = tween(durationMillis = 150),
                                                        )

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    println("Button Clicked: Action B")
                                                                    actionFlow.emit("action_type_B")
                                                                    learningProgressState.update {
                                                                        (it + 0.05f).coerceAtMost(1.0f)
                                                                    }
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
                                                                        if (isButtonBHovered) {
                                                                            androidx.compose.ui.graphics.Color(0xFFFFB74D)
                                                                        } else {
                                                                            androidx.compose.ui.graphics.Color(0xFFFF9800)
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

                                                        Button(
                                                            onClick = {
                                                                applicationScope.launch {
                                                                    println("Button Clicked: Get Prediction")
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Explicit Prediction Request: $prediction")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier.height(36.dp).width(100.dp),
                                                            shape = RoundedCornerShape(18.dp),
                                                            colors =
                                                                ButtonDefaults.buttonColors(
                                                                    backgroundColor = MaterialTheme.colors.primary,
                                                                    contentColor = androidx.compose.ui.graphics.Color.White,
                                                                ),
                                                        ) {
                                                            Text(
                                                                text = "🔮",
                                                                style =
                                                                    MaterialTheme.typography.caption.copy(
                                                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                    ),
                                                            )
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
                                                            modifier = Modifier.height(64.dp).width(160.dp), // Larger buttons
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
                                                                    println("Button Clicked: Action B")
                                                                    actionFlow.emit("action_type_B")
                                                                    learningProgressState.update {
                                                                        (it + 0.05f).coerceAtMost(1.0f)
                                                                    }
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action B: $prediction")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier.height(64.dp).width(160.dp),
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

                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Get Prediction")
                                                                val prediction = karlContainer?.getPrediction()
                                                                predictionState.value = prediction
                                                                println("Explicit Prediction Request: $prediction")
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier = Modifier.height(64.dp).width(280.dp), // Extra large prediction button
                                                        shape = RoundedCornerShape(32.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor = MaterialTheme.colors.primary,
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                    ) {
                                                        Text(
                                                            text = "🔮 Get Prediction",
                                                            style =
                                                                MaterialTheme.typography.h6.copy(
                                                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                                ),
                                                        )
                                                    }
                                                } else {
                                                    // Normal buttons when both sections are equal
                                                    Row(
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                    ) {
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
                                                            modifier = Modifier.height(48.dp).width(130.dp), // Larger normal buttons
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
                                                                    println("Button Clicked: Action B")
                                                                    actionFlow.emit("action_type_B")
                                                                    learningProgressState.update {
                                                                        (it + 0.05f).coerceAtMost(1.0f)
                                                                    }
                                                                    val prediction = karlContainer?.getPrediction()
                                                                    predictionState.value = prediction
                                                                    println("Prediction after Action B: $prediction")
                                                                }
                                                            },
                                                            enabled = karlContainer != null,
                                                            modifier = Modifier.height(48.dp).width(130.dp),
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

                                                    Button(
                                                        onClick = {
                                                            applicationScope.launch {
                                                                println("Button Clicked: Get Prediction")
                                                                val prediction = karlContainer?.getPrediction()
                                                                predictionState.value = prediction
                                                                println("Explicit Prediction Request: $prediction")
                                                            }
                                                        },
                                                        enabled = karlContainer != null,
                                                        modifier = Modifier.height(48.dp).width(180.dp), // Larger prediction button
                                                        shape = RoundedCornerShape(24.dp),
                                                        colors =
                                                            ButtonDefaults.buttonColors(
                                                                backgroundColor = MaterialTheme.colors.primary,
                                                                contentColor = androidx.compose.ui.graphics.Color.White,
                                                            ),
                                                    ) {
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
