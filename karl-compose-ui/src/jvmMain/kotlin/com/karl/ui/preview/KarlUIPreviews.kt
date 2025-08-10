/*
 * Copyright (c) 2025 Project KARL Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * UI Preview Definitions for KARL Compose Components
 *
 * This file contains Compose Desktop UI preview functions that enable visual development
 * and testing of KARL UI components within the IDE's preview system. These previews
 * facilitate rapid iteration and validation of component behavior across different states.
 *
 * Technical Architecture:
 * - **Desktop-Specific Previews**: Leverages `@Preview` annotations from Compose Desktop tooling
 * - **State Simulation**: Uses MutableStateFlow instances to simulate real-world data flows
 * - **Component Isolation**: Each preview function demonstrates a specific component state
 * - **Development Tooling**: Integrates with IDE preview systems for immediate visual feedback
 *
 * Design Pattern:
 * - **Preview Naming Convention**: `Preview{ComponentName}_{StateDescription}` format
 * - **State Mocking**: Creates realistic dummy data that represents actual usage scenarios
 * - **Progressive Complexity**: Previews range from simple states to complex configurations
 * - **Visual Validation**: Enables designers and developers to verify component appearance
 *
 * Integration Points:
 * - **karl-core**: Imports data models like `Prediction` for type-safe preview data
 * - **karl-ui**: References common UI components for preview rendering
 * - **Compose Desktop**: Utilizes desktop-specific preview infrastructure
 * - **Coroutines Flow**: Simulates reactive state management patterns
 */
package com.karl.ui.preview

import androidx.compose.desktop.ui.tooling.preview.Preview // Desktop-specific Preview
import androidx.compose.runtime.Composable
import com.karl.core.models.Prediction
import com.karl.ui.KarlContainerUI // Import your common composables
import com.karl.ui.KarlLearningProgressIndicator
import kotlinx.coroutines.flow.MutableStateFlow // Use StateFlow for preview state

// --- Previews for KarlLearningProgressIndicator ---

/**
 * Preview function for [KarlLearningProgressIndicator] displaying moderate learning progress.
 *
 * This preview demonstrates the progress indicator component at 50% completion,
 * representing a KARL AI system that has achieved moderate learning progress.
 * The component renders with default styling and intermediate progress visualization.
 *
 * **Visual Characteristics:**
 * - Progress bar filled to 50% capacity
 * - Default color scheme and animation behavior
 * - Standard size and proportions for typical usage
 *
 * **Use Cases:**
 * - Design validation for mid-stage learning scenarios
 * - Color and animation testing for intermediate states
 * - Layout verification in standard component configurations
 *
 * @see KarlLearningProgressIndicator The component being previewed
 */
@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlLearningProgressIndicator_Half() {
    KarlLearningProgressIndicator(progress = 0.5f)
}

/**
 * Preview function for [KarlLearningProgressIndicator] displaying complete learning progress.
 *
 * This preview demonstrates the progress indicator component at 100% completion with
 * a custom label, representing a fully mature KARL AI system. The component showcases
 * the visual appearance when learning objectives have been achieved.
 *
 * **Visual Characteristics:**
 * - Progress bar completely filled (100% capacity)
 * - Custom label text: "AI is Mature!"
 * - Success state styling and completion animations
 * - Full-width progress visualization
 *
 * **Technical Details:**
 * - Progress value: `1.0f` (representing 100% completion)
 * - Label parameter: Custom text override for completion messaging
 * - State representation: Final learning stage achievement
 *
 * **Use Cases:**
 * - Design validation for completion scenarios
 * - Success state visual verification
 * - Label customization testing and typography validation
 *
 * @see KarlLearningProgressIndicator The component being previewed
 */
@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlLearningProgressIndicator_Full() {
    KarlLearningProgressIndicator(progress = 1.0f, label = "AI is Mature!")
}

// --- Previews for KarlContainerUI ---

/**
 * Preview function for [KarlContainerUI] in a no-suggestion state.
 *
 * This preview demonstrates the container UI component when no AI predictions
 * are available, representing the initial state or scenarios where the KARL
 * system has insufficient data to generate meaningful suggestions.
 *
 * **State Configuration:**
 * - **Prediction State**: `null` (no active predictions available)
 * - **Learning Progress**: `0.1f` (10% - early learning stage)
 * - **UI Behavior**: Displays empty state or placeholder content
 *
 * **Technical Implementation:**
 * - Creates mock `MutableStateFlow<Prediction?>` with null value
 * - Simulates low learning progress through `MutableStateFlow(0.1f)`
 * - Demonstrates reactive state management patterns
 * - Tests component behavior with minimal data availability
 *
 * **Visual Characteristics:**
 * - Empty prediction display area
 * - Low progress indicator visualization
 * - Placeholder content or instructional messaging
 * - Initial loading state appearance
 *
 * **Use Cases:**
 * - First-time user experience validation
 * - Empty state design verification
 * - Progressive enhancement testing
 * - Error state fallback visualization
 *
 * @see KarlContainerUI The main container component being previewed
 * @see Prediction The data model used for AI suggestions
 */
@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlContainerUI_NoSuggestion() {
    // Create dummy StateFlows for this preview
    // Prediction should now be resolved via the corrected import
    val dummyPrediction = MutableStateFlow<Prediction?>(null)
    val dummyProgress = MutableStateFlow(0.1f) // Low progress

    KarlContainerUI(
        predictionState = dummyPrediction,
        learningProgressState = dummyProgress,
    )
}

/**
 * Preview function for [KarlContainerUI] displaying an active AI suggestion.
 *
 * This preview demonstrates the container UI component when the KARL AI system
 * has generated a high-confidence prediction, showcasing the full functionality
 * of the suggestion display and interaction capabilities.
 *
 * **State Configuration:**
 * - **Prediction State**: Active suggestion with high confidence
 *   - Suggestion: `"git commit"` (simulated development workflow command)
 *   - Confidence: `0.9f` (90% - high confidence prediction)
 *   - Type: `"next_command"` (categorized as workflow automation)
 * - **Learning Progress**: `0.75f` (75% - advanced learning stage)
 *
 * **Technical Implementation:**
 * - Creates mock `MutableStateFlow<Prediction>` with realistic prediction data
 * - Simulates advanced learning progress through `MutableStateFlow(0.75f)`
 * - Demonstrates prediction rendering and confidence visualization
 * - Tests component behavior with complete data structures
 *
 * **Visual Characteristics:**
 * - Active prediction display with suggestion text
 * - High confidence indicator (90% confidence visualization)
 * - Advanced progress indicator (75% completion)
 * - Interactive elements for prediction acceptance/rejection
 *
 * **Data Structure:**
 * ```kotlin
 * Prediction(
 *     suggestion = "git commit",    // User-facing suggestion text
 *     confidence = 0.9f,           // AI confidence score (0.0-1.0)
 *     type = "next_command"        // Prediction categorization
 * )
 * ```
 *
 * **Use Cases:**
 * - Active suggestion state validation
 * - High-confidence prediction visualization
 * - Interaction design verification
 * - Performance testing with realistic data
 * - User acceptance flow validation
 *
 * @see KarlContainerUI The main container component being previewed
 * @see Prediction The data model representing AI suggestions
 */
@Preview // This @Preview annotation should now resolve
@Composable
fun PreviewKarlContainerUI_WithSuggestion() {
    // Create dummy StateFlows for this preview
    // Prediction should now be resolved via the corrected import
    val dummyPrediction =
        MutableStateFlow(
            Prediction(content = "git commit", confidence = 0.9f, type = "next_command"),
        )
    val dummyProgress = MutableStateFlow(0.75f) // Higher progress

    KarlContainerUI(
        predictionState = dummyPrediction,
        learningProgressState = dummyProgress,
    )
}

// You can add more preview functions here with different states or configurations

/**
 * Extension point for additional preview functions with varied component states.
 *
 * This section serves as a designated area for expanding the preview suite
 * with additional component configurations, edge cases, and specialized states.
 *
 * **Potential Preview Extensions:**
 * - Error states and exception handling scenarios
 * - Loading states and transition animations
 * - Different confidence level visualizations
 * - Multi-language and accessibility configurations
 * - Theme variations (dark mode, high contrast)
 * - Component size and layout adaptations
 * - Advanced interaction patterns and user flows
 *
 * **Implementation Guidelines:**
 * - Follow the established naming convention: `Preview{Component}_{State}`
 * - Include comprehensive KDoc documentation for each preview
 * - Provide realistic mock data that represents actual usage
 * - Test edge cases and boundary conditions
 * - Document visual characteristics and use cases
 *
 * @see KarlContainerUI Primary container component
 * @see KarlLearningProgressIndicator Progress visualization component
 */
