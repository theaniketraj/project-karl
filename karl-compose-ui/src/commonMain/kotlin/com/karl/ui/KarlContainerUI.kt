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

// We'll need a way to expose the state from KarlContainer to Compose.
// KarlContainerImpl itself doesn't currently expose state flows,
// so for UI purposes, we might need a wrapper or pass specific state flows.
// For this example, let's assume we have access to relevant state flows.
// In a real app, you'd pass StateFlows from a ViewModel or similar state holder
// that observes the KarlContainer's state changes or prediction results.

// Placeholder State Flows (In a real app, these would come from your app's state management)
// Example: In your application's ViewModel:
// val karlContainer: KarlContainer = ... // Get your initialized container
// val predictionState: StateFlow<Prediction?> = MutableStateFlow(null) // Update this flow when KarlContainer.getPrediction() completes
// val learningProgressState: StateFlow<Float> = MutableStateFlow(0f) // Update this based on KarlContainer's internal progress state (if exposed)

/**
 * A composable that visually represents the KARL AI Container.
 * This is the UI 'sandbox' where the AI's presence and suggestions are shown.
 *
 * @param container The instance of the KarlContainer for this user/context.
 *                  (Note: Directly passing the container might not be ideal for complex state;
 *                   passing specific StateFlows is often better in Compose).
 * @param predictionState A StateFlow emitting the current prediction or suggestion from KARL.
 * @param learningProgressState A StateFlow emitting the current learning progress (0.0 to 1.0).
 * // Add more parameters for user controls (e.g., reset button callbacks)
 */
@Composable
fun KarlContainerUI(
    // Directly passing the container might be simplified for this example,
    // but consider passing StateFlows for better state management in Compose.
    // val container: KarlContainer, // Option 1: Pass container (less ideal for state observation)
    predictionState: StateFlow<Prediction?>, // Option 2: Pass state flows (more idiomatic Compose)
    learningProgressState: StateFlow<Float>,
    // Add callbacks for user actions, e.g.:
    // onResetClicked: () -> Unit,
    // onInstructionUpdated: (List<KarlInstruction>) -> Unit
) {
    // Collect state from flows
    val currentPrediction by predictionState.collectAsState()
    val currentProgress by learningProgressState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize() // Fill all available space
                .heightIn(min = 200.dp) // Ensure minimum height for content visibility
                .padding(16.dp), // Single padding, no border
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp), // Adjust spacing for better fit
    ) {
        Text(
            text = "KARL AI Container",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp, // Enlarged from 18sp
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f), // Theme-aware color
        )

        Text(
            text = "Status: Active",
            fontSize = 14.sp, // Enlarged from 12sp
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f), // Theme-aware color
        )

        // Display Learning Progress
        KarlLearningProgressIndicator(progress = currentProgress)

        // Display Current Suggestion with enhanced styling
        Spacer(modifier = Modifier.height(8.dp)) // Reduce space for better fit
        Text(
            text = "Current Suggestion:",
            fontSize = 16.sp, // Larger label
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f), // Theme-aware color
        )
        Text(
            text = currentPrediction?.suggestion ?: "No suggestion yet...",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp, // Larger suggestion text
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f), // Theme-aware color
        )
        // Optional: Display prediction confidence or type with enhanced styling
        currentPrediction?.let { prediction ->
            Text(
                text = "Confidence: ${"%.2f".format(prediction.confidence)}",
                fontSize = 14.sp, // Larger confidence text
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f), // Theme-aware color
            )
        }

        // Add UI elements for user controls (Reset, Instructions, etc.)
        // Example:
        // Button(onClick = onResetClicked) { Text("Reset KARL") }
        // Instructions UI component here...
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
