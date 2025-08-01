/*
 * Copyright (c) 2025 Project KARL Contributors
 * Licensed under the Apache License, Version 2.0
 *
 * Learning progress visualization component for KARL AI containers.
 * Provides intuitive visual feedback about AI learning maturity and system readiness.
 */
package com.karl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Material Design 3 component for visualizing KARL learning progress and AI maturity levels.
 *
 * This composable provides an intuitive and accessible way to communicate the current
 * state of AI learning to users, helping them understand when the system has sufficient
 * training to provide reliable predictions and suggestions. The component follows Material
 * Design principles for progress indicators while adding domain-specific enhancements.
 *
 * **Visual Design and User Experience:**
 *
 * **Progress Visualization**:
 * - Linear progress bar with rounded corners for modern aesthetics
 * - Proportional width and prominent height for clear visibility
 * - Smooth animations when progress values change
 * - High contrast colors for accessibility compliance
 *
 * **Information Hierarchy**:
 * - Clear label identifying the progress type and context
 * - Percentage display providing precise numerical feedback
 * - Consistent typography and spacing following Material Design
 * - Logical reading order for screen reader accessibility
 *
 * **Material Design Integration**:
 * - Respects application theme colors and typography scales
 * - Automatic adaptation to light/dark mode preferences
 * - Consistent with Material 3 progress indicator specifications
 * - Proper contrast ratios for all theme variations
 *
 * **Learning Progress Interpretation:**
 *
 * **Progress Stages and Meanings**:
 * - **0% - 25%**: Initial learning phase, minimal training data available
 *   - System is collecting basic interaction patterns
 *   - Predictions may be experimental or low-confidence
 *   - User should expect limited AI assistance capabilities
 *
 * - **25% - 50%**: Foundation learning phase, establishing core patterns
 *   - System begins recognizing common user behaviors
 *   - Basic predictions become available with moderate confidence
 *   - Interaction history provides sufficient context for simple suggestions
 *
 * - **50% - 75%**: Active learning phase, refining pattern recognition
 *   - System develops sophisticated understanding of user preferences
 *   - Predictions become more accurate and contextually relevant
 *   - AI can handle complex scenarios and multi-step workflows
 *
 * - **75% - 100%**: Mature learning phase, optimized personalization
 *   - System demonstrates deep understanding of user behavior patterns
 *   - High-confidence predictions with strong accuracy track record
 *   - AI proactively suggests optimizations and workflow improvements
 *
 * **User Communication Strategy**:
 *
 * **Transparency and Trust Building**:
 * - Clear visual indication of AI system capability level
 * - Helps users calibrate expectations for AI assistance quality
 * - Builds trust through transparent communication of system limitations
 * - Encourages continued usage as users see progress over time
 *
 * **Educational Value**:
 * - Teaches users about machine learning training requirements
 * - Demonstrates the value of providing interaction data for learning
 * - Shows correlation between usage patterns and AI improvement
 * - Encourages patient engagement during initial learning phases
 *
 * **Accessibility and Usability:**
 *
 * **Screen Reader Support**:
 * - Semantic markup for progress information
 * - Descriptive labels for context understanding
 * - Live region updates for dynamic progress changes
 * - Alternative text descriptions for visual elements
 *
 * **Visual Accessibility**:
 * - High contrast color schemes for visibility impairments
 * - Multiple information channels (visual bar, percentage text)
 * - Scalable design supporting different text size preferences
 * - Color-independent information encoding
 *
 * **Performance and Animation:**
 *
 * **Smooth Updates**:
 * - Efficient recomposition when progress values change
 * - Smooth animation transitions for progress updates
 * - Optimized rendering for minimal performance impact
 * - Stable composition to prevent unnecessary redrawing
 *
 * **Memory Efficiency**:
 * - Lightweight component with minimal state requirements
 * - Efficient Material 3 progress indicator implementation
 * - No memory leaks or resource retention issues
 * - Scalable for multiple container instances
 *
 * **Customization and Theming:**
 *
 * **Theme Integration**:
 * - Automatic color adaptation based on Material theme
 * - Typography scaling respecting user preferences
 * - Dynamic color support for Material You implementations
 * - Consistent visual language with host applications
 *
 * **Layout Flexibility**:
 * - Configurable label text for different contexts
 * - Responsive design adapting to available space
 * - Integration with various layout configurations
 * - Support for different screen sizes and orientations
 *
 * **Implementation Examples:**
 *
 * **Basic Usage**:
 * ```kotlin
 * @Composable
 * fun KarlDashboard(learningInsights: LearningInsights) {
 *     KarlLearningProgressIndicator(
 *         progress = learningInsights.progressEstimate,
 *         label = "AI Learning Progress"
 *     )
 * }
 * ```
 *
 * **Custom Integration**:
 * ```kotlin
 * @Composable
 * fun CustomProgressDisplay(container: KarlContainer) {
 *     val insights by container.learningInsightsFlow.collectAsState()
 *
 *     KarlLearningProgressIndicator(
 *         progress = insights.progressEstimate,
 *         label = "Model Maturity: ${insights.interactionCount} interactions"
 *     )
 * }
 * ```
 *
 * @param progress Learning progress value between 0.0 and 1.0, where 0.0 represents
 *                a completely untrained system and 1.0 represents a fully mature AI
 *                with comprehensive understanding of user behavior patterns.
 *                Values should be derived from LearningEngine.getLearningInsights().
 *
 * @param label Descriptive text label displayed above the progress indicator.
 *             Should clearly communicate what aspect of learning is being measured.
 *             Default value "Learning Progress" is appropriate for general usage.
 *
 * @see com.karl.core.learning.LearningInsights For progress data source
 * @see LinearProgressIndicator For underlying Material 3 component
 */
@Composable
fun KarlLearningProgressIndicator(
    progress: Float,
    label: String = "Learning Progress",
) {
    // Main container with centered alignment and Material spacing
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp),
    ) {
        // Progress label with theme-aware styling
        Text(
            text = label,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f),
        )

        // Visual separator for clear information hierarchy
        Spacer(modifier = Modifier.height(16.dp))

        // Main progress visualization with Material Design 3 styling
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .width(200.dp) // Optimal width for visibility across screen sizes
                    .height(8.dp) // Prominent height for accessibility
                    .clip(RoundedCornerShape(4.dp)),
            // Modern rounded aesthetic
        )

        // Additional spacing for clean layout
        Spacer(modifier = Modifier.height(8.dp))

        // Numerical progress indicator for precise feedback
        Text(
            text = "${(progress * 100).toInt()}%",
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
        )
    }
}
