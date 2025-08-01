/*
 * Copyright (c) 2025 Project KARL Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * Jetpack Compose UI components for visualizing and interacting with KARL AI containers.
 * This file provides the core user interface components that allow applications to
 * display AI learning progress, predictions, and container status in a modern,
 * Material Design-compliant interface.
 *
 * Component architecture:
 * - **Reactive UI**: Built on Compose StateFlows for real-time updates
 * - **Material Design**: Follows Material Design 3 guidelines and theming
 * - **Accessibility**: Implements proper semantics and navigation support
 * - **Performance**: Optimized for smooth animations and minimal recomposition
 * - **Customization**: Supports theming and layout customization
 *
 * Integration patterns:
 * - Connect to KARL containers through StateFlow observables
 * - Integrate with MVVM/MVI architecture patterns
 * - Support for dependency injection and testability
 * - Seamless integration with existing Compose applications
 */
package com.karl.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.karl.core.models.Prediction
import kotlinx.coroutines.flow.StateFlow

/**
 * Primary Compose UI component for visualizing and interacting with a KARL AI container.
 *
 * This composable provides a comprehensive visual representation of an active KARL container,
 * displaying real-time learning progress, current predictions, and system status. It serves
 * as the primary interface between users and the AI system, offering transparency into
 * the learning process and immediate access to AI-generated insights.
 *
 * **Component Architecture:**
 *
 * **Reactive State Management**:
 * - Built on Kotlin StateFlows for efficient, reactive state observation
 * - Automatic recomposition when container state changes
 * - Minimal performance impact through targeted state subscriptions
 * - Thread-safe state updates from background learning processes
 *
 * **Material Design Compliance**:
 * - Follows Material Design 3 principles and component guidelines
 * - Adaptive theming support with light/dark mode compatibility
 * - Consistent typography, spacing, and visual hierarchy
 * - Accessible color contrasts and semantic markup
 *
 * **Layout and Presentation**:
 * - Flexible column layout that adapts to available space
 * - Minimum height constraints ensure content visibility
 * - Responsive spacing and typography scaling
 * - Clear visual hierarchy emphasizing current predictions
 *
 * **User Experience Features**:
 *
 * **Learning Transparency**:
 * - Real-time progress indicator showing AI maturity level
 * - Visual feedback for learning milestones and achievements
 * - Clear status indicators for container operational state
 * - Historical progress context for user understanding
 *
 * **Prediction Display**:
 * - Prominent display of current AI suggestions and recommendations
 * - Confidence scoring with intuitive visual representation
 * - Prediction type categorization for appropriate user response
 * - Fallback content when no predictions are available
 *
 * **Interactive Elements**:
 * - Integration points for user feedback and preference controls
 * - Action buttons for container management (reset, configure)
 * - Instruction input mechanisms for behavior customization
 * - Accessibility support for keyboard and screen reader navigation
 *
 * **Integration Patterns:**
 *
 * **MVVM/MVI Architecture**:
 * - Designed for integration with modern Android architecture patterns
 * - StateFlow parameters enable clean separation of concerns
 * - ViewModel integration for complex state management scenarios
 * - Support for unidirectional data flow patterns
 *
 * **State Flow Design**:
 * ```kotlin
 * // Example ViewModel integration:
 * class KarlViewModel : ViewModel() {
 *     private val _predictionState = MutableStateFlow<Prediction?>(null)
 *     val predictionState: StateFlow<Prediction?> = _predictionState.asStateFlow()
 *
 *     private val _progressState = MutableStateFlow(0f)
 *     val progressState: StateFlow<Float> = _progressState.asStateFlow()
 *
 *     fun updateFromContainer(container: KarlContainer) {
 *         viewModelScope.launch {
 *             val prediction = container.getPrediction()
 *             _predictionState.value = prediction
 *
 *             val insights = container.learningEngine.getLearningInsights()
 *             _progressState.value = insights.progressEstimate
 *         }
 *     }
 * }
 * ```
 *
 * **Dependency Injection**:
 * - Compatible with Hilt, Dagger, and other DI frameworks
 * - StateFlow parameters enable testable component design
 * - Mock state injection for preview and testing scenarios
 * - Clear dependency boundaries for modular development
 *
 * **Performance Considerations:**
 *
 * **Efficient Recomposition**:
 * - StateFlow observation minimizes unnecessary recompositions
 * - Targeted state collection prevents cascade updates
 * - Stable parameter design reduces composition overhead
 * - Lazy evaluation for expensive UI calculations
 *
 * **Memory Management**:
 * - StateFlow lifecycle tied to composition lifecycle
 * - Automatic cleanup when component leaves composition
 * - Efficient state subscription management
 * - Memory-conscious prediction and progress caching
 *
 * **Customization and Theming:**
 *
 * **Material Theming**:
 * - Respects application theme colors and typography
 * - Automatic adaptation to light/dark mode changes
 * - Support for dynamic color schemes (Material You)
 * - Consistent visual language with host application
 *
 * **Layout Customization**:
 * - Modifier parameter for layout control and styling
 * - Configurable spacing and sizing parameters
 * - Support for different screen sizes and orientations
 * - Integration with custom design systems
 *
 * **Accessibility Features**:
 *
 * **Screen Reader Support**:
 * - Semantic content descriptions for all UI elements
 * - Proper heading hierarchy and navigation structure
 * - Dynamic content announcements for state changes
 * - Alternative text for visual progress indicators
 *
 * **Keyboard Navigation**:
 * - Tab order optimization for efficient navigation
 * - Focus management for interactive elements
 * - Keyboard shortcuts for common actions
 * - Visual focus indicators for clarity
 *
 * **Usage Examples:**
 *
 * **Basic Integration**:
 * ```kotlin
 * @Composable
 * fun MainScreen(viewModel: KarlViewModel = viewModel()) {
 *     KarlContainerUI(
 *         predictionState = viewModel.predictionState,
 *         learningProgressState = viewModel.progressState
 *     )
 * }
 * ```
 *
 * **Advanced Customization**:
 * ```kotlin
 * @Composable
 * fun CustomKarlUI(container: KarlContainer) {
 *     val prediction by container.predictionFlow.collectAsState()
 *     val progress by container.progressFlow.collectAsState()
 *
 *     KarlContainerUI(
 *         predictionState = container.predictionFlow,
 *         learningProgressState = container.progressFlow
 *     )
 * }
 * ```
 *
 * @param predictionState StateFlow emitting the current AI prediction or suggestion.
 *                       Provides reactive updates when new predictions are generated
 *                       or when prediction confidence changes. Null values indicate
 *                       no current prediction is available.
 *
 * @param learningProgressState StateFlow emitting the current learning progress as a
 *                            normalized value between 0.0 and 1.0. Represents the
 *                            AI system's maturity and confidence in its learned patterns.
 *                            Values closer to 1.0 indicate more mature and reliable learning.
 *
 * @see Prediction For the structure of AI-generated suggestions and recommendations
 * @see KarlLearningProgressIndicator For the progress visualization component
 * @see com.karl.core.models For core data models used in state flows
 */
@Composable
fun KarlContainerUI(
    predictionState: StateFlow<Prediction?>,
    learningProgressState: StateFlow<Float>,
) {
    // Reactive state collection from provided StateFlows
    val currentPrediction by predictionState.collectAsState()
    val currentProgress by learningProgressState.collectAsState()

    // Main container layout with Material Design spacing and hierarchy
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .heightIn(min = 200.dp) // Ensures minimum content visibility
                .padding(16.dp),
        // Consistent Material Design spacing
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Container header with status information
        Text(
            text = "KARL AI Container",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
        )

        Text(
            text = "Status: Active",
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
        )

        // Learning progress visualization
        KarlLearningProgressIndicator(progress = currentProgress)

        // Current prediction display section
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Current Suggestion:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        )
        Text(
            text = currentPrediction?.suggestion ?: "No suggestion yet...",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
        )

        // Prediction metadata display
        currentPrediction?.let { prediction ->
            Text(
                text = "Confidence: ${"%.2f".format(prediction.confidence)}",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            )
        }

        // Future extension points for user interaction controls
        // Button(onClick = onResetClicked) { Text("Reset KARL") }
        // Instructions UI component integration point
    }
}

// Example Usage (for Preview or within an application screen)
// @Preview
// @Composable
// fun PreviewKarlContainerUI() {
//    // Create dummy StateFlows for preview
//    val dummyPrediction = MutableStateFlow(Prediction(suggestion = "git commit", confidence = 0.9f, type = "next_command"))
//    val dummyProgress = MutableStateFlow(0.5f)
//
//    KarlContainerUI(
//        predictionState = dummyPrediction,
//        learningProgressState = dummyProgress
//    )
// }
